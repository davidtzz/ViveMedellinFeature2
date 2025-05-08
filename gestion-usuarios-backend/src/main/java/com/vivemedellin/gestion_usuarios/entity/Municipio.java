/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivemedellin.gestion_usuarios.entity;

/**
 *
 * @author David
 */

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Data;

@Entity
@Table(name = "tblmunicipios")
@Data
public class Municipio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_municipio")
    private Integer id;

    @NotBlank
    @Column(name = "nombre_municipio")
    private String nombre;

    // Relación con Usuario
    @OneToMany(mappedBy = "municipio")
    @JsonIgnore
    private List<Usuario> usuarios;
}
