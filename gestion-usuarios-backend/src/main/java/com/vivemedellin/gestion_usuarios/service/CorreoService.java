package com.vivemedellin.gestion_usuarios.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class CorreoService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarCorreoVerificacion(String correoDestino, String link) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(correoDestino);
        mensaje.setSubject("Confirma tu cuenta");
        mensaje.setText("Haz clic en el siguiente enlace para verificar tu correo: " + link);
        mailSender.send(mensaje);
    }

}
