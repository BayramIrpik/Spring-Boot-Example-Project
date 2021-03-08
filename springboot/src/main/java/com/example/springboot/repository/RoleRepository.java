package com.example.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.springboot.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
	
	@Query("SELECT r FROM Role r WHERE r.id = ?1")
	public Role findByRole(Long id);
	
	
}
