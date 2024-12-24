package com.mdm.weather.controller;

import com.mdm.weather.common.ResponseResult;
import com.mdm.weather.entity.User;
import com.mdm.weather.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseResult<User> createUser(@RequestBody User user) {
        return ResponseResult.success(userService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseResult<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseResult.success(userService.updateUser(id, user));
    }

    @DeleteMapping("/{id}")
    public ResponseResult<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseResult.success(null);
    }

    @GetMapping("/{id}")
    public ResponseResult<User> getUserById(@PathVariable Long id) {
        return ResponseResult.success(userService.getUserById(id));
    }

    @GetMapping
    public ResponseResult<List<User>> getAllUsers() {
        return ResponseResult.success(userService.getAllUsers());
    }
} 