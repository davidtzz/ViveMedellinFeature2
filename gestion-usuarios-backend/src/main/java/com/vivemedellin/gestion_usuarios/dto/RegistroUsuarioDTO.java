/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivemedellin.gestion_usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 *
 * @author David
 */
@Data
public class RegistroUsuarioDTO {

    @NotBlank
    private String apodo;

    @NotBlank
    private String nombre;

    private String segundoNombre;

    @NotBlank
    private String apellido;

    private String segundoApellido;

    @NotBlank
    @Email
    private String correoElectronico;

    @NotBlank
    private String contraseña;

    @NotBlank
    private String confirmarContraseña;

    private String fotoPerfil;

    private String biografia;

    private String telefono;

    @NotNull
    private LocalDate fechaNacimiento;

    @NotNull
    private Integer idMunicipio;

    @NotNull
    private List<Integer> idsIntereses;



}
