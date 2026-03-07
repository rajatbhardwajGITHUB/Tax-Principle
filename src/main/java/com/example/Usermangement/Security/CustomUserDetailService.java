package com.example.Usermangement.Security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.Usermangement.Repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername (String email)
       throws  UsernameNotFoundException{
         return userRepository.findByEmail(email)
            .map(CustomUserDetails :: new)
            .orElseThrow(()->
                  new  UsernameNotFoundException("User not found"));
      }

    } 



