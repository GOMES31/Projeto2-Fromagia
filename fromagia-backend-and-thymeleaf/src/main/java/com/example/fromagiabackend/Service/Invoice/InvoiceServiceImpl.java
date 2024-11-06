package com.example.fromagiabackend.Service.Invoice;

import com.example.fromagiabackend.Entity.Invoice;
import com.example.fromagiabackend.Repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InvoiceServiceImpl implements InvoiceService{

    private final InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceServiceImpl(InvoiceRepository _invoiceRepository){
        invoiceRepository = _invoiceRepository;
    }
    @Override
    public void save(Invoice invoice) {
        invoiceRepository.save(invoice);
    }

    @Override
    public Optional<Invoice> findByOrderId(Integer id) {
        return invoiceRepository.findById(id);
    }
}
