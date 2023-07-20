package com.ocena.qlsc.user_history.service;

import com.ocena.qlsc.user_history.enums.Action;
import com.ocena.qlsc.user_history.enums.Object;
import com.ocena.qlsc.user_history.model.History;
import com.ocena.qlsc.user_history.model.SpecificationDesc;
import com.ocena.qlsc.user_history.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {
    @Autowired
    HistoryRepository historyRepository;

    public void saveHistory(String action, String object, String specification) {
        if(specification != "") {
            History history = new History();
            history.setAction(action);
            history.setObject(object);
            history.setSpecification(specification);
            historyRepository.save(history);
        }
    }

//    private History setSpecification()
}
