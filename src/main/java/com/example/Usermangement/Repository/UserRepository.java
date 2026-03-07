package com.example.Usermangement.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Usermangement.Bean.User;

public interface UserRepository extends JpaRepository<User, Long>{

   Optional<User> findByEmail(String email);

}
