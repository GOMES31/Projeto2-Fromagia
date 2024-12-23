package com.example.fromagiabackend.Service.User;

import com.example.fromagiabackend.Entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User findByUsername(String username);

    void save(User user);
}
