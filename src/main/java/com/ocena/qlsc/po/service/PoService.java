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

    @Override
    public DataResponse<PoDTO> validationPoRequest(PoDTO poDTO, boolean isUpdate, String key) {
        //get list error and Po by PoNumber
        List<String> result = validationRequest(poDTO);
        if (result != null || (poDTO.getBeginAt() > poDTO.getEndAt()))
            return ResponseMapper.toDataResponse(result, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_FOUND);

        Optional<Po> newPo = poRepository.findByPoNumber(poDTO.getPoNumber());
        Optional<Po> poOld = poRepository.findByPoNumber(key);

        // get Current Time
        Long currentTime = System.currentTimeMillis();

        if (poOld.isPresent() && poOld.get().getCreated() + GlobalConstants.updateTimePO < currentTime) {
            return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_MAP, "YOU CAN ONLY UPDATE WITHIN THE FIRST 5 MINUTES");
        }

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

    private <E extends Enum<E>> Map<String, Long> getCountsByProperty(List<PoDetail> list, Function<PoDetail, Short> propertyGetter, E enums, Short... values) {
        Map<Short, Long> countsByProperty = list.stream()
                .collect(Collectors.groupingBy(detail -> propertyGetter.apply(detail) == null
                        ? (short) -1 : propertyGetter.apply(detail).shortValue(), Collectors.counting()));

        Map<String, Long> result = new HashMap<>();
        E[] enumConstant = (E[]) enums.getClass().getEnumConstants();

        for (Short value: values) {
            if(value == -1) {
                result.put("Chưa cập nhật", countsByProperty.getOrDefault(value, 0L));
            } else {
                result.put(enumConstant[value].name(), countsByProperty.getOrDefault(value, 0L));
            }
        }
        return result;
    }

    public DataResponse<HashMap<String, HashMap<String, Integer>>> getStatisticsByPoNumber(String poNumber) {
        Optional<Po> isExistPO = poRepository.findByPoNumber(poNumber);
        HashMap<String, Map<String, Long>> resultsMap = new HashMap<>();
        List<PoDetail> listPoDetail = poRepository.getPoDetailsByPoNumber(poNumber);
        if(isExistPO.isPresent()) {
            Po po = isExistPO.get();
            // Tong so luong
            resultsMap.put("Tổng số lượng", new HashMap<>(){{
                put("Tổng số lượng trong PO", (long) po.getQuantity());
                put("Tổng số lượng đã import", (long) listPoDetail.size());
            }});

            RepairStatus repairStatus = RepairStatus.SC_XONG;
            resultsMap.put("Trạng thái sản xuất", getCountsByProperty(listPoDetail, PoDetail::getRepairStatus, repairStatus, (short) 0, (short) 1, (short) 2, (short) -1));

            ExportPartner exportPartner = ExportPartner.XUAT_KHO;
            resultsMap.put("Xuất kho", getCountsByProperty(listPoDetail, PoDetail::getExportPartner, exportPartner, (short) 0, (short) 1, (short) -1));

            KSCVT kscvt = KSCVT.PASS;
            resultsMap.put("KSC VT", getCountsByProperty(listPoDetail, PoDetail::getKcsVT, kscvt, (short) 0, (short) 1, (short) -1));

            long count = listPoDetail.stream().filter(poDetail -> poDetail.getWarrantyPeriod() != null).count();
            resultsMap.put("Bảo Hành", new HashMap<>() {{
                put("Đã cập nhật", count);
                put("Chưa cập nhật", listPoDetail.size() - count);
            }});
            return ResponseMapper.toDataResponse(resultsMap, StatusCode.REQUEST_SUCCESS, StatusMessage.REQUEST_SUCCESS);

        }
        return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
    }
}
