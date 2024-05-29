package com.springboot.MyTodoList.model;

import javax.persistence.*;

@Entity
@Table(name = "Usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;

    @ManyToOne
    @JoinColumn(name = "ID_ROL")
    private Roles rol;

    @Column(name = "ID_EQUIPO")
    private int equipo;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Password")
    private String password;

    // Getters and setters
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Roles getRol() {
        return rol;
    }

    public void setRol(Roles rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEquipo(int equipo) {
        this.equipo = equipo;
    }

    public int getEquipo() {
        return equipo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}