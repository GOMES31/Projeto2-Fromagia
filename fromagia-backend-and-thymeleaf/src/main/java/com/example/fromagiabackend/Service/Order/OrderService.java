package com.example.fromagiabackend.Service.Order;

import com.example.fromagiabackend.Entity.Order;

import java.util.List;

public interface OrderService {

    void save(Order order);

    String generateOrderCode();
}
