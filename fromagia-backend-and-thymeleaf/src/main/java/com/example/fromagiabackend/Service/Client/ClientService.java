package com.example.fromagiabackend.Service.Client;

import com.example.fromagiabackend.Entity.Client;
import com.example.fromagiabackend.Entity.Company;
import com.example.fromagiabackend.Entity.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ClientService {
    void save(Client client);


    List<Order> getClientDeliveredRejectedReceivedOrders(Integer id);

    List<Order> getClientPendingOrAcceptedOrders(Integer id);

    List<Order> getClientOrders(Integer id);
}
