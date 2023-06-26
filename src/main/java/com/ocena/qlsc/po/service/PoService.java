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
import com.ocena.qlsc.po.model.PoMapper;
import com.ocena.qlsc.po.model.Po;
import com.ocena.qlsc.po.repository.PoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    public DataResponse<Po> validationPoRequest(PoDTO poDTO, boolean isUpdate, String id) {
        //get list error and Po by PoNumber
        List<String> result = validationRequest(poDTO);
        if (result != null || (poDTO.getBeginAt() > poDTO.getEndAt()))
            return ResponseMapper.toDataResponse(result, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_FOUND);

        Optional<Po> newPo = poRepository.findByPoNumber(poDTO.getPoNumber());
        if (newPo.isPresent()){
            if (isUpdate){
                //Check PO
                Optional<Po> poOld = poRepository.findByPoNumber(id);

                // get Current Time
                Long currentTime = System.currentTimeMillis();

                if (poOld.get().getCreated() + GlobalConstants.updateTimePO < currentTime) {
                    return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_MAP, "YOU CAN ONLY UPDATE WITHIN THE FIRST 5 MINUTES");
                }

                if (!poOld.get().getPoNumber().equals(poDTO.getPoNumber())) {
                    return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_MAP, "PO NUMBER ALREADY EXISTS");
                }
            } else {
                return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_MAP, "PO NUMBER ALREADY EXISTS");
            }
        }

        return null;
    }
}
