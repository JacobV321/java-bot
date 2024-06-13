package com.springboot.MyTodoList.util;

public enum BotMessages {
	
	HELLO_MYTODO_BOT(
	"👋 ¡Hola! Soy MyTodoList Bot!\nEscribe una nueva tarea abajo y presiona el botón de enviar (flecha azul), o selecciona una opción a continuación:"),
	BOT_REGISTERED_STARTED("✅ ¡Bot registrado y comenzado exitosamente!"),
	ITEM_DONE("✔️ ¡Tarea completada! Selecciona /todolist para volver a la lista de tareas, o /start para ir a la pantalla principal."), 
	ITEM_UNDONE("🔄 ¡Tarea marcada como no hecha! Selecciona /todolist para volver a la lista de tareas, o /start para ir a la pantalla principal."), 
	ITEM_DELETED("🗑️ ¡Tarea eliminada! Selecciona /todolist para volver a la lista de tareas, o /start para ir a la pantalla principal."),
	TYPE_NEW_TODO_ITEM("📝 Escribe una nueva tarea abajo y presiona el botón de enviar (flecha azul) en el lado derecho."),
	NEW_ITEM_ADDED("➕ ¡Nueva tarea añadida! Selecciona /todolist para volver a la lista de tareas, o /start para ir a la pantalla principal."),
	BYE("👋 ¡Adiós! Selecciona /start para continuar!");

	private String message;

	BotMessages(String enumMessage) {
		this.message = enumMessage;
	}

	public String getMessage() {
		return message;
	}

}
