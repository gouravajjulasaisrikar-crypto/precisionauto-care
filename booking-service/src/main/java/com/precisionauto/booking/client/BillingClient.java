package com.precisionauto.booking.client;

import com.precisionauto.booking.dto.BillingDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "billing-service")
public interface BillingClient {

    @PostMapping("/bills")
    BillingDto generateBill(@RequestBody BillingDto billingDto);
}
