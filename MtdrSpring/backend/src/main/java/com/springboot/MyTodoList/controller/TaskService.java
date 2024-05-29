package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.model.Usuario;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import com.springboot.MyTodoList.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private ToDoItemRepository toDoItemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<ToDoItem> findTasksByUserIdAndTeamId(int userId) {

        Usuario usuario = usuarioRepository.findById(userId).orElse(null);
        if (usuario == null) {

            return null;
        }

        List<ToDoItem> tasks = toDoItemRepository.findAll();

        int userTeamId = usuario.getEquipo();

        return filterTasksByTeamId(tasks, userTeamId);
    }

    private List<ToDoItem> filterTasksByTeamId(List<ToDoItem> tasks, int teamId) {

        List<ToDoItem> filteredTasks = new ArrayList<>();

        for (ToDoItem task : tasks) {

            int taskUserId = task.getIdUsuario();

            Usuario taskUser = usuarioRepository.findById(taskUserId).orElse(null);
            if (taskUser != null && taskUser.getEquipo() == teamId) {

                filteredTasks.add(task);
            }
        }

        return filteredTasks;
    }
}
