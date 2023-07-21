package com.ocena.qlsc.user_history.service;

import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.user_history.dto.HistoryDTO;
import com.ocena.qlsc.user_history.mapper.HistoryMapper;
import com.ocena.qlsc.user_history.enums.Action;
import com.ocena.qlsc.user_history.model.History;
import com.ocena.qlsc.user_history.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HistoryService {
    @Autowired
    HistoryRepository historyRepository;

    @Autowired
    HistoryMapper historyMapper;
    public void writeHistory(History history){
        historyRepository.save(history);
    }

    public ListResponse<HistoryDTO> getAll(){
        List<History> historyList = historyRepository.findAll();
        List<HistoryDTO> historyDTOList = historyList.stream()
                .map(history -> historyMapper.convertTo(history, HistoryDTO.class)).collect(Collectors.toList());

        return ResponseMapper.toListResponseSuccess(historyDTOList);
    }

    public void saveHistory(String action, String object, String specification, String email) {
        if(action.equals(Action.DELETE.getValue()) ||
                action.equals(Action.RESET_PASSWORD.getValue()) ||
                !specification.equals("") ) {
            History history = new History();
            history.setAction(action);
            history.setObject(object);
            history.setSpecification(specification);
            history.setEmail(email);
            historyRepository.save(history);
        }
    }
}
