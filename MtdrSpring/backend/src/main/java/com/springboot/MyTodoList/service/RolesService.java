package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Roles;
import com.springboot.MyTodoList.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolesService {

    @Autowired
    private RolesRepository rolesRepository;

    public List<Roles> findAll() {
        return rolesRepository.findAll();
    }

    public ResponseEntity<Roles> getRoleById(int id) {
        Optional<Roles> roleData = rolesRepository.findById(id);
        if (roleData.isPresent()) {
            return new ResponseEntity<>(roleData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Otros métodos para realizar otras operaciones con roles, como crear, actualizar o eliminar
}
