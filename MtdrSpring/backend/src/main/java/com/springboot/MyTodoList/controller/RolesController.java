package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Roles;
import com.springboot.MyTodoList.service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RolesController {

    @Autowired
    private RolesService rolesService;

    @GetMapping
    public List<Roles> getAllRoles() {
        return rolesService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Roles> getRoleById(@PathVariable int id) {
        ResponseEntity<Roles> responseEntity = rolesService.getRoleById(id);
        return new ResponseEntity<>(responseEntity.getBody(), responseEntity.getStatusCode());
    }

    // Aquí podrías agregar métodos adicionales para crear, actualizar y eliminar roles si lo deseas

}
