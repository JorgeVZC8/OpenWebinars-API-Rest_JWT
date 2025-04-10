package com.example.ApiRestSecurity.user.service;

import com.example.ApiRestSecurity.user.dto.CreateUserRequest;
import com.example.ApiRestSecurity.user.model.UserEntity;
import com.example.ApiRestSecurity.user.model.UserRole;
import com.example.ApiRestSecurity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;

    public UserEntity createUser(CreateUserRequest request, Set<UserRole> roles){
        UserEntity user= UserEntity.builder()
                .userName(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .avatar(request.getAvatar())
                .fullName(request.getFullName())
                .roles(roles)
                .build();

        return repository.save(user);
    }

    public UserEntity createUserWithUserRole(CreateUserRequest request){
        return createUser(request, Set.of(UserRole.USER));
    }

    public UserEntity createUserWithAdminRole(CreateUserRequest request){
        return createUser(request, Set.of(UserRole.ADMIN));
    }

    public List<UserEntity> findAll(){
        return repository.findAll();
    }

    public Optional<UserEntity> findById(UUID id){
        return repository.findById(id);
    }

    public Optional<UserEntity> findByUserName(String username){
        return repository.findFirstByUserName(username);
    }

    public Optional<UserEntity> edit(String id, UserEntity user){
        return repository.findById(UUID.fromString(id))
                .map(u->{
                    u.setAvatar(user.getAvatar());
                    u.setFullName(user.getFullName());
                    return repository.save(u);
                });
    }

    public Optional<UserEntity> editPassword(UUID id, String newPassword){
        return repository.findById(id)
                .map(u->{
                    u.setPassword(passwordEncoder.encode(newPassword));
                    return repository.save(u);
                });
    }

    public void deleteById(UUID id){
        if (repository.existsById(id))
            repository.deleteById(id);
    }

    public void delete(UserEntity user) {
        deleteById(user.getId());
    }

    public boolean passwordMatch(UserEntity user, String clearPassword){
        return passwordEncoder.matches(clearPassword, user.getPassword());
    }
}
