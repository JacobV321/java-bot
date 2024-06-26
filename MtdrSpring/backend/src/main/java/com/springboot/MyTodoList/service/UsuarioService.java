package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Usuario;
import com.springboot.MyTodoList.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> findAllByIdEquipo(int idEquipo) {
        return usuarioRepository.findAllByIdEquipo(idEquipo);
    }

}
