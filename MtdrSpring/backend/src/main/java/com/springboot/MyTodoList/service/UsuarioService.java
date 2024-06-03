package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Usuario;
import com.springboot.MyTodoList.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> findAllByIdEquipo(int idEquipo) {
        return usuarioRepository.findAllByIdEquipo(idEquipo);
    }

    public String findUserNameById(int id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        return (usuario != null) ? usuario.getNombre() : null;
    }

}
    
