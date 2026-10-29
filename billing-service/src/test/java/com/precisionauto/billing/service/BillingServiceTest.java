package com.precisionauto.billing.service;

import com.precisionauto.billing.entity.Billing;
import com.precisionauto.billing.repository.BillingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BillingServiceTest {

    @Mock
    private BillingRepository billingRepository;

    @InjectMocks
    private BillingService billingService;

    private Billing sampleBill;

    @BeforeEach
    void setUp() {
        sampleBill = new Billing(101L, "FLT-101", 3L, new BigDecimal("100.00"), new BigDecimal("50.00"), "Oil Service");
        sampleBill.setBillId(1L);
    }

    @Test
    @DisplayName("Should dynamically calculate bill amount with subtotal and 10% tax")
    void testGenerateBill_DynamicCalculation() {
        Billing input = new Billing();
        input.setBookingId(101L);
        input.setVehicleId("FLT-101");
        input.setUserId(3L);
        input.setLaborCharge(new BigDecimal("200.00"));
        input.setPartsCharge(new BigDecimal("100.00"));

        when(billingRepository.save(any(Billing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Billing result = billingService.generateBill(input);

        assertNotNull(result);
        assertEquals(new BigDecimal("30.00"), result.getTax()); // 10% of 300
        assertEquals(new BigDecimal("330.00"), result.getAmount()); // 300 + 30
        assertEquals("PENDING", result.getStatus());
        verify(billingRepository, times(1)).save(any(Billing.class));
    }

    @Test
    @DisplayName("Should update billing payment status to PAID")
    void testPayBill() {
        when(billingRepository.findById(1L)).thenReturn(Optional.of(sampleBill));
        when(billingRepository.save(any(Billing.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Billing updated = billingService.payBill(1L);

        assertNotNull(updated);
        assertEquals("PAID", updated.getStatus());
        verify(billingRepository, times(1)).save(sampleBill);
    }
}
