package com.precisionauto.booking.dto;

import java.math.BigDecimal;

public class CompleteBookingRequest {
    private Long technicianId;
    private String serviceDetails;
    private String partsReplaced;
    private BigDecimal laborCharge;
    private BigDecimal partsCharge;
    private String invoiceDescription;

    public CompleteBookingRequest() {}

    public CompleteBookingRequest(Long technicianId, String serviceDetails, String partsReplaced, BigDecimal laborCharge, BigDecimal partsCharge, String invoiceDescription) {
        this.technicianId = technicianId;
        this.serviceDetails = serviceDetails;
        this.partsReplaced = partsReplaced;
        this.laborCharge = laborCharge;
        this.partsCharge = partsCharge;
        this.invoiceDescription = invoiceDescription;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public void setTechnicianId(Long technicianId) {
        this.technicianId = technicianId;
    }

    public String getServiceDetails() {
        return serviceDetails;
    }

    public void setServiceDetails(String serviceDetails) {
        this.serviceDetails = serviceDetails;
    }

    public String getPartsReplaced() {
        return partsReplaced;
    }

    public void setPartsReplaced(String partsReplaced) {
        this.partsReplaced = partsReplaced;
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

    public String getInvoiceDescription() {
        return invoiceDescription;
    }

    public void setInvoiceDescription(String invoiceDescription) {
        this.invoiceDescription = invoiceDescription;
    }
}
