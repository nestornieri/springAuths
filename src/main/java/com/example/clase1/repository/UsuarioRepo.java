package com.example.clase1.repository;

import com.example.clase1.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepo extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
}