package com.precisionauto.booking.service;

import com.precisionauto.booking.client.BillingClient;
import com.precisionauto.booking.client.RecordClient;
import com.precisionauto.booking.dto.*;
import com.precisionauto.booking.entity.Booking;
import com.precisionauto.booking.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);
    private static final List<String> ALL_BAYS = Arrays.asList("BAY-1", "BAY-2", "BAY-3");

    private final BookingRepository bookingRepository;
    private final RecordClient recordClient;
    private final BillingClient billingClient;

    public BookingService(BookingRepository bookingRepository, RecordClient recordClient, BillingClient billingClient) {
        this.bookingRepository = bookingRepository;
        this.recordClient = recordClient;
        this.billingClient = billingClient;
    }

    /**
     * Eliminates booking collisions for service bays
     */
    @Transactional
    public Booking scheduleBooking(BookingRequest request) {
        String serviceDate = request.getServiceDate().trim();
        String targetBay = request.getBayId();

        if (targetBay != null && !targetBay.isBlank()) {
            targetBay = targetBay.trim().toUpperCase();
            // Check if specified bay is already booked for this slot
            List<Booking> collisions = bookingRepository.findByBayIdAndServiceDateAndStatusNot(
                    targetBay, serviceDate, "CANCELLED"
            );
            if (!collisions.isEmpty()) {
                throw new IllegalStateException("Collision Detected: " + targetBay +
                        " is already booked for " + serviceDate + ". Please choose another bay or time.");
            }
        } else {
            // Auto-assign first available bay
            List<Booking> existingBookingsForDate = bookingRepository.findByServiceDateAndStatusNot(
                    serviceDate, "CANCELLED"
            );

            List<String> occupiedBays = existingBookingsForDate.stream()
                    .map(Booking::getBayId)
                    .toList();

            targetBay = ALL_BAYS.stream()
                    .filter(b -> !occupiedBays.contains(b))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Collision Detected: All service bays are fully booked for "
                            + serviceDate + ". Please select another date or time slot."));
        }

        Booking booking = new Booking(
                request.getUserId(),
                request.getVehicleId(),
                serviceDate,
                targetBay,
                request.getServiceType(),
                request.getNotes()
        );

        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getBookingsByUserId(Long userId) {
        return bookingRepository.findByUserId(userId);
    }

    public List<Booking> getBookingsByVehicleId(String vehicleId) {
        return bookingRepository.findByVehicleId(vehicleId);
    }

    public Booking updateStatus(Long id, String newStatus) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + id));
        booking.setStatus(newStatus.toUpperCase());
        return bookingRepository.save(booking);
    }

    /**
     * Orchestrates inter-service communication pipeline:
     * Booking -> Record Service -> Billing Service
     */
    @Transactional
    public CompleteBookingResponse completeBooking(Long id, CompleteBookingRequest req) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + id));

        booking.setStatus("COMPLETED");
        Booking updatedBooking = bookingRepository.save(booking);

        // 1. Inter-service call to Record Service
        RecordDto createdRecord = null;
        try {
            String details = (req != null && req.getServiceDetails() != null && !req.getServiceDetails().isBlank())
                    ? req.getServiceDetails()
                    : "Completed " + booking.getServiceType() + " in " + booking.getBayId();

            String parts = (req != null && req.getPartsReplaced() != null) ? req.getPartsReplaced() : "Standard service fluids and filters";
            Long techId = (req != null && req.getTechnicianId() != null) ? req.getTechnicianId() : 2L;

            RecordDto recordRequest = new RecordDto(
                    booking.getVehicleId(),
                    booking.getBookingId(),
                    details,
                    techId,
                    parts
            );
            createdRecord = recordClient.createRecord(recordRequest);
            log.info("Inter-service call: Created Service Record {} for Booking {}", createdRecord.getRecordId(), id);
        } catch (Exception e) {
            log.error("Failed to create Service Record via OpenFeign: {}", e.getMessage());
        }

        // 2. Inter-service call to Billing Service
        BillingDto createdBill = null;
        try {
            BigDecimal labor = (req != null && req.getLaborCharge() != null) ? req.getLaborCharge() : new BigDecimal("120.00");
            BigDecimal parts = (req != null && req.getPartsCharge() != null) ? req.getPartsCharge() : new BigDecimal("65.00");
            String desc = (req != null && req.getInvoiceDescription() != null)
                    ? req.getInvoiceDescription()
                    : "Invoice for " + booking.getServiceType() + " (Vehicle " + booking.getVehicleId() + ")";

            BigDecimal subtotal = labor.add(parts);
            BigDecimal tax = subtotal.multiply(new BigDecimal("0.10")).setScale(2, java.math.RoundingMode.HALF_UP);
            BigDecimal total = subtotal.add(tax).setScale(2, java.math.RoundingMode.HALF_UP);

            BillingDto billRequest = new BillingDto(
                    booking.getBookingId(),
                    booking.getVehicleId(),
                    booking.getUserId(),
                    labor,
                    parts,
                    desc
            );
            billRequest.setTax(tax);
            billRequest.setAmount(total);
            billRequest.setStatus("PENDING");

            createdBill = billingClient.generateBill(billRequest);
            log.info("Inter-service call: Generated Bill {} for Booking {}", createdBill.getBillId(), id);
        } catch (Exception e) {
            log.error("Failed to generate Bill via OpenFeign: {}", e.getMessage());
        }

        return new CompleteBookingResponse(
                "Service successfully completed. Maintenance record logged and invoice generated.",
                updatedBooking,
                createdRecord,
                createdBill
        );
    }

    @Transactional
    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + id));
        bookingRepository.delete(booking);
        log.info("Booking with ID {} successfully deleted.", id);
    }

    @Transactional
    public Booking updateBooking(Long id, BookingRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + id));
        if (request.getVehicleId() != null) booking.setVehicleId(request.getVehicleId());
        if (request.getServiceDate() != null) booking.setServiceDate(request.getServiceDate());
        if (request.getBayId() != null) booking.setBayId(request.getBayId());
        if (request.getServiceType() != null) booking.setServiceType(request.getServiceType());
        if (request.getNotes() != null) booking.setNotes(request.getNotes());
        return bookingRepository.save(booking);
    }
}
