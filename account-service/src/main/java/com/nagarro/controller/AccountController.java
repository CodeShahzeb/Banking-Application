package com.nagarro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nagarro.feign.CustomerFeignClient;
import com.nagarro.model.AccountDetailsDTO;
import com.nagarro.model.Customer;
import com.nagarro.repository.AccountRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.Builder;

@RestController
@Builder
public class AccountController {

	@Autowired
	AccountRepository accountRepository;
	
	 @Autowired 
	CustomerFeignClient customerFeignClient;
	 

	
	@PostMapping("/account/{id}/add")
	public ResponseEntity<String> addMoney(@PathVariable Long id, @RequestParam Long customerId,
			@RequestParam Double amount) {
		return accountRepository.findById(id).map(account -> {
			if (account.getCustomerId() == customerId) {
				account.setBalance(amount + account.getBalance());
				accountRepository.save(account);
				return ResponseEntity
						.ok("Amount " + amount + " added to account no " + id + " for customer id " + customerId);
			} else {
				return ResponseEntity.badRequest()
						.body("Customer id " + customerId + " does not match with the account");
			}
		}).orElse(ResponseEntity.badRequest().body("Account with id " + id + " not found"));
	}

	@PostMapping("/account/{id}/withdraw")
	public ResponseEntity<String> withdrawMoney(@PathVariable Long id, @RequestParam Long customerId,
			@RequestParam Double amount) {

		return accountRepository.findById(id).map(account -> {
			if (account.getCustomerId() == customerId) {
				if (amount > account.getBalance()) {
					return new ResponseEntity<>("Insuffient amount of balance", HttpStatus.BAD_REQUEST);
				}

				account.setBalance(account.getBalance() - amount);
				accountRepository.save(account);
				return ResponseEntity
						.ok("Amount " + amount + " withdrwan from account no " + id + " for customer id " + customerId);
			} else {
				return new ResponseEntity<>("Customer id " + customerId + "does not exist", HttpStatus.BAD_REQUEST);
			}
		}).orElseThrow(() -> new EntityNotFoundException("No account with id " + id));
	}
	

	@DeleteMapping("/account/delete")
	public ResponseEntity<String> deleteAccount(@RequestParam(required = false) Long accountNo,
			@RequestParam(required = false) Long customerId) {
		if (accountNo == null && customerId == null) {
			return new ResponseEntity<>(" Account number/Customer ID  not provided", HttpStatus.BAD_REQUEST);
		} else if (accountNo == null && customerId != null) {
			return accountRepository.findByCustomerId(customerId).map(account -> {
				accountRepository.delete(account);
				return ResponseEntity.ok("Account deleted for customer id " + customerId);
			}).orElseThrow(() -> new EntityNotFoundException("No account exits for customer id " + customerId));
		} else if (accountNo != null && customerId == null) {
			return accountRepository.findById(accountNo).map(account -> {
				accountRepository.delete(account);
				return ResponseEntity.ok("Account " + accountNo + " deleted");
			}).orElseThrow(() -> new EntityNotFoundException("No account exits for customer id " + customerId));
		} else {
			return accountRepository.findById(accountNo).map(account -> {
				accountRepository.delete(account);
				return ResponseEntity.ok("Account " + accountNo + " deleted");
			}).orElseThrow(() -> new EntityNotFoundException("No account exits for customer id " + customerId));
		}
	}
	
	@GetMapping("/account/{id}/details")
	public AccountDetailsDTO getAccountDetails(@PathVariable Long id) {
		return accountRepository.findById(id).map(account -> {
			Customer customer = customerFeignClient.getCustomerDetails(account.getCustomerId());
			AccountDetailsDTO accountDetailsDTO = new AccountDetailsDTO();
			accountDetailsDTO.setAccountBalance(account.getBalance());
			accountDetailsDTO.setAccountNo(account.getAccountNo());
			accountDetailsDTO.setAccountType(account.getType());
			accountDetailsDTO.setCustomerAddress(customer.getAddress());
			accountDetailsDTO.setCustomerEmail(customer.getEmail());
			accountDetailsDTO.setCustomerId(customer.getId());
			accountDetailsDTO.setCustomerName(customer.getName());
			return accountDetailsDTO;
		}).orElseThrow(() -> new EntityNotFoundException("Account with id: " + id + " not found"));
	}

}
