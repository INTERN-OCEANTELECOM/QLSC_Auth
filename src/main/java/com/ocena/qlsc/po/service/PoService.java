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
        //get list error and Po by PoNumber
        List<String> result = validationRequest(poDTO);
        if (result != null || (poDTO.getBeginAt() > poDTO.getEndAt()))
            return ResponseMapper.toDataResponse(result, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_FOUND);

        Optional<Po> newPo = poRepository.findByPoNumber(poDTO.getPoNumber());
        Optional<Po> poOld = poRepository.findByPoNumber(key);

        // get Current Time User can update within the first 15 minutes
        Long currentTime = System.currentTimeMillis();

        if (poOld.isPresent() && poOld.get().getCreated() + GlobalConstants.updateTimePO < currentTime) {
            return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_MAP, "YOU CAN ONLY UPDATE WITHIN THE FIRST 15 MINUTES");
        }

        //Check Po
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
     * Calculates the counts of PoDetail objects by property.
     *
     * @param list           The list of PoDetail objects.
     * @param propertyGetter The function to extract the Short property from PoDetail.
     * @param enums          The enum representing the possible values of the property.
     * @param values         The Short values to count.
     * @return A map containing the counts of PoDetail objects by property value.
     */
    private <E extends Enum<E>> Map<String, Long> getCountsByProperty(List<PoDetail> list, Function<PoDetail, Short> propertyGetter, E enums, Short... values) {
        Map<Short, Long> countsByProperty = list.stream()
                .collect(Collectors.groupingBy(detail -> propertyGetter.apply(detail) == null
                        ? (short) -1 : propertyGetter.apply(detail).shortValue(), Collectors.counting()));

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
     * Retrieves statistics by PoNumber.
     *
     * @param poNumber The PoNumber used for retrieving statistics.
     * @return A DataResponse containing a HashMap with statistical information.
     */
    public DataResponse<HashMap<String, HashMap<String, Integer>>> getStatisticsByPoNumber(String poNumber) {
        Optional<Po> isExistPO = poRepository.findByPoNumber(poNumber);
        HashMap<String, Map<String, Long>> resultsMap = new HashMap<>();
        List<PoDetail> listPoDetail = poRepository.getPoDetailsByPoNumber(poNumber);
        if(isExistPO.isPresent()) {
            Po po = isExistPO.get();
            // Tong so luong
            resultsMap.put("TONG_SO_LUONG", new HashMap<>(){{
                put("TONG", (long) po.getQuantity());
                put("SO_LUONG_IMPORT", (long) listPoDetail.size());
            }});

            RepairStatus repairStatus = RepairStatus.SC_XONG;
            resultsMap.put("TRANG_THAI_SC", getCountsByProperty(listPoDetail, PoDetail::getRepairStatus, repairStatus, (short) 0, (short) 1, (short) 2, (short) -1));

            ExportPartner exportPartner = ExportPartner.DA_XUAT_KHO;
            resultsMap.put("XUAT_KHO", getCountsByProperty(listPoDetail, PoDetail::getExportPartner, exportPartner, (short) 0, (short) 1, (short) -1));

            KSCVT kscvt = KSCVT.PASS;
            resultsMap.put("KSC_VT", getCountsByProperty(listPoDetail, PoDetail::getKcsVT, kscvt, (short) 0, (short) 1, (short) -1));

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
