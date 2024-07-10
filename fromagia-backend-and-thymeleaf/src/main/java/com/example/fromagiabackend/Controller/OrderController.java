package com.example.fromagiabackend.Controller;

import com.example.fromagiabackend.Entity.Order;
import com.example.fromagiabackend.Service.Order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private OrderService orderService;

    @Autowired
    public OrderController(OrderService _orderService){
        orderService = _orderService;
    }

    @GetMapping("/orders/new")
    public String showOrderForm(Model model) {
        model.addAttribute("order", new Order());
        return "orders/new";
    }

    @PostMapping("/orders")
    public String createOrder(Order order) {

        String orderCode = orderService.generateOrderCode();
        order.setOrderCode(orderCode);
        orderService.save(order);

        return "redirect:/orders/list";
    }
}
