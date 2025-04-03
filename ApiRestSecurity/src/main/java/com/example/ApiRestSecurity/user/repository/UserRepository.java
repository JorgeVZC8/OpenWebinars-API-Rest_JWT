package com.example.ApiRestSecurity.user.repository;

import com.example.ApiRestSecurity.user.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findFirstByUserName(String userName);
}
