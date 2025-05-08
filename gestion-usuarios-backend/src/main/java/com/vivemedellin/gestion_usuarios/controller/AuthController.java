/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivemedellin.gestion_usuarios.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.vivemedellin.gestion_usuarios.entity.Usuario;
import com.vivemedellin.gestion_usuarios.service.UsuarioService;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author David
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/google")
    public ResponseEntity<?> autenticarConGoogle(@RequestBody Map<String, String> request) throws IOException {
        String idToken = request.get("idToken");

        GoogleIdToken.Payload payload = verificarTokenGoogle(idToken);

        if (payload == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
        System.out.println("Payload: " + payload.toPrettyString());
        String email = payload.getEmail();

        Usuario usuario = usuarioService.autenticarOCrearUsuarioDesdeGoogle(email);
        return ResponseEntity.ok(usuario);
    }
    
    private GoogleIdToken.Payload verificarTokenGoogle(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList("604384290651-21e4ksnnivctu7ved76u9nvv35i9j5tc.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            return (idToken != null) ? idToken.getPayload() : null;

        } catch (Exception e) {
            return null;
        }
    }
}

