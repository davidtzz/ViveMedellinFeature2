/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivemedellin.gestion_usuarios.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import lombok.Data;

@Entity
@Table(name = "tblusuarios")
@Data
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name="id_usuario", nullable=false)
    private Integer id_usuario;
    
    @NotBlank
    @Column(name = "apodo", nullable = false)
    private String apodo;

    @NotBlank
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "segundo_nombre")
    private String segundoNombre;

    @NotBlank
    @Column(name = "apellido", nullable = false)
    private String apellido;

    
    @Column(name = "segundo_apellido")
    private String segundoApellido;

    @Email
    @NotBlank
    @Column(name = "correo_electronico", unique = true, nullable = false)
    private String correoElectronico;

    @NotBlank
    @Column(name = "contrasena", nullable = false)
    private String contraseña; // Aquí se almacenará cifrada (bcrypt u otra)

    
    @Column(name = "foto_perfil")
    private String fotoPerfil; // URL a la imagen

    @Column(name = "biografia")
    private String biografia;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(name = "correo_verificado", nullable = false)
    private boolean correoVerificado = false;
    
    @Column(name = "registrado_manual", nullable = false)
    private boolean registradoManual = true;
    
    @ManyToOne
    @JoinColumn(name = "municipio_residencia", referencedColumnName = "id_municipio")
    @NotNull
    private Municipio municipio;
}
