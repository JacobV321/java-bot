
package com.springboot.MyTodoList.controller;

import java.util.ArrayList;
import java.util.List;

public class UserAuthentication {
    
    // Definir una estructura para almacenar las credenciales válidas
    private List<String[]> validCredentials;

    // Constructor para inicializar las credenciales válidas
    public UserAuthentication() {
        validCredentials = new ArrayList<>();
        // Agregar credenciales válidas (nombre, contraseña, rol)
        validCredentials.add(new String[]{"devuser", "devpass", "Dev"});
        validCredentials.add(new String[]{"adminuser", "adminpass", "Manager"});
    }

    // Método para verificar si las credenciales son válidas
    public String[] isAuthenticated(String username, String password) {
        // Verificar las credenciales en la estructura de datos
        for (String[] credentials : validCredentials) {
            if (credentials[0].equals(username) && credentials[1].equals(password)) {
                return new String[]{"true", credentials[0], credentials[2]}; // Usuario autenticado correctamente
            }
        }
        return new String[]{"false", "Credenciales inválidas. Por favor, inténtalo de nuevo."}; // Usuario no autenticado
    }
}
