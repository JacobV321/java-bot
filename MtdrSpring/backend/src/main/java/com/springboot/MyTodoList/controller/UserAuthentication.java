
package com.springboot.MyTodoList.controller;

import java.util.HashMap;
import java.util.Map;

public class UserAuthentication {
    
    // Definir una estructura para almacenar las credenciales válidas
    private Map<String, String> validCredentials;

    // Constructor para inicializar las credenciales válidas
    public UserAuthentication() {
        validCredentials = new HashMap<>();
        // Agregar credenciales válidas (usuario_contraseña)
        validCredentials.put("devuser", "devpass");
        validCredentials.put("adminuser", "adminpass");
    }

    // Método para verificar si las credenciales son válidas
    public boolean isAuthenticated(String credentials) {
        // Separar el usuario y la contraseña
        String[] parts = credentials.split("_");
        if (parts.length != 2) {
            return false; // Formato incorrecto
        }
        String username = parts[0];
        String password = parts[1];

        // Verificar las credenciales en la estructura de datos
        return validCredentials.containsKey(username) && validCredentials.get(username).equals(password);
    }
}
