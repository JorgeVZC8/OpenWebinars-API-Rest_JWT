package com.example.ApiRestSecurity.user.service;

import com.example.ApiRestSecurity.user.model.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService service;

   @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return service.findByUserName(username)
                .orElseThrow(()-> new UsernameNotFoundException("No user with username "+username));
    }


}
