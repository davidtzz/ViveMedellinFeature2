/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivemedellin.gestion_usuarios.controller;


import com.vivemedellin.gestion_usuarios.dto.RegistroComplementarioDTO;
import com.vivemedellin.gestion_usuarios.dto.RegistroUsuarioDTO;
import com.vivemedellin.gestion_usuarios.entity.Interes;
import com.vivemedellin.gestion_usuarios.entity.InteresXUsuario;
import com.vivemedellin.gestion_usuarios.entity.Municipio;
import com.vivemedellin.gestion_usuarios.entity.Usuario;
import com.vivemedellin.gestion_usuarios.repository.InteresRepository;
import com.vivemedellin.gestion_usuarios.repository.InteresXUsuarioRepository;
import com.vivemedellin.gestion_usuarios.repository.MunicipioRepository;
import com.vivemedellin.gestion_usuarios.repository.UsuarioRepository;
import com.vivemedellin.gestion_usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
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
   
}
