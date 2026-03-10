package com.example.Usermangement.Security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.service.invoker.UrlArgumentResolver;

import com.example.Usermangement.Repository.UserRepository;


@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository repo){
      this.userRepository = repo;
    }

    
      @Override
      public UserDetails loadUserByUsername (String email)
        throws  UsernameNotFoundException{
          return userRepository.findByEmail(email)
              .map(CustomUserDetails :: new)
              .orElseThrow(()->
                    new  UsernameNotFoundException("User not found"));
        }
} 



