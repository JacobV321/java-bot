package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Usuario;
import com.springboot.MyTodoList.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserAuthentication {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public String[] isAuthenticated(String username, String password) {
        Usuario usuario = usuarioRepository.findByNombre(username);
        if (usuario != null && usuario.getPassword().equals(password)) {
            return new String[]{"true", usuario.getNombre(), usuario.getRol().getNombre(), String.valueOf(usuario.getID())};
        }
        return new String[]{"false", "Credenciales inválidas. Por favor, inténtalo de nuevo."};
    }
}