package com.ocena.qlsc.service;

import com.ocena.qlsc.model.User;

import java.util.List;

public interface IUserService {
    User registerUser(User user);

    User update(String id, User user);

    User getUserById(String userId);

    List<User> getAll();

    boolean delete(User user);
}
