package com.example.ApiRestSecurity.user.controller;

import com.example.ApiRestSecurity.user.dto.CreateUserRequest;
import com.example.ApiRestSecurity.user.dto.UserResponse;
import com.example.ApiRestSecurity.user.model.UserEntity;
import com.example.ApiRestSecurity.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/auth/register")
    public ResponseEntity<UserResponse> createUserWithUserRole(@RequestBody CreateUserRequest request){
        UserEntity user= service.createUserWithUserRole(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserResponse.fromUser(user));
    }

    @PostMapping("/auth/register/admin")
    public ResponseEntity<UserResponse> createUserWithAdminRole(@RequestBody CreateUserRequest request){
        UserEntity user= service.createUserWithAdminRole(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserResponse.fromUser(user));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id){
            UserEntity user= service.findById(UUID.fromString(id))
                    .orElseThrow(()->new UsernameNotFoundException("There is not a user with id "+id));
            return ResponseEntity.ok(UserResponse.fromUser(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(UserResponse::fromUser)
                        .toList());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUserName(@PathVariable String username){
        UserEntity user= service.findByUserName(username)
                .orElseThrow(()-> new UsernameNotFoundException("There is not a user with username: "+username));
        return ResponseEntity.ok(UserResponse.fromUser(user));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<UserResponse> edit(@PathVariable String id, @RequestBody UserEntity user){
        UserEntity u= service.edit(id, user).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(UserResponse.fromUser(u));
    }

    @PutMapping("/edit-password/{id}")
    public ResponseEntity<UserResponse> editPassword(@PathVariable String id, @RequestBody String newPassword){
        UserEntity u= service.editPassword(UUID.fromString(id), newPassword).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(UserResponse.fromUser(u));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable String id){
        try{
            service.deleteById(UUID.fromString(id));
            return ResponseEntity.ok("The user was removed successfully");
        }catch (UsernameNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

}
