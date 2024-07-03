package com.example.fromagiabackend.Repository;

import com.example.fromagiabackend.Entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount,Integer> {

    UserAccount findByUsername(String username);

    void deleteByUsername(String username);
}
