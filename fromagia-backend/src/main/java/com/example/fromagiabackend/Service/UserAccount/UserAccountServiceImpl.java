package com.example.fromagiabackend.Service.UserAccount;


import com.example.fromagiabackend.Entity.UserAccount;
import com.example.fromagiabackend.Repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAccountServiceImpl implements UserAccountService {


    private final UserAccountRepository userAccountRepository;

    @Autowired
    public UserAccountServiceImpl(UserAccountRepository _userAccountRepository){
        userAccountRepository = _userAccountRepository;
    }
    @Override
    public List<UserAccount> findAll(){
        return userAccountRepository.findAll();
    }

    @Override
    public UserAccount findByUsername(String username){
        return userAccountRepository.findByUsername(username);
    }

    @Override
    public UserAccount save(UserAccount userAccount){
        return userAccountRepository.save(userAccount);
    }

    @Override
    public void deleteByUsername(String username){
        userAccountRepository.deleteByUsername(username);
    }
}
