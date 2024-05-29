package com.example.springboot.rediscache.repository;

import com.example.springboot.rediscache.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
