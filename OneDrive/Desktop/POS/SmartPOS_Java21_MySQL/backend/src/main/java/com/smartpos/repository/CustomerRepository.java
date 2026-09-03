package com.smartpos.repository;
import com.smartpos.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CustomerRepository extends JpaRepository<Customer,Long>{ Optional<Customer> findFirstByPhone(String phone); Optional<Customer> findFirstByEmailIgnoreCase(String email); }
