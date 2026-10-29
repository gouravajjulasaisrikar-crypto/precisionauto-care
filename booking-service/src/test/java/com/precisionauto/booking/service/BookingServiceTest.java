package com.precisionauto.booking.service;

import com.precisionauto.booking.client.BillingClient;
import com.precisionauto.booking.client.RecordClient;
import com.precisionauto.booking.dto.*;
import com.precisionauto.booking.entity.Booking;
import com.precisionauto.booking.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RecordClient recordClient;

    @Mock
    private BillingClient billingClient;

    @InjectMocks
    private BookingService bookingService;

    private Booking sampleBooking;

    @BeforeEach
    void setUp() {
        sampleBooking = new Booking(1L, "FLT-101", "2026-10-10 10:00", "BAY-1", "Standard Service", "Routine check");
        sampleBooking.setBookingId(1L);
    }

    @Test
    @DisplayName("Should successfully schedule booking when bay is available")
    void testScheduleBooking_Success() {
        BookingRequest request = new BookingRequest(1L, "FLT-101", "2026-10-10 10:00", "BAY-1", "Standard Service", "Routine check");

        when(bookingRepository.findByBayIdAndServiceDateAndStatusNot("BAY-1", "2026-10-10 10:00", "CANCELLED"))
                .thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(Booking.class))).thenReturn(sampleBooking);

        Booking result = bookingService.scheduleBooking(request);

        assertNotNull(result);
        assertEquals("BAY-1", result.getBayId());
        assertEquals("CONFIRMED", result.getStatus());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should detect collision and throw IllegalStateException when bay is already booked")
    void testScheduleBooking_CollisionDetected() {
        BookingRequest request = new BookingRequest(1L, "FLT-102", "2026-10-10 10:00", "BAY-1", "Brake Check", "Urgent");

        when(bookingRepository.findByBayIdAndServiceDateAndStatusNot("BAY-1", "2026-10-10 10:00", "CANCELLED"))
                .thenReturn(List.of(sampleBooking));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            bookingService.scheduleBooking(request);
        });

        assertTrue(exception.getMessage().contains("Collision Detected"));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should auto-assign available bay when bay is not explicitly specified")
    void testScheduleBooking_AutoAssignBay() {
        BookingRequest request = new BookingRequest(1L, "FLT-103", "2026-10-10 10:00", null, "Oil Change", null);

        // BAY-1 is taken
        Booking existing = new Booking(2L, "FLT-999", "2026-10-10 10:00", "BAY-1", "Tune-up", null);
        when(bookingRepository.findByServiceDateAndStatusNot("2026-10-10 10:00", "CANCELLED"))
                .thenReturn(List.of(existing));

        Booking savedBooking = new Booking(1L, "FLT-103", "2026-10-10 10:00", "BAY-2", "Oil Change", null);
        when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);

        Booking result = bookingService.scheduleBooking(request);

        assertNotNull(result);
        assertEquals("BAY-2", result.getBayId());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    @DisplayName("Should orchestrate completion: update status, call Record Service, and call Billing Service")
    void testCompleteBooking_OrchestrationFlow() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(sampleBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(sampleBooking);

        RecordDto mockRecord = new RecordDto("FLT-101", 1L, "Brake pads replaced", 2L, "Ceramic Pads");
        mockRecord.setRecordId(501L);
        when(recordClient.createRecord(any(RecordDto.class))).thenReturn(mockRecord);

        BillingDto mockBill = new BillingDto(1L, "FLT-101", 1L, new BigDecimal("120.00"), new BigDecimal("65.00"), "Invoice");
        mockBill.setBillId(701L);
        mockBill.setAmount(new BigDecimal("203.50"));
        when(billingClient.generateBill(any(BillingDto.class))).thenReturn(mockBill);

        CompleteBookingRequest completeReq = new CompleteBookingRequest(
                2L, "Brake pads replaced", "Ceramic Pads",
                new BigDecimal("120.00"), new BigDecimal("65.00"), "Invoice"
        );

        CompleteBookingResponse response = bookingService.completeBooking(1L, completeReq);

        assertNotNull(response);
        assertEquals("COMPLETED", response.getBooking().getStatus());
        assertNotNull(response.getServiceRecord());
        assertEquals(501L, response.getServiceRecord().getRecordId());
        assertNotNull(response.getBillingInvoice());
        assertEquals(701L, response.getBillingInvoice().getBillId());

        verify(recordClient, times(1)).createRecord(any(RecordDto.class));
        verify(billingClient, times(1)).generateBill(any(BillingDto.class));
    }
}
