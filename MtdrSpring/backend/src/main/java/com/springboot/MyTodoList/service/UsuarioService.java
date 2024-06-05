package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Usuario;
import com.springboot.MyTodoList.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;


@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> findAllByIdEquipo(int idEquipo) {
        return usuarioRepository.findAllByIdEquipo(idEquipo);
    }

    public Usuario findById(int id) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            logger.info("Usuario encontrado: " + usuario.getNombre() + " (ID: " + id + ")");
            return usuario;
        } else {
            logger.error("Usuario no encontrado para ID: " + id);
            return null;
        }
    }
}
