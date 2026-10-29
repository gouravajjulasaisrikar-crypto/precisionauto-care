package com.precisionauto.record.config;

import com.precisionauto.record.entity.ServiceRecord;
import com.precisionauto.record.repository.RecordRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initRecords(RecordRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                ServiceRecord r1 = new ServiceRecord(
                        "FLT-101",
                        101L,
                        "50,000 km Major Maintenance: Engine oil replacement, transmission fluid flush, brake pad replacement, multi-point safety inspection.",
                        2L,
                        "OEM Brake Pads (Front), Mobil 1 5W-30 Synthetic Oil, Oil Filter"
                );
                r1.setServicedAt(LocalDateTime.now().minusDays(45));
                repository.save(r1);

                ServiceRecord r2 = new ServiceRecord(
                        "FLT-101",
                        102L,
                        "Tire Rotation & Digital Wheel Alignment: Front-to-rear rotation, camber and toe angle calibration.",
                        2L,
                        "Wheel Balance Weights"
                );
                r2.setServicedAt(LocalDateTime.now().minusDays(15));
                repository.save(r2);

                ServiceRecord r3 = new ServiceRecord(
                        "FLT-202",
                        103L,
                        "Battery Replacement and Alternator Diagnostic: Battery test failed under load. Installed heavy-duty commercial AGM battery.",
                        2L,
                        "Optima YellowTop AGM Battery"
                );
                r3.setServicedAt(LocalDateTime.now().minusDays(5));
                repository.save(r3);
            }
        };
    }
}
