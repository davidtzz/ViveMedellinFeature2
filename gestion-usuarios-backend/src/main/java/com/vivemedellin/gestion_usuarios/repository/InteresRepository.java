/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.vivemedellin.gestion_usuarios.repository;

import com.vivemedellin.gestion_usuarios.entity.Interes;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad Interes.
 */
public interface InteresRepository extends JpaRepository<Interes, Integer> {
}

