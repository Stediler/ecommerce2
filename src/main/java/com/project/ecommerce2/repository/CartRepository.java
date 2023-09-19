package com.project.ecommerce2.repository;

import com.project.ecommerce2.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByCustomerId(UUID customerId);
}
