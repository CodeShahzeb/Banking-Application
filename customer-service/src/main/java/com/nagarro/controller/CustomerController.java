package com.nagarro.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.feign.AccountFeignClient;
import com.nagarro.model.Customer;
import com.nagarro.repository.CustomerRepository;

import jakarta.persistence.EntityNotFoundException;

@RestController
public class CustomerController {

	@Autowired
	CustomerRepository customerRepository;
	
	@Autowired
	AccountFeignClient accountFeignClient;

	

	@PostMapping("/customer")
	public Customer addCustomer(@Valid @RequestBody Customer customer) {
		return customerRepository.save(customer);
	}

	@GetMapping("/customer")
	public ResponseEntity<List<Customer>> getAllCustomers() {
		List<Customer> customers = customerRepository.findAll();
		return ResponseEntity.ok(customers);
	}

	@GetMapping("/customer/{id}")
	public Customer getCustomerDetails(@PathVariable Long id) {
		return customerRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Customer" + id + "not found."));
	}

	@PutMapping("/customer/{id}")
	public ResponseEntity<Customer> updateCustomer(@PathVariable(value = "id") Long employeeId,
			@RequestBody Customer customerDetails) {
		Customer customer = customerRepository.findById(employeeId)
				.orElseThrow(() -> new EntityNotFoundException("Customer not found for this id :: " + employeeId));

		customer.setAddress(customerDetails.getAddress());
		customer.setEmail(customerDetails.getEmail());
		customer.setName(customerDetails.getName());
		customer.setPassword(customerDetails.getPassword());
		final Customer updatedEmployee = customerRepository.save(customer);
		return ResponseEntity.ok(updatedEmployee);
	}
	
	@DeleteMapping("/customer/{id}")
	public ResponseEntity<String> deleteCustomer(@RequestParam Long id) throws EntityNotFoundException {
		return customerRepository.findById(id).map(customer -> {
			try {
				customerRepository.delete(customer);
				accountFeignClient.deleteAccount(id);
			} catch (Exception e) {
				throw new EntityNotFoundException(
						"Customer with id " + id + " deleted but Account for the customer doesn't exist");
			}

			return ResponseEntity.ok("customer with id " + id + " deleted along with its account");

		}).orElseThrow(() -> new EntityNotFoundException("customer " + id + " not found"));
	}


}
