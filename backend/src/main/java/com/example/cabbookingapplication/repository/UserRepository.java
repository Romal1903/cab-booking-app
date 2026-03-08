package com.example.cabbookingapplication.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.cabbookingapplication.entity.User;
import com.example.cabbookingapplication.enums.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    List<User> findByRole(Role role);
}
