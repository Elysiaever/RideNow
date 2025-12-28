package com.fth.user.controller;

import com.fth.user.domain.model.User;
import com.fth.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
   @Autowired
    private UserService userService;

   //增加用户
    @PutMapping("/add")
    public ResponseEntity<String> addUser(@RequestBody User user) {
        userService.addUser(user);
        return ResponseEntity.ok("User added");
    }

    //删除用户
    @PutMapping("/delete")
    public ResponseEntity<String> deleteUser(Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted");
    }

    //修改用户信息
    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return ResponseEntity.ok("User updated");
    }

    //查询用户信息
    @PutMapping("/get")
    public ResponseEntity<User> getUser(Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
}
