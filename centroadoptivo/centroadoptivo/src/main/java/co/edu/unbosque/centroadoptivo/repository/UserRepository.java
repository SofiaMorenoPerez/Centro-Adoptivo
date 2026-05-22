package co.edu.unbosque.centroadoptivo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.centroadoptivo.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	
	public Optional<User> findByUsername(String username);
	public void deleteByUsername(String username); 
	Optional<User> findByEmail(String email);
	boolean existsByUsername(String username);
	
}