package com.example.springboot.rediscache.services.impl;

import com.example.springboot.rediscache.models.User;
import com.example.springboot.rediscache.repository.UserRepository;
import com.example.springboot.rediscache.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RListReactive;
import org.redisson.api.RMapReactive;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RListReactive<User> redissonRListReactiveClient;
    private final RMapReactive<Long, User> redissonRMapReactiveClient;


    @Override
    public Mono<User> save(User user) {
        return userRepository.save(user)
                .doOnNext(user1 -> log.info("User Created: Thread used {}", Thread.currentThread().getName()))
                .flatMap(user1 -> redissonRMapReactiveClient.put(user1.getId(), user1)
                        .thenReturn(user1));

    }

    @Override
    public Mono<User> findById(Long id) {
        return redissonRMapReactiveClient.get(id)
                .doOnNext(user -> log.info("Find user by id {}", user.getId()))
                .switchIfEmpty(userRepository.findById(id)
                        .flatMap(user -> redissonRMapReactiveClient.put(id, user)
                                .thenReturn(user)));
    }

    @Override
    public Flux<User> findAll() {
        return redissonRListReactiveClient.readAll()
                .doOnNext(users -> log.info("Find all users from Redis thread {}", Thread.currentThread().getName()))
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(userRepository.findAll()
                        .collectList()
                        .doOnNext(user -> log.info("Find all users from Database thread {}", Thread.currentThread().getName()))
                        .flatMap(users -> redissonRListReactiveClient.addAll(users)
                                .then(redissonRListReactiveClient.expire(Duration.ofSeconds(30))).thenReturn(users))
                        .flatMapMany(Flux::fromIterable));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return userRepository.deleteById(id)
                .then(redissonRMapReactiveClient.remove(id).then());
    }
}
