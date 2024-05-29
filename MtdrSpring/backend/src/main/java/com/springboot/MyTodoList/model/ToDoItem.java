package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.time.OffsetDateTime;

/*
    representation of the TODOITEM table that exists already
    in the autonomous database
 */
@Entity
@Table(name = "TODOITEM")
public class ToDoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int ID;
    @ManyToOne
    @JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID")
    int idUsuario;
    @Column(name = "DESCRIPTION", length = 255)
    String description;
    @Column(name = "CREATION_TS", length = 255)
    OffsetDateTime creation_ts;
    @Column(name = "done")
    boolean done;

    public ToDoItem() {

    }

    public ToDoItem(int ID, int idUsuario, String description, OffsetDateTime creation_ts, boolean done) {
        this.ID = ID;
        this.idUsuario = idUsuario; // Inicializar el campo idUsuario
        this.description = description;
        this.creation_ts = creation_ts;
        this.done = done;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getIdUsuario() {
        return idUsuario; // Getter para idUsuario
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario; // Setter para idUsuario
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getCreation_ts() {
        return creation_ts;
    }

    public void setCreation_ts(OffsetDateTime creation_ts) {
        this.creation_ts = creation_ts;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    public String toString() {
        return "ToDoItem{" +
                "ID=" + ID +
                ", idUsuario=" + idUsuario +
                ", description='" + description + '\'' +
                ", creation_ts=" + creation_ts +
                ", done=" + done +
                '}';
    }
}
