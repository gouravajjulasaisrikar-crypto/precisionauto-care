package com.precisionauto.booking.dto;

import com.precisionauto.booking.entity.Booking;

public class CompleteBookingResponse {
    private String message;
    private Booking booking;
    private RecordDto serviceRecord;
    private BillingDto billingInvoice;

    public CompleteBookingResponse() {}

    public CompleteBookingResponse(String message, Booking booking, RecordDto serviceRecord, BillingDto billingInvoice) {
        this.message = message;
        this.booking = booking;
        this.serviceRecord = serviceRecord;
        this.billingInvoice = billingInvoice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public RecordDto getServiceRecord() {
        return serviceRecord;
    }

    public void setServiceRecord(RecordDto serviceRecord) {
        this.serviceRecord = serviceRecord;
    }

    public BillingDto getBillingInvoice() {
        return billingInvoice;
    }

    public void setBillingInvoice(BillingDto billingInvoice) {
        this.billingInvoice = billingInvoice;
    }
}
