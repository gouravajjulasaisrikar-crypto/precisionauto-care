package com.precisionauto.billing.service;

import com.precisionauto.billing.entity.Billing;
import com.precisionauto.billing.repository.BillingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BillingService {

    private final BillingRepository billingRepository;

    public BillingService(BillingRepository billingRepository) {
        this.billingRepository = billingRepository;
    }

    public Billing generateBill(Billing billing) {
        // If amount is not set or zero, calculate dynamically from labor + parts + tax
        if (billing.getAmount() == null || billing.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal labor = billing.getLaborCharge() != null ? billing.getLaborCharge() : new BigDecimal("150.00");
            BigDecimal parts = billing.getPartsCharge() != null ? billing.getPartsCharge() : new BigDecimal("85.00");
            BigDecimal subtotal = labor.add(parts);
            BigDecimal tax = subtotal.multiply(new BigDecimal("0.10")).setScale(2, java.math.RoundingMode.HALF_UP);
            BigDecimal total = subtotal.add(tax).setScale(2, java.math.RoundingMode.HALF_UP);
            billing.setLaborCharge(labor.setScale(2, java.math.RoundingMode.HALF_UP));
            billing.setPartsCharge(parts.setScale(2, java.math.RoundingMode.HALF_UP));
            billing.setTax(tax);
            billing.setAmount(total);
        }

        if (billing.getStatus() == null || billing.getStatus().isBlank()) {
            billing.setStatus("PENDING");
        }

        return billingRepository.save(billing);
    }

    public List<Billing> getAllBills() {
        return billingRepository.findAll();
    }

    public Optional<Billing> getBillById(Long billId) {
        return billingRepository.findById(billId);
    }

    public Optional<Billing> getBillByBookingId(Long bookingId) {
        return billingRepository.findByBookingId(bookingId);
    }

    public List<Billing> getBillsByUserId(Long userId) {
        return billingRepository.findByUserId(userId);
    }

    public Billing updateStatus(Long billId, String newStatus) {
        Billing billing = billingRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Bill not found with ID: " + billId));
        billing.setStatus(newStatus.toUpperCase());
        return billingRepository.save(billing);
    }

    public Billing payBill(Long billId) {
        return updateStatus(billId, "PAID");
    }
}
