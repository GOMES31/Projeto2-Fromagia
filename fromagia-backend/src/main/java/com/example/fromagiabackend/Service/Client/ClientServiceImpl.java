package com.example.fromagiabackend.Service.Client;

import com.example.fromagiabackend.Entity.Client;
import com.example.fromagiabackend.Repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
