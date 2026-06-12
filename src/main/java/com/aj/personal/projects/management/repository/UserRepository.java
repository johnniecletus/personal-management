package com.aj.personal.projects.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aj.personal.projects.management.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailOrUsername(String username, String email );

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);


}
