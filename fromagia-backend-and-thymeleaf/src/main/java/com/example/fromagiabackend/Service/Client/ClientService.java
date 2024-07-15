package com.example.fromagiabackend.Service.Client;

import com.example.fromagiabackend.Entity.Client;
import com.example.fromagiabackend.Entity.Order;

import java.util.List;

public interface ClientService {
    void save(Client client);

    List<Order> getClientAcceptedOrRejectedOrders(Integer id);

    List<Order> getClientPendingOrders(Integer id);
}
