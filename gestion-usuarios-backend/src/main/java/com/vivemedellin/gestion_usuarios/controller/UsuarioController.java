/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivemedellin.gestion_usuarios.controller;


import com.vivemedellin.gestion_usuarios.config.JwtUtil;
import com.vivemedellin.gestion_usuarios.dto.ActualizacionPerfilDTO;
import com.vivemedellin.gestion_usuarios.dto.ConfirmacionEliminacionDTO;
import com.vivemedellin.gestion_usuarios.dto.RegistroComplementarioDTO;
import com.vivemedellin.gestion_usuarios.dto.RegistroUsuarioDTO;
import com.vivemedellin.gestion_usuarios.entity.*;
import com.vivemedellin.gestion_usuarios.repository.*;
import com.vivemedellin.gestion_usuarios.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/usuarios") // Esta es la URL base de la API
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MunicipioRepository municipioRepository;

    @Autowired
    private InteresRepository interesRepository;

    @Autowired
    private InteresXUsuarioRepository interesXUsuarioRepository;

    @Autowired
    private TokenVerificacionRepository tokenVerificacionRepository;

    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarUsuario(@RequestBody @Valid RegistroUsuarioDTO dto) {
        usuarioService.registrarUsuario(dto);
        return ResponseEntity.ok("Usuario registrado exitosamente.");
    }
    
    private static final List<String> PALABRAS_INAPROPIADAS = List.of("xxx", "puta", "mierda","pendiente");
    
    @PostMapping("/registro-complementario")
    public ResponseEntity<?> completarRegistro(@RequestBody RegistroComplementarioDTO dto, @RequestParam String email) {
        System.out.println("DTO recibido: " + dto);
        Usuario usuario = usuarioRepository.findByCorreoElectronico(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        
        if (dto.getApodo() != null) {
            if (usuarioRepository.existsByApodo(dto.getApodo())) {
            throw new IllegalArgumentException("El apodo ya está en uso.");
            }
            for (String palabra : PALABRAS_INAPROPIADAS) {
                if (dto.getApodo().toLowerCase().contains(palabra)) {
                throw new IllegalArgumentException("El apodo contiene palabras inapropiadas.");
                }
            }
            usuario.setApodo(dto.getApodo());
        }
        if (dto.getNombre() != null && !dto.getNombre().isBlank()) {
            usuario.setNombre(dto.getNombre());
        }
        if (dto.getSegundoNombre() != null) {
            usuario.setSegundoNombre(dto.getSegundoNombre());
        }
        if (dto.getApellido() != null) {
            usuario.setApellido(dto.getApellido());
        }
        if (dto.getSegundoApellido() != null) {
            usuario.setSegundoApellido(dto.getSegundoApellido());
        }
        if (dto.getTelefono() != null) {
            usuario.setTelefono(dto.getTelefono());
        }
        if (dto.getFotoPerfil() != null) {
            usuario.setFotoPerfil(dto.getFotoPerfil());
        }
        if (dto.getBiografia() != null) {
            usuario.setBiografia(dto.getBiografia());
        }
        if (dto.getFechaNacimiento() != null) {
            usuario.setFechaNacimiento(dto.getFechaNacimiento());
        }
        if (dto.getIdMunicipio() != null) {
            Municipio municipio = municipioRepository.findById(dto.getIdMunicipio())
                .orElseThrow(() -> new IllegalArgumentException("Municipio no válido"));
            usuario.setMunicipio(municipio);
        }
        usuarioRepository.save(usuario);
        if (dto.getIdsIntereses() != null && !dto.getIdsIntereses().isEmpty()) {
            for (Integer idInteres : dto.getIdsIntereses()) {
                Interes interes = interesRepository.findById(idInteres)
                    .orElseThrow(() -> new IllegalArgumentException("Interés con ID " + idInteres + " no válido"));
                InteresXUsuario ixu = new InteresXUsuario();
                ixu.setUsuario(usuario);
                ixu.setInteres(interes);
                interesXUsuarioRepository.save(ixu);
            }
        }


        return ResponseEntity.ok("Registro completado correctamente");
    }

    @GetMapping("/verificar")
    public ResponseEntity<String> verificarCorreo(@RequestParam("token") String token) {
        TokenVerificacion tv = tokenVerificacionRepository.findByToken(token);

        if (tv == null || tv.getExpiracion().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token inválido o expirado.");
        }

        Usuario usuario = tv.getUsuario();
        usuario.setCorreoVerificado(true);
        usuarioRepository.save(usuario);
        tokenVerificacionRepository.delete(tv); // elimina el token tras validarlo

        return ResponseEntity.ok("Correo verificado con éxito.");
    }

    @DeleteMapping("/eliminar")
    public ResponseEntity<?> eliminarCuenta(@RequestBody ConfirmacionEliminacionDTO dto,
                                            @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no proporcionado o inválido");
        }

        String token = authHeader.substring(7); // Quitar "Bearer "
        String correo = jwtUtil.extractEmail(token);

        try {
            usuarioService.eliminarCuenta(correo, dto.getContraseña());
            return ResponseEntity.ok("Cuenta eliminada exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/perfil")
    public ResponseEntity<?> actualizarPerfil(@RequestBody ActualizacionPerfilDTO dto,
                                              @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no proporcionado o inválido");
        }

        String token = authHeader.substring(7); // Quitar "Bearer "
        String correo = jwtUtil.extractEmail(token);

        try {
            usuarioService.actualizarPerfil(correo, dto);
            return ResponseEntity.ok("Perfil actualizado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
