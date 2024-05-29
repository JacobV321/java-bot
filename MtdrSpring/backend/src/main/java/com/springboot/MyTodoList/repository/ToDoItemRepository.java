package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.ToDoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
@EnableTransactionManagement
public interface ToDoItemRepository extends JpaRepository<ToDoItem, Integer> {

    // Método para obtener todas las tareas por ID de usuario
    List<ToDoItem> findAllByidUsuario(int idUsuario);

    List<ToDoItem> findAllByidUsuarioIn(List<Integer> idUsuarios);
}
