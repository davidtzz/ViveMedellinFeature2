/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vivemedellin.gestion_usuarios.entity;

/**
 *
 * @author David
 */
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "tblinteresesxusuario")
@Data
public class InteresXUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_interesesxusuario")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_interes", nullable = false)
    private Interes interes;
}
