package com.example.springboot.rediscache.services.impl;

import com.example.springboot.rediscache.models.User;
import com.example.springboot.rediscache.repository.UserRepository;
import com.example.springboot.rediscache.services.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RedissonClient redissonClient;

    private static final String USER_CACHE = "USER_CACHE";
    private static final String USER_LIST_CACHE = "USER_LIST_CACHE";


    @Override
    public void save(User user) {
        User userSaved = userRepository.save(user);
        log.info("Save user in DB. Name = {} , Email = {}", user.getName(), user.getEmail());
        redissonClient.getMap(USER_CACHE).put(userSaved.getId(), userSaved);
        log.info("Save user in Redis. Name = {} , Email = {}", user.getName(), user.getEmail());
    }

    @Override
    public Optional<User> findById(Long id) {
        RMap<Long, User> userRMap = redissonClient.getMap(USER_CACHE);
        log.info("Find user from caché");
        if (!userRMap.containsKey(id)) {
            Optional<User> optionalUser = userRepository.findById(id);
            log.info("Find user from DB");
            optionalUser.ifPresent(user -> {
                userRMap.put(id, user);
                log.info("Save user in cache");
            });
            return optionalUser;
        }
        return Optional.ofNullable(userRMap.get(id));
    }

    @Override
    public List<User> findAll() {
        RList<User> userRList = redissonClient.getList(USER_LIST_CACHE);
        if (userRList.isEmpty()) {
            List<User> userList = userRepository.findAll();
            userRList.addAll(userList);
            userRList.expire(60, TimeUnit.SECONDS);
            return userList;
        }
        return userRList.readAll();
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
        log.info("Delete user from DB with id {}", id);
        redissonClient.getMap(USER_CACHE).remove(id);
        log.info("Delete user from CACHE with id {}", id);

    }
}
