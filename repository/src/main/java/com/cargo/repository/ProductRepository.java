package com.cargo.repository;

import com.cargo.models.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrue();
    List<Product> findByActiveTrueAndNameContainingIgnoreCase(String keyword);
    List<Product> findByActiveTrueAndStockGreaterThan(int stock);
    List<Product> findByNameContainingIgnoreCase(String keyword);
}