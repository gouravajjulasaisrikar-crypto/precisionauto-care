package com.precisionauto.booking.repository;

import com.precisionauto.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBayIdAndServiceDateAndStatusNot(String bayId, String serviceDate, String status);
    List<Booking> findByServiceDateAndStatusNot(String serviceDate, String status);
    List<Booking> findByUserId(Long userId);
    List<Booking> findByVehicleId(String vehicleId);
    List<Booking> findByStatus(String status);
}
