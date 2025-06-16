package com.vivemedellin.gestion_usuarios.dto;

import lombok.Data;

import java.util.List;

@Data
public class ActualizacionPerfilDTO {
    private String apodo;
    private String telefono;
    private String biografia;
    private String fotoPerfil;
    private Integer idMunicipio;
    private List<Integer> idsIntereses;
}
