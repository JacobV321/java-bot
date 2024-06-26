package com.springboot.MyTodoList.controller;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;

public class ToDoItemBotController extends TelegramLongPollingBot {

	private static final Logger logger = LoggerFactory.getLogger(ToDoItemBotController.class);
	private ToDoItemService toDoItemService;

	private String botName;
	private UserAuthentication userAuthentication;

	private Map<Long, Boolean> authenticatedUsers = new HashMap<>();
	private Map<Long, Integer> authenticatedUserIds = new HashMap<>(); // Nuevo mapa para almacenar ID de usuarios
																		// autenticados

	private String status;
	private int userID;
	private int idEquipo;

	public ToDoItemBotController(String botToken, String botName, ToDoItemService toDoItemService,
			UserAuthentication userAuthentication) {
		super(botToken);
		logger.info("Bot Token: " + botToken);
		logger.info("Bot name: " + botName);
		this.toDoItemService = toDoItemService;
		this.botName = botName;
		this.userAuthentication = userAuthentication;
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			String messageTextFromTelegram = update.getMessage().getText();
			long chatId = update.getMessage().getChatId();

			// Verificar si el usuario está autenticado antes de manejar cualquier comando
			Boolean isLoggedIn = authenticatedUsers.get(chatId);
			if (isLoggedIn == null || !isLoggedIn) {
				// Si el usuario no está autenticado y el comando no es /login, enviar mensaje
				// de error
				if (!messageTextFromTelegram.startsWith(BotCommands.LOG_IN.getCommand())) {
					sendErrorMessage(chatId, "🔒 Por favor, haz el /login primero.");
					return;
				}
			}

			// Procesar comando de login por separado
			if (messageTextFromTelegram.startsWith(BotCommands.LOG_IN.getCommand())) {
				// Lógica de autenticación
				String[] parts = messageTextFromTelegram.split("\\s+", 3);
				if (parts.length != 3) {
					sendErrorMessage(chatId,
							"❗ Por favor, introduce tu nombre de usuario y contraseña en el siguiente formato: /login usuario contraseña");
					return;
				}

				String username = parts[1];
				String password = parts[2];
				String[] authenticationResult = userAuthentication.isAuthenticated(username, password);
				if (authenticationResult[0].equals("true")) {
					authenticatedUsers.put(chatId, true);
					authenticatedUserIds.put(chatId, Integer.parseInt(authenticationResult[3])); // Almacenar ID del
																									// usuario
																									// autenticado
					String name = authenticationResult[1];
					String role = authenticationResult[2];
					status = authenticationResult[2];
					userID = Integer.parseInt(authenticationResult[3]);
					idEquipo = Integer.parseInt(authenticationResult[4]);
					sendSuccessMessage(chatId, "👋 ¡Hola " + name + "! Tienes un rol de " + role);

					// Mostrar el teclado principal después del login exitoso
					handleStartCommand(chatId, role, idEquipo);

				} else {
					sendErrorMessage(chatId, authenticationResult[1]);
				}
			} else if (messageTextFromTelegram.equals(BotCommands.LOG_OUT.getCommand())
					|| messageTextFromTelegram.equals(BotLabels.LOG_OUT.getLabel())) {
				// Lógica de cierre de sesión
				authenticatedUsers.remove(chatId); // Eliminar al usuario autenticado
				authenticatedUserIds.remove(chatId); // Eliminar el ID del usuario autenticado

				sendSuccessMessage(chatId,
						"🔓 ¡Sesión cerrada exitosamente! Puedes usar /login para iniciar sesión nuevamente.");
			} else {
				// Manejar otros comandos según el rol del usuario
				handleUserCommands(chatId, messageTextFromTelegram, status, userID, idEquipo);
			}
		}
	}

	private void handleUserCommands(long chatId, String messageTextFromTelegram, String role, int userID,
			int idEquipo) {
		if (role.equals("dev")) {
			// Lógica para el rol de desarrollador
			if (messageTextFromTelegram.equals(BotCommands.START_COMMAND.getCommand())
					|| messageTextFromTelegram.equals(BotLabels.SHOW_MAIN_SCREEN.getLabel())) {
				handleStartCommand(chatId, role, idEquipo);
			} else if (messageTextFromTelegram.contains(BotLabels.DONE.getLabel())) {
				handleDoneCommand(chatId, messageTextFromTelegram);
			} else if (messageTextFromTelegram.contains(BotLabels.UNDO.getLabel())) {
				handleUndoCommand(chatId, messageTextFromTelegram);
			} else if (messageTextFromTelegram.contains(BotLabels.DELETE.getLabel())) {
				handleDeleteCommand(chatId, messageTextFromTelegram);
			} else if (messageTextFromTelegram.equals(BotCommands.HIDE_COMMAND.getCommand())
					|| messageTextFromTelegram.equals(BotLabels.HIDE_MAIN_SCREEN.getLabel())) {
				handleHideCommand(chatId);
			} else if (messageTextFromTelegram.equals(BotCommands.TODO_LIST.getCommand())
					|| messageTextFromTelegram.equals(BotLabels.LIST_ALL_ITEMS.getLabel())
					|| messageTextFromTelegram.equals(BotLabels.MY_TODO_LIST.getLabel())) {
				handleToDoListCommand(chatId, userID);
			} else if (messageTextFromTelegram.equals(BotCommands.ADD_ITEM.getCommand())
					|| messageTextFromTelegram.equals(BotLabels.ADD_NEW_ITEM.getLabel())) {
				handleAddItemCommand(chatId);
			} else {
				handleNewItem(chatId, userID, messageTextFromTelegram);
			}
		} else if (role.equals("admin")) {
			// Lógica para el rol de administrador
			if (messageTextFromTelegram.equals(BotLabels.TEAM_LIST.getLabel())) {
				handleTeamListCommand(chatId, idEquipo);
			} else {
				sendErrorMessage(chatId, "❗ Comando no reconocido para el rol admin.");
			}
		} else {
			// Otros roles aquí
			sendErrorMessage(chatId, "❗ Rol no reconocido.");
		}
	}

	private void handleStartCommand(long chatId, String role, int idEquipo) {
		if (role.equals("dev")) {
			// Lógica para el rol de desarrollador
			SendMessage messageToTelegram = new SendMessage();
			messageToTelegram.setChatId(chatId);
			messageToTelegram.setText("👨‍💻 ¡Bienvenido! Selecciona una opción:");

			ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
			List<KeyboardRow> keyboard = new ArrayList<>();

			KeyboardRow row1 = new KeyboardRow();
			row1.add(BotLabels.LIST_ALL_ITEMS.getLabel());
			keyboard.add(row1);

			KeyboardRow row2 = new KeyboardRow();
			row2.add(BotLabels.ADD_NEW_ITEM.getLabel());
			keyboard.add(row2);

			KeyboardRow row3 = new KeyboardRow();
			row3.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
			row3.add(BotLabels.HIDE_MAIN_SCREEN.getLabel());
			keyboard.add(row3);

			KeyboardRow row4 = new KeyboardRow();
			row4.add(BotLabels.LOG_OUT.getLabel());
			keyboard.add(row4);

			keyboardMarkup.setKeyboard(keyboard);
			messageToTelegram.setReplyMarkup(keyboardMarkup);

			try {
				execute(messageToTelegram);
			} catch (TelegramApiException e) {
				logger.error(e.getLocalizedMessage(), e);
				sendErrorMessage(chatId, "❗ Error al mostrar el menú principal: " + e.getLocalizedMessage());
			}
		} else if (role.equals("admin")) {
			SendMessage messageToTelegram = new SendMessage();
			messageToTelegram.setChatId(chatId);
			messageToTelegram.setText("🛠 Opciones de Manager:");

			ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
			List<KeyboardRow> keyboard = new ArrayList<>();

			KeyboardRow row1 = new KeyboardRow();
			row1.add(BotLabels.TEAM_LIST.getLabel());
			keyboard.add(row1);

			KeyboardRow row2 = new KeyboardRow();
			row2.add(BotLabels.LOG_OUT.getLabel());
			keyboard.add(row2);

			keyboardMarkup.setKeyboard(keyboard);
			messageToTelegram.setReplyMarkup(keyboardMarkup);

			try {
				execute(messageToTelegram);
			} catch (TelegramApiException e) {
				logger.error(e.getLocalizedMessage(), e);
				sendErrorMessage(chatId, "❗ Error al mostrar el menú principal: " + e.getLocalizedMessage());
			}
		} else {
			sendErrorMessage(chatId, "Rol no reconocido.");
		}
	}

	private void handleTeamListCommand(long chatId, int idEquipo) {
		// Obtener las tareas del equipo
		List<ToDoItem> teamItems = toDoItemService.findAllByEquipo(idEquipo);

		// Crear el mensaje de respuesta con la lista de tareas
		StringBuilder responseText = new StringBuilder("📋 Tareas del equipo:\n");
		for (ToDoItem item : teamItems) {
			try {
				// Añadir ID de usuario y descripción al mensaje de respuesta
				responseText.append("ID: ")
						.append(item.getIdUsuario())
						.append("/ ")
						.append(item.getDescription())
						.append("\n");
			} catch (Exception e) {
				// Log de cualquier excepción durante el procesamiento de las tareas
				logger.error("Error procesando tarea ID: " + item.getID() + " - " + e.getLocalizedMessage(), e);
			}
		}

		// Enviar el mensaje final con la lista de tareas
		SendMessage messageToTelegram = new SendMessage();
		messageToTelegram.setChatId(chatId);
		messageToTelegram.setText(responseText.toString());

		try {
			execute(messageToTelegram);
		} catch (TelegramApiException e) {
			// Log de error al enviar el mensaje
			logger.error("Error al enviar mensaje al chatId: " + chatId + " - " + e.getLocalizedMessage(), e);
			sendErrorMessage(chatId, "Error al mostrar la lista de tareas del equipo: " + e.getLocalizedMessage());
		}
	}

	public void handleDoneCommand(long chatId, String messageTextFromTelegram) {
		String done = messageTextFromTelegram.substring(0, messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
		Integer id = Integer.valueOf(done);

		try {
			ToDoItem item = getToDoItemById(id).getBody();
			item.setDone(true);
			updateToDoItem(item, id);
			BotHelper.sendMessageToTelegram(chatId, BotMessages.ITEM_DONE.getMessage(), this);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	public void handleUndoCommand(long chatId, String messageTextFromTelegram) {
		String undo = messageTextFromTelegram.substring(0, messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
		Integer id = Integer.valueOf(undo);

		try {
			ToDoItem item = getToDoItemById(id).getBody();
			item.setDone(false);
			updateToDoItem(item, id);
			BotHelper.sendMessageToTelegram(chatId, BotMessages.ITEM_UNDONE.getMessage(), this);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	public void handleDeleteCommand(long chatId, String messageTextFromTelegram) {
		String delete = messageTextFromTelegram.substring(0,
				messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
		Integer id = Integer.valueOf(delete);

		try {
			// Verificar si la tarea existe
			ResponseEntity<ToDoItem> responseEntity = getToDoItemById(id);
			ToDoItem item = responseEntity.getBody();

			if (item == null || responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
				// Si la tarea no existe, enviar un mensaje de error
				BotHelper.sendMessageToTelegram(chatId, "❗ La tarea con ID " + id + " no existe.", this);
				return;
			}

			// Verificar si la tarea pertenece al usuario autenticado
			if (item.getIdUsuario() != authenticatedUserIds.get(chatId)) {
				// Si la tarea no pertenece al usuario, enviar un mensaje de error
				BotHelper.sendMessageToTelegram(chatId, "❗ No tienes permiso para eliminar la tarea con ID " + id + ".",
						this);
				return;
			}

			// Si la tarea existe y pertenece al usuario, proceder a eliminarla
			deleteToDoItem(id).getBody();
			BotHelper.sendMessageToTelegram(chatId, BotMessages.ITEM_DELETED.getMessage(), this);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			BotHelper.sendMessageToTelegram(chatId, "❗ Ocurrió un error al intentar eliminar la tarea.", this);
		}
	}

	public void handleHideCommand(long chatId) {
		BotHelper.sendMessageToTelegram(chatId, BotMessages.BYE.getMessage(), this);
	}

	public void handleToDoListCommand(long chatId, int userID) {
		List<ToDoItem> allItems = getAllToDoItemsByidUsuario(userID);
		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		List<KeyboardRow> keyboard = new ArrayList<>();

		KeyboardRow mainScreenRowTop = new KeyboardRow();
		mainScreenRowTop.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
		keyboard.add(mainScreenRowTop);

		KeyboardRow firstRow = new KeyboardRow();
		firstRow.add(BotLabels.ADD_NEW_ITEM.getLabel());
		keyboard.add(firstRow);

		KeyboardRow myTodoListTitleRow = new KeyboardRow();
		myTodoListTitleRow.add(BotLabels.MY_TODO_LIST.getLabel());
		keyboard.add(myTodoListTitleRow);

		List<ToDoItem> activeItems = allItems.stream().filter(item -> !item.isDone()).collect(Collectors.toList());

		for (ToDoItem item : activeItems) {
			KeyboardRow currentRow = new KeyboardRow();
			currentRow.add(item.getDescription());
			currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.DONE.getLabel());
			keyboard.add(currentRow);
		}

		List<ToDoItem> doneItems = allItems.stream().filter(ToDoItem::isDone).collect(Collectors.toList());

		for (ToDoItem item : doneItems) {
			KeyboardRow currentRow = new KeyboardRow();
			currentRow.add(item.getDescription());
			currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.UNDO.getLabel());
			currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.DELETE.getLabel());
			keyboard.add(currentRow);
		}

		KeyboardRow mainScreenRowBottom = new KeyboardRow();
		mainScreenRowBottom.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
		keyboard.add(mainScreenRowBottom);

		keyboardMarkup.setKeyboard(keyboard);

		SendMessage messageToTelegram = new SendMessage();
		messageToTelegram.setChatId(chatId);
		messageToTelegram.setText(BotLabels.MY_TODO_LIST.getLabel());
		messageToTelegram.setReplyMarkup(keyboardMarkup);

		try {
			execute(messageToTelegram);
		} catch (TelegramApiException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	public void handleAddItemCommand(long chatId) {
		try {
			SendMessage messageToTelegram = new SendMessage();
			messageToTelegram.setChatId(chatId);
			messageToTelegram.setText(BotMessages.TYPE_NEW_TODO_ITEM.getMessage());
			ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove(true);
			messageToTelegram.setReplyMarkup(keyboardMarkup);

			execute(messageToTelegram);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	public void handleNewItem(long chatId, int userID, String messageTextFromTelegram) {
		try {
			if (!messageTextFromTelegram.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+")) {
				sendErrorMessage(chatId, "❗ La descripción de la tarea solo puede contener letras y espacios.");
				return;
			}

			// Validar que el usuario no tenga más de 10 tareas
			List<ToDoItem> userItems = getAllToDoItemsByidUsuario(userID);
			if (userItems.size() >= 10) {
				sendErrorMessage(chatId, "❗ Solo puedes tener un máximo de 10 tareas.");
				return;
			}
			ToDoItem newItem = new ToDoItem();
			newItem.setIdUsuario(userID);
			newItem.setDescription(messageTextFromTelegram);
			newItem.setCreation_ts(OffsetDateTime.now());
			newItem.setDone(false);
			addToDoItem(newItem);

			SendMessage messageToTelegram = new SendMessage();
			messageToTelegram.setChatId(chatId);
			messageToTelegram.setText(BotMessages.NEW_ITEM_ADDED.getMessage());

			execute(messageToTelegram);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	private void sendSuccessMessage(long chatId, String messageText) {
		SendMessage messageToTelegram = new SendMessage();
		messageToTelegram.setChatId(chatId);
		messageToTelegram.setText(messageText);
		try {
			execute(messageToTelegram);
		} catch (TelegramApiException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	private void sendErrorMessage(long chatId, String messageText) {
		SendMessage messageToTelegram = new SendMessage();
		messageToTelegram.setChatId(chatId);
		messageToTelegram.setText("Error: " + messageText);
		try {
			execute(messageToTelegram);
		} catch (TelegramApiException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public String getBotUsername() {
		return botName;
	}

	// GET /todolist
	public List<ToDoItem> getAllToDoItems() {
		return toDoItemService.findAll();
	}

	// Nuevo método para obtener tareas por ID de usuario
	public List<ToDoItem> getAllToDoItemsByidUsuario(int userID) {
		return toDoItemService.findAllByidUsuario(userID);
	}

	// GET BY ID /todolist/{id}
	public ResponseEntity<ToDoItem> getToDoItemById(@PathVariable int id) {
		try {
			ResponseEntity<ToDoItem> responseEntity = toDoItemService.getItemById(id);
			return new ResponseEntity<ToDoItem>(responseEntity.getBody(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// PUT /todolist
	public ResponseEntity addToDoItem(@RequestBody ToDoItem todoItem) throws Exception {
		ToDoItem td = toDoItemService.addToDoItem(todoItem);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("location", "" + td.getID());
		responseHeaders.set("Access-Control-Expose-Headers", "location");
		// URI location = URI.create(""+td.getID())

		return ResponseEntity.ok().headers(responseHeaders).build();
	}

	// UPDATE /todolist/{id}
	public ResponseEntity updateToDoItem(@RequestBody ToDoItem toDoItem, @PathVariable int id) {
		try {
			ToDoItem toDoItem1 = toDoItemService.updateToDoItem(id, toDoItem);
			System.out.println(toDoItem1.toString());
			return new ResponseEntity<>(toDoItem1, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	// DELETE todolist/{id}
	public ResponseEntity<Boolean> deleteToDoItem(@PathVariable("id") int id) {
		Boolean flag = false;
		try {
			flag = toDoItemService.deleteToDoItem(id);
			return new ResponseEntity<>(flag, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(flag, HttpStatus.NOT_FOUND);
		}
	}

}