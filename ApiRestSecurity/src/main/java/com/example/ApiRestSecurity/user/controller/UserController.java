package com.example.ApiRestSecurity.user.controller;

import com.example.ApiRestSecurity.security.jwt.JwtProvider;
import com.example.ApiRestSecurity.user.dto.*;
import com.example.ApiRestSecurity.user.model.UserEntity;
import com.example.ApiRestSecurity.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

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
    @PostMapping("/auth/login")
    public ResponseEntity<JwtUserResponse> login(@RequestBody LoginRequest loginRequest){
        //Realizamos la autenticacion
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        //Una vez realizada la autenticacion la guardamos en el contexto de seguridad
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        UserEntity user=(UserEntity) authentication.getPrincipal();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(JwtUserResponse.of(user, token));

    }

    @PutMapping("/user/changePassword")
    public ResponseEntity<UserResponse> changePassword(@RequestBody ChangePasswordRequest request, @AuthenticationPrincipal  UserEntity loggedUser){
        try{
            if(service.passwordMatch(loggedUser, request.getOldPassword())){
                Optional<UserEntity> modified= service.editPassword(loggedUser.getId(), request.getNewPassword());
                if(modified.isPresent()){
                    return ResponseEntity.ok(UserResponse.fromUser(modified.get()));
                }else{
                    throw new RuntimeException();
                }
            }
        }catch (RuntimeException exception){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password data error");
        }

        return null;
    }

}
