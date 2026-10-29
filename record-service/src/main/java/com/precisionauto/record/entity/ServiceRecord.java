package com.precisionauto.record.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "service_records")
public class ServiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @NotBlank
    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;

    @Column(name = "booking_id")
    private Long bookingId;

    @NotBlank
    @Column(name = "service_details", columnDefinition = "TEXT", nullable = false)
    private String serviceDetails;

    @Column(name = "technician_id")
    private Long technicianId;

    @Column(name = "parts_replaced")
    private String partsReplaced;

    @Column(name = "serviced_at")
    private LocalDateTime servicedAt;

    public ServiceRecord() {
        this.servicedAt = LocalDateTime.now();
    }

    public ServiceRecord(String vehicleId, Long bookingId, String serviceDetails, Long technicianId, String partsReplaced) {
        this.vehicleId = vehicleId;
        this.bookingId = bookingId;
        this.serviceDetails = serviceDetails;
        this.technicianId = technicianId;
        this.partsReplaced = partsReplaced;
        this.servicedAt = LocalDateTime.now();
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getServiceDetails() {
        return serviceDetails;
    }

    public void setServiceDetails(String serviceDetails) {
        this.serviceDetails = serviceDetails;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }

    public String getPartsReplaced() {
        return partsReplaced;
    }

    public void setPartsReplaced(String partsReplaced) {
        this.partsReplaced = partsReplaced;
    }

    public LocalDateTime getServicedAt() {
        return servicedAt;
    }

    public void setServicedAt(LocalDateTime servicedAt) {
        this.servicedAt = servicedAt;
    }
}
