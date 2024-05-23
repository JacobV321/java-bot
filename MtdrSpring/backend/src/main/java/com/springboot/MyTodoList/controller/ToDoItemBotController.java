package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.controller.UserAuthentication;
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
import com.springboot.MyTodoList.model.Roles;
import com.springboot.MyTodoList.model.Usuario;

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

			if (messageTextFromTelegram.equals(BotCommands.START_COMMAND.getCommand())
					&& authenticatedUsers.getOrDefault(chatId, false)) {
				// Solo permitir el comando /login al iniciar el bot
				SendMessage messageToTelegram = new SendMessage();
				messageToTelegram.setChatId(chatId);
				messageToTelegram.setText("Por favor, introduce /login para autenticarte.");
				try {
					execute(messageToTelegram);
				} catch (TelegramApiException e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			} else if (messageTextFromTelegram.startsWith(BotCommands.LOG_IN.getCommand())
					&& authenticatedUsers.getOrDefault(chatId, false) == false) {
				// Lógica de autenticación
				String[] parts = messageTextFromTelegram.split("\\s+", 3); // Divide en al menos 3 partes, ignorando los
																			// espacios extras
				if (parts.length != 3) {
					sendErrorMessage(chatId,
							"Por favor, introduce tu nombre de usuario y contraseña en el siguiente formato: /login usuario contraseña");
					return;
				}
				String username = parts[1];
				String password = parts[2];
				String[] authenticationResult = userAuthentication.isAuthenticated(username, password);
				if (authenticationResult[0].equals("true")) {
					authenticatedUsers.put(chatId, true);
					String name = authenticationResult[1];
					String role = authenticationResult[2];
					sendSuccessMessage(chatId, "¡Hola " + name + "! Eres un " + role);
					handleUserCommands(chatId, messageTextFromTelegram, role);
				} else {
					sendErrorMessage(chatId, authenticationResult[1]);
				}
			}
			if (authenticatedUsers.getOrDefault(chatId, false)) {
				// Usuario no autenticado, enviar mensaje de inicio de sesión
				sendErrorMessage(chatId, "Por favor, inicia sesión primero con /login");
				return;
			}

		}
	}

	private void handleUserCommands(long chatId, String messageTextFromTelegram, String role) {
		if (role.equals("dev")) {
			if (messageTextFromTelegram.equals(BotCommands.START_COMMAND.getCommand())) {
				handleStartCommand(chatId);
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
				handleToDoListCommand(chatId);
			} else if (messageTextFromTelegram.equals(BotCommands.ADD_ITEM.getCommand())
					|| messageTextFromTelegram.equals(BotLabels.ADD_NEW_ITEM.getLabel())) {
				handleAddItemCommand(chatId);
			} else if (messageTextFromTelegram.equals(BotLabels.LOG_OUT.getLabel())) {
				authenticatedUsers.remove(chatId);

				sendSuccessMessage(chatId,
						"¡Sesión cerrada exitosamente! Puedes usar /login para iniciar sesión nuevamente.");
				return;
			} else {
				handleNewItem(chatId, messageTextFromTelegram);
			}
		} else if (role.equals("admin")) {
			if (messageTextFromTelegram.equals(BotLabels.TEAM_LIST.getLabel())) {
				handleTeamListCommand(chatId);
			} else {
				sendErrorMessage(chatId, "Comando no reconocido para el rol admin.");
			}
		} else if (messageTextFromTelegram.equals(BotLabels.LOG_OUT.getLabel())) {
			authenticatedUsers.remove(chatId);

			sendSuccessMessage(chatId,
					"¡Sesión cerrada exitosamente! Puedes usar /login para iniciar sesión nuevamente.");
			return;
		} else {
			sendErrorMessage(chatId, "Rol no reconocido.");
		}
	}

	public void handleDevCommand(long chatId) {
		SendMessage messageToTelegram = new SendMessage();
		messageToTelegram.setChatId(chatId);
		messageToTelegram.setText("Dev Options:");

		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		List<KeyboardRow> keyboard = new ArrayList<>();

		KeyboardRow row1 = new KeyboardRow();
		row1.add(BotLabels.LIST_ALL_ITEMS.getLabel());
		keyboard.add(row1);

		KeyboardRow row2 = new KeyboardRow();
		row2.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
		row2.add(BotLabels.HIDE_MAIN_SCREEN.getLabel());
		keyboard.add(row2);

		KeyboardRow row3 = new KeyboardRow();
		row2.add(BotLabels.LOG_OUT.getLabel());
		keyboard.add(row3);

		keyboardMarkup.setKeyboard(keyboard);
		messageToTelegram.setReplyMarkup(keyboardMarkup);

		try {
			execute(messageToTelegram);
		} catch (TelegramApiException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	public void handleManagerCommand(long chatId) {
		SendMessage messageToTelegram = new SendMessage();
		messageToTelegram.setChatId(chatId);
		messageToTelegram.setText("Opciones de Manager:");

		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		List<KeyboardRow> keyboard = new ArrayList<>();

		// Añadir el botón para TEAM_LIST
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
		}
	}

	public void handleTeamListCommand(long chatId) {
		SendMessage messageToTelegram = new SendMessage();
		messageToTelegram.setChatId(chatId);
		messageToTelegram.setText("Lista de equipo");

		try {
			execute(messageToTelegram);
		} catch (TelegramApiException e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	public void handleStartCommand(long chatId) {
		SendMessage messageToTelegram = new SendMessage();
		messageToTelegram.setChatId(chatId);
		messageToTelegram.setText(BotMessages.HELLO_MYTODO_BOT.getMessage());

		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		List<KeyboardRow> keyboard = new ArrayList<>();

		keyboardMarkup.setKeyboard(keyboard);
		messageToTelegram.setReplyMarkup(keyboardMarkup);

		try {
			execute(messageToTelegram);
		} catch (TelegramApiException e) {
			logger.error(e.getLocalizedMessage(), e);
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
			deleteToDoItem(id).getBody();
			BotHelper.sendMessageToTelegram(chatId, BotMessages.ITEM_DELETED.getMessage(), this);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	public void handleHideCommand(long chatId) {
		BotHelper.sendMessageToTelegram(chatId, BotMessages.BYE.getMessage(), this);
	}

	public void handleToDoListCommand(long chatId) {
		List<ToDoItem> allItems = getAllToDoItems();
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

	public void handleNewItem(long chatId, String messageTextFromTelegram) {
		try {
			ToDoItem newItem = new ToDoItem();
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

	private void sendSuccessMessage(Long chatId, String text) {
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText(text);
		try {
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private void sendErrorMessage(Long chatId, String text) {
		SendMessage message = new SendMessage();
		message.setChatId(chatId);
		message.setText(text);
		try {
			execute(message);
		} catch (TelegramApiException e) {
			e.printStackTrace();
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