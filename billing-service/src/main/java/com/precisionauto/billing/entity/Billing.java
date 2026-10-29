package com.precisionauto.billing.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "billings")
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_id")
    private Long billId;

    @NotNull
    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @Column(name = "vehicle_id")
    private String vehicleId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "labor_charge", precision = 10, scale = 2)
    private BigDecimal laborCharge;

    @Column(name = "parts_charge", precision = 10, scale = 2)
    private BigDecimal partsCharge;

    @Column(name = "tax", precision = 10, scale = 2)
    private BigDecimal tax;

    @Column(name = "status", nullable = false)
    private String status; // PENDING, PAID, CANCELLED

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Billing() {
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    public Billing(Long bookingId, String vehicleId, Long userId, BigDecimal laborCharge, BigDecimal partsCharge, String description) {
        this.bookingId = bookingId;
        this.vehicleId = vehicleId;
        this.userId = userId;
        this.laborCharge = laborCharge != null ? laborCharge : BigDecimal.ZERO;
        this.partsCharge = partsCharge != null ? partsCharge : BigDecimal.ZERO;
        BigDecimal subtotal = this.laborCharge.add(this.partsCharge);
        this.tax = subtotal.multiply(new BigDecimal("0.10")); // 10% tax
        this.amount = subtotal.add(this.tax);
        this.status = "PENDING";
        this.description = description;
        this.createdAt = LocalDateTime.now();
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
