package com.example.springboot.rediscache.controller;

import com.example.springboot.rediscache.models.User;
import com.example.springboot.rediscache.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.net.URI;

@Slf4j
@RestController
@RequestMapping("/api/cache/v1")
@AllArgsConstructor
public class UserRestController {

    private final UserService userService;

    @GetMapping
    public Flux<User> getAllUsers() {
        return userService.findAll()
                .doFinally(signalType -> log.info("Thread: {}", Thread.currentThread().getName()));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<User>> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.fromCallable(() -> ResponseEntity.notFound().build()));
    }

    @PostMapping
    public Mono<ResponseEntity<Void>> createUser(@RequestBody User user) {
        return userService.save(user)
                .map(user1 -> ResponseEntity.created(
                                URI.create("/api/cache/v1/".concat(String.valueOf(user1.getId()))))
                        .build());
    }


    @PutMapping("/{id}")
    public Mono<ResponseEntity<Void>> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userService.findById(id)
                .flatMap(user -> {
                    user.setName(userDetails.getName());
                    user.setEmail(userDetails.getEmail());
                    return userService.save(user);
                })
                .map(unused -> ResponseEntity.noContent().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable Long id) {
        return userService.findById(id)
                .doOnNext(user -> log.info("Find user id : {}", user.getId()))
                .flatMap(user -> userService.deleteById(id)
                        .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                        .doFinally(signalType -> log.info("Deleted User with ID : {}", id)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));


    }

}
