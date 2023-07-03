package com.ocena.qlsc.po.service;

import com.ocena.qlsc.common.constants.GlobalConstants;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.message.StatusCode;
import com.ocena.qlsc.common.message.StatusMessage;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseServiceImpl;
import com.ocena.qlsc.po.dto.PoDTO;
import com.ocena.qlsc.po.mapper.PoMapper;
import com.ocena.qlsc.po.model.Po;
import com.ocena.qlsc.po.repository.PoRepository;
import com.ocena.qlsc.podetail.enums.ExportPartner;
import com.ocena.qlsc.podetail.enums.KSCVT;
import com.ocena.qlsc.podetail.enums.RepairStatus;
import com.ocena.qlsc.podetail.model.PoDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PoService extends BaseServiceImpl<Po, PoDTO> implements IPoService {

    @Autowired
    PoRepository poRepository;

    @Autowired
    PoMapper poMapper;

    @Override
    protected BaseRepository<Po> getBaseRepository() {
        return poRepository;
    }

    @Override
    protected BaseMapper<Po, PoDTO> getBaseMapper() {
        return poMapper;
    }

    @Override
    protected Function<String, Optional<Po>> getFindByFunction() {
        return poRepository::findByPoNumber;
    }

    @Override
    protected Page<Po> getPageResults(SearchKeywordDto searchKeywordDto, Pageable pageable) {
        return null;
    }

    @Override
    protected List<Po> getListSearchResults(String keyword) {
        return null;
    }

    @Override
    public List<String> validationRequest(Object object) {
        return super.validationRequest(object);
    }

    /**
     * Validates the Po request
     *
     * @param poDTO The PoDTO object containing the Po data
     * @param isUpdate A boolean value indicating if it's an update operation
     * @param key A string representing the key value
     * @return A DataResponse object containing the validated PoDTO
     */
    @Override
    public DataResponse<PoDTO> validationPoRequest(PoDTO poDTO, boolean isUpdate, String key) {
        // get list error and Po by PoNumber
        List<String> result = validationRequest(poDTO);
        if (result != null || (poDTO.getBeginAt() > poDTO.getEndAt()))
            return ResponseMapper.toDataResponse(result, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);

        Optional<Po> newPo = poRepository.findByPoNumber(poDTO.getPoNumber());
        Optional<Po> poOld = poRepository.findByPoNumber(key);

        // get Current Time User can update within the first 15 minutes
        Long currentTime = System.currentTimeMillis();

        if (poOld.isPresent() && poOld.get().getCreated() + GlobalConstants.updateTimePO < currentTime) {
            return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_MAP, "YOU CAN ONLY UPDATE WITHIN THE FIRST 15 MINUTES");
        }

        // Check Po
        if (newPo.isPresent()){
            if (isUpdate){
                if (!poOld.get().getPoNumber().equals(poDTO.getPoNumber())) {
                    return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_MAP, "PO NUMBER ALREADY EXISTS");
                }
            } else {
                return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_MAP, "PO NUMBER ALREADY EXISTS");
            }
        }
        return null;
    }

    /**
     * Groups the PoDetail objects in the given list by a Short property extracted by the given function,
     * and returns a map of the counts of objects in each group, mapped to their corresponding Enum values
     * @param list the list of PoDetail objects to group
     * @param propertyGetter the function to extract the Short property from each PoDetail object
     * @param enums the Enum class to use for mapping the counts to Enum values
     * @param values the Short values to use for mapping the counts to Enum values or a special value
     * @return a map of the counts of objects in each group, mapped to their corresponding Enum values or special value
     */
    private <E extends Enum<E>> Map<String, Long> getCountsByProperty(List<PoDetail> list, Function<PoDetail, Short> propertyGetter, E enums, Short... values) {
        // Group the PoDetail objects by the Short property extracted by the given function
        Map<Short, Long> countsByProperty = list.stream()
                .collect(Collectors.groupingBy(detail -> propertyGetter.apply(detail) == null
                        ? (short) -1 : propertyGetter.apply(detail).shortValue(), Collectors.counting()));

        // Create a new map to store the counts mapped to their corresponding Enum values or special value
        Map<String, Long> result = new HashMap<>();
        E[] enumConstant = (E[]) enums.getClass().getEnumConstants();

        for (Short value: values) {
            if(value == -1) {
                result.put("CHUA_CAP_NHAT", countsByProperty.getOrDefault(value, 0L));
            } else {
                result.put(enumConstant[value].name(), countsByProperty.getOrDefault(value, 0L));
            }
        }
        return result;
    }


    /**
     * Gets various statistics on the PoDetail objects associated with the Po with the given poNumber,
     * @param poNumber the poNumber of the Po to get statistics for
     * @return a DataResponse containing the statistics and a status code and message
     */
    public DataResponse<HashMap<String, HashMap<String, Integer>>> getStatisticsByPoNumber(String poNumber) {
        // Check if a Po object with the given poNumber exists in the repository
        Optional<Po> isExistPO = poRepository.findByPoNumber(poNumber);

        // Create a new map to store the results of the statistics
        HashMap<String, Map<String, Long>> resultsMap = new HashMap<>();
        List<PoDetail> listPoDetail = poRepository.getPoDetailsByPoNumber(poNumber);
        if(isExistPO.isPresent()) {
            Po po = isExistPO.get();
            // Add the total quantity of the Po and the number of PoDetail objects associated with it to the results map
            resultsMap.put("TONG_SO_LUONG", new HashMap<>(){{
                put("TONG", (long) po.getQuantity());
                put("SO_LUONG_IMPORT", (long) listPoDetail.size());
            }});

            // Get the counts of PoDetail objects with a certain RepairStatus property, and add it to the results map
            RepairStatus repairStatus = RepairStatus.SC_XONG;
            resultsMap.put("TRANG_THAI_SC", getCountsByProperty(listPoDetail, PoDetail::getRepairStatus, repairStatus, (short) 0, (short) 1, (short) 2, (short) -1));

            // Get the counts of PoDetail objects with a certain ExportPartner property, and add it to the results map
            ExportPartner exportPartner = ExportPartner.DA_XUAT_KHO;
            resultsMap.put("XUAT_KHO", getCountsByProperty(listPoDetail, PoDetail::getExportPartner, exportPartner, (short) 0, (short) 1, (short) -1));

            // Get the counts of PoDetail objects with a certain KSCVT property, and add it to the results map
            KSCVT kscvt = KSCVT.PASS;
            resultsMap.put("KSC_VT", getCountsByProperty(listPoDetail, PoDetail::getKcsVT, kscvt, (short) 0, (short) 1, (short) -1));

            // Get the count of PoDetail objects with a non-null WarrantyPeriod property, and add it to the results map
            long count = listPoDetail.stream().filter(poDetail -> poDetail.getWarrantyPeriod() != null).count();
            resultsMap.put("BAO_HANH", new HashMap<>() {{
                put("DA_CAP_NHAT", count);
                put("CHUA_CAP_NHAT", listPoDetail.size() - count);
            }});
            return ResponseMapper.toDataResponse(resultsMap, StatusCode.REQUEST_SUCCESS, StatusMessage.REQUEST_SUCCESS);

        }
        return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
    }
}
