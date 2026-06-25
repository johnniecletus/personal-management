package com.aj.personal.projects.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aj.personal.projects.management.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailOrUserName(String email, String userName);

    Optional<User> findByEmail(String email);

    Optional<User> findByUserName(String userName);

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);
}
