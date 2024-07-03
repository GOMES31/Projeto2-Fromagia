package com.example.fromagiabackend.Controller;

import com.example.fromagiabackend.Entity.UserAccount;
import com.example.fromagiabackend.Service.UserAccount.UserAccountService;
import com.example.fromagiabackend.Service.UserAccount.UserAccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserAccountController {

    private final UserAccountService userAccountService;

    @Autowired
    public UserAccountController(UserAccountService _userAccountService){
        userAccountService = _userAccountService;
    }

    @GetMapping("/all")
    public List<UserAccount> findAll(){
        return userAccountService.findAll();
    }

    @GetMapping("/username")
    public UserAccount findByUsername(String username){
        return userAccountService.findByUsername(username);
    }

    @PostMapping("/username")
    public UserAccount save(UserAccount userAccount){
        return userAccountService.save(userAccount);
    }

    @DeleteMapping("/username")
    public void deleteByUsername(String username){
        userAccountService.deleteByUsername(username);
    }
}
