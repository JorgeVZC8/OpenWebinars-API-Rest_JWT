package com.example.ApiRestSecurity.user.dto;

import com.example.ApiRestSecurity.user.model.UserEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserResponse {

    protected String id;
    protected String username, avatar, fullName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    protected LocalDateTime createdAt;


    public static UserResponse fromUser(UserEntity user){
        return UserResponse.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .fullName(user.getFullName())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
