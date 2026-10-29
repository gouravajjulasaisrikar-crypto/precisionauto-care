package com.precisionauto.booking.config;

import com.precisionauto.booking.entity.Booking;
import com.precisionauto.booking.repository.BookingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initBookings(BookingRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                String today = LocalDate.now().toString();
                String tomorrow = LocalDate.now().plusDays(1).toString();

                Booking b1 = new Booking(
                        3L,
                        "FLT-101",
                        today + " 10:00",
                        "BAY-1",
                        "Fleet Preventive Inspection",
                        "Priority maintenance for delivery fleet"
                );
                b1.setStatus("IN_PROGRESS");
                repository.save(b1);

                Booking b2 = new Booking(
                        3L,
                        "FLT-202",
                        tomorrow + " 14:00",
                        "BAY-2",
                        "Brake Rotor Resurfacing",
                        "Driver reported high-speed pulsation"
                );
                b2.setStatus("CONFIRMED");
                repository.save(b2);
            }
        };
    }
}
