package com.example.XenoShopSync.repository;

import java.util.Optional;
import com.example.XenoShopSync.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
