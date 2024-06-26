package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.model.Usuario;
import com.springboot.MyTodoList.repository.ToDoItemRepository;
import com.springboot.MyTodoList.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ToDoItemService {

    @Autowired
    private ToDoItemRepository toDoItemRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;


    public List<ToDoItem> findAll(){
        List<ToDoItem> todoItems = toDoItemRepository.findAll();
        return todoItems;
    }

    public List<ToDoItem> findAllByEquipo(int idEquipo) {
        List<Usuario> usuarios = usuarioRepository.findAllByIdEquipo(idEquipo);
        List<Integer> userIds = usuarios.stream().map(Usuario::getID).collect(Collectors.toList());
        return toDoItemRepository.findAllByIdUsuarioIn(userIds);
    }





    // Nuevo método para obtener tareas por ID de usuario
    public List<ToDoItem> findAllByidUsuario(int idUsuario) {
        return toDoItemRepository.findAllByidUsuario(idUsuario);
    }


    public ResponseEntity<ToDoItem> getItemById(int id){
        Optional<ToDoItem> todoData = toDoItemRepository.findById(id);
        if (todoData.isPresent()){
            return new ResponseEntity<>(todoData.get(), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    public ToDoItem addToDoItem(ToDoItem toDoItem){
        return toDoItemRepository.save(toDoItem);
    }

    public boolean deleteToDoItem(int id){
        try{
            toDoItemRepository.deleteById(id);
            return true;
        }catch(Exception e){
            return false;
        }
    }
    public ToDoItem updateToDoItem(int id, ToDoItem td){
        Optional<ToDoItem> toDoItemData = toDoItemRepository.findById(id);
        if(toDoItemData.isPresent()){
            ToDoItem toDoItem = toDoItemData.get();
            toDoItem.setID(id);
            toDoItem.setCreation_ts(td.getCreation_ts());
            toDoItem.setDescription(td.getDescription());
            toDoItem.setDone(td.isDone());
            return toDoItemRepository.save(toDoItem);
        }else{
            return null;
        }
    }

}
