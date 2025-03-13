package com.example.clase1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api1")
public class hello {
    @GetMapping("/hola")
    public String saludar() {
        return "Bienvenidos al Curso de API Rest";
    }

    @GetMapping("/publico")
    public String publico() {
        return "Bienvenidos a mi API Rest Público";
    }

    @GetMapping("/publico/items")
    public String publicoItems() {
        return "Bienvenidos a mi API Rest Público Items";
    }

    @GetMapping("/privado")
    public String privado() {
        return "Bienvenidos a mi API Rest Privado";
    }

    @GetMapping("/user")
    public String user() {
        return "API para validar roles de usuario";
    }

    @GetMapping("/admin")
    public String admin() {
        return "API para validar roles de administrador";
    }
}