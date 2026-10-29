package com.precisionauto.record.repository;

import com.precisionauto.record.entity.ServiceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecordRepository extends JpaRepository<ServiceRecord, Long> {
    List<ServiceRecord> findByVehicleIdOrderByServicedAtDesc(String vehicleId);
    Optional<ServiceRecord> findByBookingId(Long bookingId);
}
