package com.example.fromagiabackend.Service.Order;

import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderService {

    void save(Order order);

    String generateOrderCode();

    Optional<Order> findById(Integer id);

    void rejectOrder(Order order);

    void acceptOrder(Order order);
}
