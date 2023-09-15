package com.project.ecommerce2.repository;

import com.project.ecommerce2.model.Items;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemsRepository extends JpaRepository<Items, Long> {
}
