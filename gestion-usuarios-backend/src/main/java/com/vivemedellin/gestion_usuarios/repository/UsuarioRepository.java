/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.vivemedellin.gestion_usuarios.repository;

import com.vivemedellin.gestion_usuarios.entity.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author David
 */
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    boolean existsByCorreoElectronico(String correoElectronico);
    boolean existsByApodo(String apodo);
    Optional<Usuario> findByCorreoElectronico(String correoElectronico);
}

