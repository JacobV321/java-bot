package com.springboot.MyTodoList.util;

import com.springboot.MyTodoList.model.Roles;
import com.springboot.MyTodoList.service.RolesService;

import java.util.List;

public enum BotMessages {

    HELLO_MYTODO_BOT("Hello! I'm MyTodoList Bot!\nType a new todo item below and press the send button (blue arrow), or select an option below:\nNumber of roles: %d"),

    BOT_REGISTERED_STARTED("Bot registered and started successfully!"),
    ITEM_DONE("Item done! Select /todolist to return to the list of todo items, or /start to go to the main screen."),
    ITEM_UNDONE("Item undone! Select /todolist to return to the list of todo items, or /start to go to the main screen."),
    ITEM_DELETED("Item deleted! Select /todolist to return to the list of todo items, or /start to go to the main screen."),
    TYPE_NEW_TODO_ITEM("Type a new todo item below and press the send button (blue arrow) on the right-hand side."),
    NEW_ITEM_ADDED("New item added! Select /todolist to return to the list of todo items, or /start to go to the main screen."),
    BYE("Bye! Select /start to resume!");

    private String message;

    BotMessages(String enumMessage) {
        this.message = enumMessage;
    }

    public String getMessage() {
        return message;
    }

    public String getMessage(int numberOfRoles) {
        return String.format(message, numberOfRoles);
    }

    public static String getHelloMessage() {
        RolesService rolesService = new RolesService();
        List<Roles> roles = rolesService.findAll();
        int numberOfRoles = roles.size();
        return String.format(HELLO_MYTODO_BOT.getMessage(), numberOfRoles);
    }
}
