package com.vivemedellin.gestion_usuarios.repository;

import com.vivemedellin.gestion_usuarios.entity.TokenVerificacion;
import com.vivemedellin.gestion_usuarios.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenVerificacionRepository extends JpaRepository<TokenVerificacion, Long> {
    TokenVerificacion findByToken(String token);
    void deleteByUsuario(Usuario usuario);
}
