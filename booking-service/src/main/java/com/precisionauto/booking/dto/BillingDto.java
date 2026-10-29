package com.precisionauto.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BillingDto {
    private Long billId;
    private Long bookingId;
    private String vehicleId;
    private Long userId;
    private BigDecimal amount;
    private BigDecimal laborCharge;
    private BigDecimal partsCharge;
    private BigDecimal tax;
    private String status;
    private String description;
    private LocalDateTime createdAt;

    public BillingDto() {}

    public BillingDto(Long bookingId, String vehicleId, Long userId, BigDecimal laborCharge, BigDecimal partsCharge, String description) {
        this.bookingId = bookingId;
        this.vehicleId = vehicleId;
        this.userId = userId;
        this.laborCharge = laborCharge;
        this.partsCharge = partsCharge;
        this.description = description;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getLaborCharge() {
        return laborCharge;
    }

    public void setLaborCharge(BigDecimal laborCharge) {
        this.laborCharge = laborCharge;
    }

    public BigDecimal getPartsCharge() {
        return partsCharge;
    }

    public void setPartsCharge(BigDecimal partsCharge) {
        this.partsCharge = partsCharge;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
