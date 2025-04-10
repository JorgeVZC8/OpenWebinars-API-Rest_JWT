package com.example.ApiRestSecurity.user.dto;

import com.example.ApiRestSecurity.user.model.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class JwtUserResponse extends UserResponse{

    private String token;

    public JwtUserResponse(UserResponse response){
        this.id = response.getUsername();
        username= response.getUsername();
        fullName= response.getFullName();
        avatar= response.getAvatar();
        createdAt= response.getCreatedAt();
    }

    public  static JwtUserResponse of(UserEntity user, String token){
        JwtUserResponse result = new JwtUserResponse(UserResponse.fromUser(user));
        result.setToken(token);
        return result;
    }
}
