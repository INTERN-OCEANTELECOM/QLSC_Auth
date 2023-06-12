package com.ocena.qlsc.service;

import com.ocena.qlsc.model.User;
import com.ocena.qlsc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService{

    @Autowired
    UserRepository userRepository;

    @Override
    public User registerUser(User user) {
        return null;
    }

    @Override
    public User update(String id, User user) {
        return null;
    }

    @Override
    public User getUserById(String userId) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public boolean delete(User user) {
        return false;
    }
}
