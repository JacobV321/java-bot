package com.springboot.MyTodoList.model;

import javax.persistence.*;

/*
    Representación de la tabla Roles que ya existe en la base de datos autónoma.
*/
@Entity
@Table(name = "Roles") // Nombre de la tabla en la base de datos
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "Nombre") // Nombre de la columna en la tabla
    private String nombre;

    public Roles() {
        // Constructor vacío necesario para JPA
    }

    public Roles(String nombre) {
        this.nombre = nombre;
    }

    // Getters y setters

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Roles{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
