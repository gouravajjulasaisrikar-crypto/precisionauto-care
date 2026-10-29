package com.precisionauto.record.service;

import com.precisionauto.record.entity.ServiceRecord;
import com.precisionauto.record.repository.RecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecordServiceTest {

    @Mock
    private RecordRepository recordRepository;

    @InjectMocks
    private RecordService recordService;

    private ServiceRecord sampleRecord;

    @BeforeEach
    void setUp() {
        sampleRecord = new ServiceRecord("FLT-101", 101L, "50k Major Service", 2L, "Spark plugs, filters");
        sampleRecord.setRecordId(1L);
    }

    @Test
    @DisplayName("Should successfully persist service record")
    void testCreateRecord() {
        when(recordRepository.save(any(ServiceRecord.class))).thenReturn(sampleRecord);

        ServiceRecord saved = recordService.createRecord(sampleRecord);

        assertNotNull(saved);
        assertEquals("FLT-101", saved.getVehicleId());
        assertEquals("50k Major Service", saved.getServiceDetails());
        verify(recordRepository, times(1)).save(sampleRecord);
    }

    @Test
    @DisplayName("Should retrieve vehicle history ordered by date")
    void testGetRecordsByVehicleId() {
        when(recordRepository.findByVehicleIdOrderByServicedAtDesc("FLT-101"))
                .thenReturn(List.of(sampleRecord));

        List<ServiceRecord> records = recordService.getRecordsByVehicleId("FLT-101");

        assertNotNull(records);
        assertEquals(1, records.size());
        assertEquals("FLT-101", records.get(0).getVehicleId());
    }
}
