package com.precisionauto.booking.dto;

import java.time.LocalDateTime;

public class RecordDto {
    private Long recordId;
    private String vehicleId;
    private Long bookingId;
    private String serviceDetails;
    private Long technicianId;
    private String partsReplaced;
    private LocalDateTime servicedAt;

    public RecordDto() {}

    public RecordDto(String vehicleId, Long bookingId, String serviceDetails, Long technicianId, String partsReplaced) {
        this.vehicleId = vehicleId;
        this.bookingId = bookingId;
        this.serviceDetails = serviceDetails;
        this.technicianId = technicianId;
        this.partsReplaced = partsReplaced;
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
