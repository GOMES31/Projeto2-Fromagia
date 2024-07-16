package com.example.fromagiabackend.Service.Client;

import com.example.fromagiabackend.Entity.Client;
import com.example.fromagiabackend.Entity.Enums.OrderState;
import com.example.fromagiabackend.Entity.Order;
import com.example.fromagiabackend.Repository.ClientRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository _clientRepository){
        clientRepository = _clientRepository;
    }
    @Override
    public void save(Client client) {
        clientRepository.save(client);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> getClientDeliveredRejectedReceivedOrders(Integer id) {
        Client client = clientRepository.findById(id).orElse(null);

        if (Objects.nonNull(client)) {
            Hibernate.initialize(client.getOrders());
            return client.getOrders().stream()
                    .filter(order -> order.getOrderState() == OrderState.DELIVERED || order.getOrderState() == OrderState.REJECTED || order.getOrderState() == OrderState.RECEIVED)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<Order> getClientPendingOrAcceptedOrders(Integer id) {
        Client client = clientRepository.findById(id).orElse(null);

        if (Objects.nonNull(client)) {
            Hibernate.initialize(client.getOrders());
            return client.getOrders().stream()
                    .filter(order -> order.getOrderState() == OrderState.PENDING || order.getOrderState() == OrderState.ACCEPTED)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    @Override
    @Transactional
    public List<Order> getClientOrders(Integer id) {

        Client client = clientRepository.findById(id).orElse(null);

        if (Objects.nonNull(client)) {
            Hibernate.initialize(client.getOrders());
            return client.getOrders();
        }

        return Collections.emptyList();
    }


}
