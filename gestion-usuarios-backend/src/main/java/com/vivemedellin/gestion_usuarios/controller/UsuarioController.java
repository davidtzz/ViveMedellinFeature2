/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivemedellin.gestion_usuarios.controller;

import com.vivemedellin.gestion_usuarios.dto.RegistroUsuarioDTO;
import com.vivemedellin.gestion_usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios") // Esta es la URL base de la API
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registrar")
    public ResponseEntity<String> registrarUsuario(@RequestBody @Valid RegistroUsuarioDTO dto) {
        usuarioService.registrarUsuario(dto);
        return ResponseEntity.ok("Usuario registrado exitosamente.");
    }
}
