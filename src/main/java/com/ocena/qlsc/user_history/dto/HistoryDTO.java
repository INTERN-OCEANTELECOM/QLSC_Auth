package com.ocena.qlsc.user_history.dto;

import com.ocena.qlsc.user.dto.UserDTO;
import com.ocena.qlsc.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

public class HistoryDTO {

    private String id;
    private UserDTO userDTO;

    private Long created;

    private String object;

    private String action;

    private String specification;
}
