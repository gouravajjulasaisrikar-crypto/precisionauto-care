package com.precisionauto.billing.config;

import com.precisionauto.billing.entity.Billing;
import com.precisionauto.billing.repository.BillingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initBills(BillingRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                Billing b1 = new Billing(
                        101L,
                        "FLT-101",
                        3L,
                        new BigDecimal("220.00"),
                        new BigDecimal("145.50"),
                        "50,000 km Major Maintenance + Brake Pads replacement"
                );
                b1.setStatus("PAID");
                repository.save(b1);

                Billing b2 = new Billing(
                        102L,
                        "FLT-101",
                        3L,
                        new BigDecimal("75.00"),
                        new BigDecimal("20.00"),
                        "Tire Rotation & Digital Alignment"
                );
                b2.setStatus("PAID");
                repository.save(b2);

                Billing b3 = new Billing(
                        103L,
                        "FLT-202",
                        3L,
                        new BigDecimal("90.00"),
                        new BigDecimal("210.00"),
                        "Optima AGM Battery Replacement & Electrical Diagnostics"
                );
                b3.setStatus("PENDING");
                repository.save(b3);
            }
        };
    }
}
