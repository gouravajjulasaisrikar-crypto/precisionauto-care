package com.precisionauto.booking.client;

import com.precisionauto.booking.dto.RecordDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "record-service")
public interface RecordClient {

    @PostMapping("/records")
    RecordDto createRecord(@RequestBody RecordDto recordDto);
}
