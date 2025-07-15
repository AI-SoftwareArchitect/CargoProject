package com.cargo.repository;

import com.cargo.models.entities.Shipment; // Make sure this import is correct
import com.cargo.models.entities.ShipmentStatus; // Make sure this import is correct
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByOrderId(Long orderId);
    List<Shipment> findByStatus(ShipmentStatus status);
    List<Shipment> findByShipperId(Long shipperId);
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
}