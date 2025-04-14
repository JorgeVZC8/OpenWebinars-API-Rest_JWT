package com.example.ApiRestSecurity.user.controller;

import com.example.ApiRestSecurity.security.jwt.JwtProvider;
import com.example.ApiRestSecurity.user.dto.*;
import com.example.ApiRestSecurity.user.model.UserEntity;
import com.example.ApiRestSecurity.user.service.UserService;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

    //Inyectamos las dependencias necesarias
    private final UserService service;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    //Este endpoint sirve para guardar un nuevo usuario con rol USER y sera publico para todo el mundo
    @PostMapping("/auth/register")
    public ResponseEntity<UserResponse> createUserWithUserRole(@RequestBody CreateUserRequest request){
        UserEntity user= service.createUserWithUserRole(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserResponse.fromUser(user));
    }

    //Este endpoint sirve para guardar un nuevo usuario con rol ADMIN. Solo ADMIN
    @PostMapping("/auth/register/admin")
    public ResponseEntity<UserResponse> createUserWithAdminRole(@RequestBody CreateUserRequest request){
        UserEntity user= service.createUserWithAdminRole(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UserResponse.fromUser(user));
    }

    //Este endpoint sirve para buscar un usuario por su id. Solo ADMIN
    @GetMapping("/id/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id){
            UserEntity user= service.findById(UUID.fromString(id))
                    .orElseThrow(()->new UsernameNotFoundException("There is not a user with id "+id));
            return ResponseEntity.ok(UserResponse.fromUser(user));
    }

    //Este endpoint sirve para listar todos los usuarios. Solo ADMIN
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        return ResponseEntity.ok(
                service.findAll().stream()
                        .map(UserResponse::fromUser)
                        .toList());
    }

    //Este endpoint sirve para buscar un usuario por su username. Solo ADMIN
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUserName(@PathVariable String username){
        UserEntity user= service.findByUserName(username)
                .orElseThrow(()-> new UsernameNotFoundException("There is not a user with username: "+username));
        return ResponseEntity.ok(UserResponse.fromUser(user));
    }

    //Permite editar los datos de un usuario como su username o el avatar. Solo usuario loggeadp
    @PutMapping("/edit/{id}")
    public ResponseEntity<UserResponse> edit(@PathVariable String id, @RequestBody UserEntity user){
        UserEntity u= service.edit(id, user).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(UserResponse.fromUser(u));
    }

    //Permite editar la contraseña de un usuario. en caso de implementarse solo ADMIN
    /*
    @PutMapping("/edit-password/{id}")
    public ResponseEntity<UserResponse> editPassword(@PathVariable String id, @RequestBody String newPassword){
        UserEntity u= service.editPassword(UUID.fromString(id), newPassword).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(UserResponse.fromUser(u));
    }*/

    //Permite al usuario logeado editar eliminar su cuenta. Los ADMIN tambien podran tener acceso a este endpoint
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable String id){
        try{
            service.deleteById(UUID.fromString(id));
            return ResponseEntity.ok("The user was removed successfully");
        }catch (UsernameNotFoundException e){
            return ResponseEntity.notFound().build();
        }
    }

    //Permite a los usuarios registrados logearse a partir de sus credenciales y genera un token
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

    //Permite al usuario logeado cambiar su propia contraseña
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
