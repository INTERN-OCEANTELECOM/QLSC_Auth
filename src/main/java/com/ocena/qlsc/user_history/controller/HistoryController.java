package com.ocena.qlsc.user_history.controller;

import com.ocena.qlsc.common.controller.BaseApiImpl;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.service.BaseService;
import com.ocena.qlsc.user_history.dto.HistoryDTO;
import com.ocena.qlsc.user_history.model.History;
import com.ocena.qlsc.user_history.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/history")
public class HistoryController {
    @Autowired
    HistoryService historyService;
    @GetMapping("/get-all")
    public ListResponse<HistoryDTO> getAll(){
        return historyService.getAll();
    }
}
