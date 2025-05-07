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

@Entity
@Table(name = "tblinteresesxusuario")
public class InteresXUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_interesesxusuario")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "apodo", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_interes", nullable = false)
    private Interes interes;

    // Getters y setters (puedes usar Lombok si lo prefieres)

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Interes getInteres() {
        return interes;
    }

    public void setInteres(Interes interes) {
        this.interes = interes;
    }
}
