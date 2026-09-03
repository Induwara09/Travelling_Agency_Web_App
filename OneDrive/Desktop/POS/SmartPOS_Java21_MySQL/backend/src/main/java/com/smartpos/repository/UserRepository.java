package com.smartpos.repository;
import com.smartpos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User,Long>{
 Optional<User> findByUsernameIgnoreCase(String username);
 Optional<User> findByEmployeeIdIgnoreCase(String employeeId);
 boolean existsByUsernameIgnoreCase(String username);
 boolean existsByEmployeeIdIgnoreCase(String employeeId);
}
