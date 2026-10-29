package com.precisionauto.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BookingRequest {
    @NotNull
    private Long userId;

    @NotBlank
    private String vehicleId;

    @NotBlank
    private String serviceDate; // e.g. "2026-10-05 09:00"

    private String bayId; // Optional: if omitted, auto-assigned

    @NotBlank
    private String serviceType; // e.g. "Oil Change & Inspection", "Brake Overhaul"

    private String notes;

    public BookingRequest() {}

    public BookingRequest(Long userId, String vehicleId, String serviceDate, String bayId, String serviceType, String notes) {
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.serviceDate = serviceDate;
        this.bayId = bayId;
        this.serviceType = serviceType;
        this.notes = notes;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
