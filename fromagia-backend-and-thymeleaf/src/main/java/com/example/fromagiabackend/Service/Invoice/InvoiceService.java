package com.example.fromagiabackend.Service.Invoice;

import com.example.fromagiabackend.Entity.Invoice;

import java.util.Optional;

public interface InvoiceService {

    void save(Invoice invoice);

    Optional<Invoice> findByOrderId(Integer id);
}
