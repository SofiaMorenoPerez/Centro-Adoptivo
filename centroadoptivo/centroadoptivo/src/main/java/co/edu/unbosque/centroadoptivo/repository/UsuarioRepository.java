package co.edu.unbosque.centroadoptivo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unbosque.centroadoptivo.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	
	public Optional<Usuario> findByUsername(String username);

	
	public void deleteByUsername(String username); 
}
