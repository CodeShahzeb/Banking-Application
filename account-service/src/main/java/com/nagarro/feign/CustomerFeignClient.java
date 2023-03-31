package com.nagarro.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.nagarro.model.Customer;

@FeignClient(name="customer-service")
public interface CustomerFeignClient {
     
	@GetMapping("/customer/{id}")
	Customer getCustomerDetails(@PathVariable Long id);
}
