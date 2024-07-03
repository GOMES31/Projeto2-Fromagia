package com.example.fromagiabackend.Service.UserAccount;

import com.example.fromagiabackend.Entity.UserAccount;

import java.util.List;

public interface UserAccountService {

    List<UserAccount> findAll();

    UserAccount findByUsername(String username);

    UserAccount save(UserAccount userAccount);

    void deleteByUsername(String username);
}
