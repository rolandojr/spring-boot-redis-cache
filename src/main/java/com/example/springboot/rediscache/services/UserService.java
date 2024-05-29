package com.example.springboot.rediscache.services;

import com.example.springboot.rediscache.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void save(User user);

    Optional<User> findById(Long id);

    List<User> findAll();

    void deleteById(Long id);
}
