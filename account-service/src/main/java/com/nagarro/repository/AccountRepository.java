package com.nagarro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nagarro.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
   
	Optional<Account> findByCustomerId(Long customerId);
}
