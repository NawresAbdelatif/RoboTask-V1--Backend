package com.example.acwa.repositories;

import com.example.acwa.entities.RoleName;
import com.example.acwa.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByUsername(String username);
        Boolean existsByUsername(String username);
        Boolean existsByEmail(String email);
        Optional<User> findByEmail(String email);
        Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(String username, String email, Pageable pageable);
        Page<User> findByRoles_Name(String role, Pageable pageable);
        Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRoles_Name(String username, String email, String role, Pageable pageable);

        Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRoles_Name(
                String username, String email, RoleName role, Pageable pageable);

        Page<User> findByRoles_Name(RoleName role, Pageable pageable);

        long countByEnabled(boolean enabled);

}
