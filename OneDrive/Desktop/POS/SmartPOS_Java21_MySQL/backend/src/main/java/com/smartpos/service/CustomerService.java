package com.smartpos.service;
import com.smartpos.dto.CustomerDtos.*;
import com.smartpos.model.Customer;
import com.smartpos.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class CustomerService {
 private final CustomerRepository repo;private final CurrentUserService current;private final AuditService audit;
 public CustomerService(CustomerRepository r,CurrentUserService c,AuditService a){repo=r;current=c;audit=a;}
 public List<CustomerView> list(){return repo.findAll().stream().map(this::view).toList();}
 public CustomerView create(CustomerRequest r){Customer c=repo.save(Customer.builder().name(r.name()).phone(r.phone()).email(r.email()).address(r.address()).build());audit.log(current.get(),"CUSTOMER_CREATED","CUSTOMER",c.getId().toString(),c.getName());return view(c);}
 public CustomerView update(Long id,CustomerRequest r){Customer c=repo.findById(id).orElseThrow(()->new IllegalArgumentException("Customer not found"));c.setName(r.name());c.setPhone(r.phone());c.setEmail(r.email());c.setAddress(r.address());repo.save(c);audit.log(current.get(),"CUSTOMER_UPDATED","CUSTOMER",id.toString(),c.getName());return view(c);}
 private CustomerView view(Customer c){return new CustomerView(c.getId(),c.getName(),c.getPhone(),c.getEmail(),c.getAddress(),c.getLoyaltyPoints());}
}
