package com.example.springboot.rediscache.services;

import com.example.springboot.rediscache.models.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> save(User user);

    Mono<User> findById(Long id);

    Flux<User> findAll();

    Mono<Void> deleteById(Long id);
}
