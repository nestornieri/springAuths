package com.example.clase1;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class MainT {
    public static void main(String[] args) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode("passnest");
        System.out.println("Contraseña encriptada: " + hashedPassword);
    }
}
