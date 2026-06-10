package com.aj.personal.projects.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aj.personal.projects.management.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
