package com.vivemedellin.gestion_usuarios.service;

import com.vivemedellin.gestion_usuarios.dto.RegistroUsuarioDTO;
import com.vivemedellin.gestion_usuarios.entity.*;
import com.vivemedellin.gestion_usuarios.repository.*;
import com.vivemedellin.gestion_usuarios.dto.ActualizacionPerfilDTO;
import java.time.LocalDateTime;
import java.util.UUID;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class UsuarioService {

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
    private CorreoService correoService;

    private static final List<String> PALABRAS_INAPROPIADAS = List.of("xxx", "puta", "mierda","pendiente");
    
    public Usuario autenticarOCrearUsuarioDesdeGoogle(String email) {
        return usuarioRepository.findByCorreoElectronico(email)
                .orElseGet(() -> {
                    Usuario nuevoUsuario = new Usuario();
                    Municipio municipio = new Municipio();
                    municipio.setId(1);
                    nuevoUsuario.setCorreoElectronico(email);
                    nuevoUsuario.setNombre("PENDIENTE");
                    nuevoUsuario.setApellido("PENDIENTE");
                    nuevoUsuario.setApodo(UUID.randomUUID().toString().substring(0, 8)); // apodo temporal único
                    nuevoUsuario.setContraseña(encriptarPassword("Pendiente")); // o algún placeholder
                    nuevoUsuario.setFechaNacimiento(LocalDate.of(1900, 1, 1)); // placeholder válido
                    nuevoUsuario.setCorreoVerificado(true);
                    nuevoUsuario.setRegistradoManual(false);
                    nuevoUsuario.setMunicipio(municipio);// si ya viene verificado por Google
//                    nuevoUsuario.setRegistradoCon("GOOGLE");
                    return usuarioRepository.save(nuevoUsuario);
                });
    }   

    public void registrarUsuario(RegistroUsuarioDTO dto) {

        if (usuarioRepository.existsByApodo(dto.getApodo())) {
            throw new IllegalArgumentException("El apodo ya está en uso.");
        }

        for (String palabra : PALABRAS_INAPROPIADAS) {
            if (dto.getApodo().toLowerCase().contains(palabra)) {
                throw new IllegalArgumentException("El apodo contiene palabras inapropiadas.");
            }
        }

        // Validar correo
        if (usuarioRepository.existsByCorreoElectronico(dto.getCorreoElectronico())) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }

        // Validar contraseña
        String password = dto.getContraseña();
        if (!password.equals(dto.getConfirmarContraseña())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }

        if (!validarPassword(password)) {
            throw new IllegalArgumentException("La contraseña no cumple con los requisitos.");
        }

        // Crear entidad Usuario
        Usuario u = new Usuario();
        u.setApodo(dto.getApodo());
        u.setNombre(dto.getNombre());
        u.setSegundoNombre(dto.getSegundoNombre());
        u.setApellido(dto.getApellido());
        u.setSegundoApellido(dto.getSegundoApellido());
        u.setCorreoElectronico(dto.getCorreoElectronico());
        u.setContraseña(encriptarPassword(password));
        u.setFotoPerfil(dto.getFotoPerfil());
        u.setBiografia(dto.getBiografia());
        u.setTelefono(dto.getTelefono());
        u.setFechaNacimiento(dto.getFechaNacimiento());

        // Asociar municipio
        Municipio municipio = municipioRepository.findById(dto.getIdMunicipio())
            .orElseThrow(() -> new IllegalArgumentException("Municipio no válido"));
        u.setMunicipio(municipio);

        usuarioRepository.save(u); // Guarda primero el usuario para tener su apodo como FK

        // Asociar interés
        for (Integer idInteres : dto.getIdsIntereses()) {
    Interes interes = interesRepository.findById(idInteres)
        .orElseThrow(() -> new IllegalArgumentException("Interés con ID " + idInteres + " no válido"));

    InteresXUsuario ixu = new InteresXUsuario();
    ixu.setUsuario(u);
    ixu.setInteres(interes);

    interesXUsuarioRepository.save(ixu);
}
        String token = UUID.randomUUID().toString();
        TokenVerificacion tv = new TokenVerificacion();
        tv.setToken(token);
        tv.setUsuario(u);
        tv.setExpiracion(LocalDateTime.now().plusHours(24)); // válido por 24h

        tokenVerificacionRepository.save(tv);

// Enviar correo
        String link = "https://vivemedellinfeature2.onrender.com/api/usuarios/verificar?token=" + token;
        correoService.enviarCorreoVerificacion(u.getCorreoElectronico(), link);
    }

    private boolean validarPassword(String password) {
    if (password.length() < 8) {
        return false;
    }

    boolean tieneMayuscula = false;
    int cantidadNumeros = 0;
    boolean tieneEspecial = false;

    for (char c : password.toCharArray()) {
        if (Character.isUpperCase(c)) {
            tieneMayuscula = true;
        } else if (Character.isDigit(c)) {
            cantidadNumeros++;
        } else if ("!@#$%^&*()_+-={}[]:;\"'<>,.?/\\|".contains(String.valueOf(c))) {
            tieneEspecial = true;
        }
    }

    return tieneMayuscula && cantidadNumeros >= 3 && tieneEspecial;
}
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void eliminarCuenta(String correo, String contraseñaConfirmacion) {
        Usuario usuario = usuarioRepository.findByCorreoElectronico(correo)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!passwordEncoder.matches(contraseñaConfirmacion, usuario.getContraseña())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        // Si hay relaciones en cascada o tablas relacionadas, manéjalo aquí si no tienes cascade delete
        tokenVerificacionRepository.deleteByUsuario(usuario);
        interesXUsuarioRepository.deleteByUsuario(usuario);
        usuarioRepository.delete(usuario);
    }

    @Transactional
    public void actualizarPerfil(String correoUsuario, ActualizacionPerfilDTO dto) {
        Usuario usuario = usuarioRepository.findByCorreoElectronico(correoUsuario)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Validar y actualizar apodo solo si fue enviado
        if (dto.getApodo() != null && !dto.getApodo().equals(usuario.getApodo())) {
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

        // Actualizar otros campos si vienen
        if (dto.getTelefono() != null) usuario.setTelefono(dto.getTelefono());
        if (dto.getBiografia() != null) usuario.setBiografia(dto.getBiografia());
        if (dto.getFotoPerfil() != null) usuario.setFotoPerfil(dto.getFotoPerfil());

        // Actualizar municipio si viene
        if (dto.getIdMunicipio() != null) {
            Municipio municipio = municipioRepository.findById(dto.getIdMunicipio())
                    .orElseThrow(() -> new IllegalArgumentException("Municipio no válido"));
            usuario.setMunicipio(municipio);
        }

        // Actualizar intereses si viene la lista
        if (dto.getIdsIntereses() != null) {
            interesXUsuarioRepository.deleteByUsuario(usuario);

            for (Integer idInteres : dto.getIdsIntereses()) {
                Interes interes = interesRepository.findById(idInteres)
                        .orElseThrow(() -> new IllegalArgumentException("Interés con ID " + idInteres + " no válido"));
                InteresXUsuario ixu = new InteresXUsuario();
                ixu.setUsuario(usuario);
                ixu.setInteres(interes);
                interesXUsuarioRepository.save(ixu);
            }
        }

        usuarioRepository.save(usuario);
    }


    private String encriptarPassword(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }
}