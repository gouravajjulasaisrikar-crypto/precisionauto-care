package com.precisionauto.billing.repository;

import com.precisionauto.billing.entity.Billing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {
    Optional<Billing> findByBookingId(Long bookingId);
    List<Billing> findByUserId(Long userId);
    List<Billing> findByVehicleId(String vehicleId);
    List<Billing> findByStatus(String status);
}
