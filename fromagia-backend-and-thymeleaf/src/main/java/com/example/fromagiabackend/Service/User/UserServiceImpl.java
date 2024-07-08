package com.example.fromagiabackend.Service.User;

import com.example.fromagiabackend.Entity.User;
import com.example.fromagiabackend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository _userRepository){
        userRepository = _userRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public String findPasswordByUsername(String username){
        return userRepository.findPasswordByUsername(username);
    }

    @Override
    public void save(User user){
        userRepository.save(user);
    }

}