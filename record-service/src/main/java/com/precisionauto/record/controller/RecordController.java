package com.precisionauto.record.controller;

import com.precisionauto.record.entity.ServiceRecord;
import com.precisionauto.record.service.RecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/records")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @PostMapping
    public ResponseEntity<ServiceRecord> createRecord(@Valid @RequestBody ServiceRecord record) {
        ServiceRecord saved = recordService.createRecord(record);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<ServiceRecord>> getAllRecords() {
        return ResponseEntity.ok(recordService.getAllRecords());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecordById(@PathVariable("id") Long id) {
        return recordService.getRecordById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<ServiceRecord>> getRecordsByVehicle(@PathVariable("vehicleId") String vehicleId) {
        return ResponseEntity.ok(recordService.getRecordsByVehicleId(vehicleId));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getRecordByBooking(@PathVariable("bookingId") Long bookingId) {
        return recordService.getRecordByBookingId(bookingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "record-service"));
    }
}
