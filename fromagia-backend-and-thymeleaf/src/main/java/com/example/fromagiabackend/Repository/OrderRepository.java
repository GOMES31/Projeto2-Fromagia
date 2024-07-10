package com.example.fromagiabackend.Repository;

import com.example.fromagiabackend.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Query para encontrar a ultima encomenda, para ir buscar o codigo dela
    Order findTopByOrderByIdDesc();

}
