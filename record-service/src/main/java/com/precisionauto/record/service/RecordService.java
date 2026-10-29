package com.precisionauto.record.service;

import com.precisionauto.record.entity.ServiceRecord;
import com.precisionauto.record.repository.RecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecordService {

    private final RecordRepository recordRepository;

    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    public ServiceRecord createRecord(ServiceRecord record) {
        return recordRepository.save(record);
    }

    public List<ServiceRecord> getAllRecords() {
        return recordRepository.findAll();
    }

    public Optional<ServiceRecord> getRecordById(Long recordId) {
        return recordRepository.findById(recordId);
    }

    public List<ServiceRecord> getRecordsByVehicleId(String vehicleId) {
        return recordRepository.findByVehicleIdOrderByServicedAtDesc(vehicleId);
    }

    public Optional<ServiceRecord> getRecordByBookingId(Long bookingId) {
        return recordRepository.findByBookingId(bookingId);
    }
}
