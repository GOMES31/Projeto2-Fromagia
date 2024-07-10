package com.example.fromagiabackend.Service.Order;

import com.example.fromagiabackend.Entity.Helpers.OrderCodeGenerator;
import com.example.fromagiabackend.Entity.Order;
import com.example.fromagiabackend.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository _orderRepository){
        orderRepository = _orderRepository;
    }

    @Override
    public void save(Order order){
        order.calculateTotalAmount();
        orderRepository.save(order);
    }
    @Override
    public String generateOrderCode() {
        Order lastOrder = orderRepository.findTopByOrderByIdDesc();

        if(lastOrder == null) {
            return "AAAA";
        }
        else {
            return OrderCodeGenerator.generateNextOrderCode(lastOrder.getOrderCode());
        }
    }
}
