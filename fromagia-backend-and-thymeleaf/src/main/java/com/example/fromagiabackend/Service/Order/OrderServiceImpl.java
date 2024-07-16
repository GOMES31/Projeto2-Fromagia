package com.example.fromagiabackend.Service.Order;

import com.example.fromagiabackend.Entity.Enums.OrderState;
import com.example.fromagiabackend.Entity.Helpers.OrderCodeGenerator;
import com.example.fromagiabackend.Entity.Order;
import com.example.fromagiabackend.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository _orderRepository){
        orderRepository = _orderRepository;
    }

    @Override
    public void save(Order order){
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

    @Override
    public Optional<Order> findById(Integer id) {
        return orderRepository.findById(id);
    }

    @Override
    public void rejectOrder(Order order){
        order.setReceivedDate(LocalDateTime.now());
        order.setOrderState(OrderState.REJECTED);
        orderRepository.save(order);
    }

    @Override
    public void acceptOrder(Order order){
        order.setReceivedDate(LocalDateTime.now());
        order.setOrderState(OrderState.ACCEPTED);
        orderRepository.save(order);
    }
}
