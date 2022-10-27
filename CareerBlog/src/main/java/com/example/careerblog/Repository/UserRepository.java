package com.example.careerblog.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.careerblog.Models.User;

public interface UserRepository extends JpaRepository<User, Long>{
	
	User findByUsername(String username);

	User findByActivationCode(String code);
}
