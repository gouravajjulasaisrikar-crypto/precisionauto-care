package com.precisionauto.booking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotBlank
    @Column(name = "vehicle_id", nullable = false)
    private String vehicleId;

    @NotBlank
    @Column(name = "service_date", nullable = false)
    private String serviceDate; // Format: YYYY-MM-DD or YYYY-MM-DD HH:mm

    @Column(name = "bay_id", nullable = false)
    private String bayId; // e.g., BAY-1, BAY-2, BAY-3

    @Column(name = "service_type")
    private String serviceType;

    @NotNull
    @Column(name = "status", nullable = false)
    private String status; // CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Booking() {
        this.status = "CONFIRMED";
        this.createdAt = LocalDateTime.now();
    }

    public Booking(Long userId, String vehicleId, String serviceDate, String bayId, String serviceType, String notes) {
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.serviceDate = serviceDate;
        this.bayId = bayId;
        this.serviceType = serviceType;
        this.notes = notes;
        this.status = "CONFIRMED";
        this.createdAt = LocalDateTime.now();
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(String serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getBayId() {
        return bayId;
    }

    public void setBayId(String bayId) {
        this.bayId = bayId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
