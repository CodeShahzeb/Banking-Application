package com.nagarro.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name="account-service")
public interface AccountFeignClient {
    
    @DeleteMapping("/account/delete")
    @CircuitBreaker(name = "deleteAccount", fallbackMethod = "deleteAccountFallback")
    public ResponseEntity<String> deleteAccount(@RequestParam(required = false) Long customerId);

	
	default ResponseEntity<String> deleteAccountFallback(Long customerId,Throwable ex) { 
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service unavailable. Please try again later."); 
		}
	 
}
	