package com.springboot.MyTodoList.controller;
import com.springboot.MyTodoList.controller.UserAuthentication;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
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

/* public class ToDoItemBotController extends TelegramLongPollingBot {

	private static final Logger logger = LoggerFactory.getLogger(ToDoItemBotController.class);
	private ToDoItemService toDoItemService;
	private String botName;

	public ToDoItemBotController(String botToken, String botName, ToDoItemService toDoItemService) {
		super(botToken);
		logger.info("Bot Token: " + botToken);
		logger.info("Bot name: " + botName);
		this.toDoItemService = toDoItemService;
		this.botName = botName;
	}

	@Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {

            String messageTextFromTelegram = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

        	if (messageTextFromTelegram.equals(BotCommands.START_COMMAND.getCommand())) {

	                SendMessage messageToTelegram = new SendMessage();
	                messageToTelegram.setChatId(chatId);
	                messageToTelegram.setText(BotMessages.HELLO_MYTODO_BOT.getMessage());
	
	                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
	                List<KeyboardRow> keyboard = new ArrayList<>();
	
	                // Manager Option
	                KeyboardRow managerRow = new KeyboardRow();
	                managerRow.add(BotLabels.MANAGER.getLabel());
	                keyboard.add(managerRow);
	
	                // Dev Option
	                KeyboardRow devRow = new KeyboardRow();
	                devRow.add(BotLabels.DEV.getLabel());
	                keyboard.add(devRow);
	
	                keyboardMarkup.setKeyboard(keyboard);
	                messageToTelegram.setReplyMarkup(keyboardMarkup);
	
	                try {
	                    execute(messageToTelegram);
	                } catch (TelegramApiException e) {
	                    logger.error(e.getLocalizedMessage(), e);
	                }

            	} else if (messageTextFromTelegram.equals(BotLabels.MANAGER.getLabel())) {
	                // Options for Manager
	                SendMessage messageToTelegram = new SendMessage();
	                messageToTelegram.setChatId(chatId);
	                messageToTelegram.setText("Manager Options:");
	
	                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
	                List<KeyboardRow> keyboard = new ArrayList<>();
	
	                KeyboardRow row1 = new KeyboardRow();
	                row1.add(BotLabels.LIST_ALL_ITEMS.getLabel());
	                row1.add(BotLabels.ADD_NEW_ITEM.getLabel());
	                keyboard.add(row1);
	
	                KeyboardRow row2 = new KeyboardRow();
	                row2.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
	                row2.add(BotLabels.HIDE_MAIN_SCREEN.getLabel());
	                keyboard.add(row2);
	
	                keyboardMarkup.setKeyboard(keyboard);
	                messageToTelegram.setReplyMarkup(keyboardMarkup);
	
	                try {
	                    execute(messageToTelegram);
	                } catch (TelegramApiException e) {
	                    logger.error(e.getLocalizedMessage(), e);
	                }

		} else if (messageTextFromTelegram.equals(BotLabels.DEV.getLabel())) {
	                // Options for Dev
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
	
	                keyboardMarkup.setKeyboard(keyboard);
	                messageToTelegram.setReplyMarkup(keyboardMarkup);
	
	                try {
	                    execute(messageToTelegram);
	                } catch (TelegramApiException e) {
	                    logger.error(e.getLocalizedMessage(), e);
	                }
			
		} else if (messageTextFromTelegram.indexOf(BotLabels.DONE.getLabel()) != -1) {
	
			String done = messageTextFromTelegram.substring(0,
					messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
			Integer id = Integer.valueOf(done);
	
			try {
	
				ToDoItem item = getToDoItemById(id).getBody();
				item.setDone(true);
				updateToDoItem(item, id);
				BotHelper.sendMessageToTelegram(chatId, BotMessages.ITEM_DONE.getMessage(), this);
	
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}

		} else if (messageTextFromTelegram.indexOf(BotLabels.UNDO.getLabel()) != -1) {

			String undo = messageTextFromTelegram.substring(0,
					messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
			Integer id = Integer.valueOf(undo);
	
			try {

				ToDoItem item = getToDoItemById(id).getBody();
				item.setDone(false);
				updateToDoItem(item, id);
				BotHelper.sendMessageToTelegram(chatId, BotMessages.ITEM_UNDONE.getMessage(), this);
	
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}

		} else if (messageTextFromTelegram.indexOf(BotLabels.DELETE.getLabel()) != -1) {
	
			String delete = messageTextFromTelegram.substring(0,
					messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
			Integer id = Integer.valueOf(delete);
	
			try {
	
				deleteToDoItem(id).getBody();
				BotHelper.sendMessageToTelegram(chatId, BotMessages.ITEM_DELETED.getMessage(), this);
	
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}

		} else if (messageTextFromTelegram.equals(BotCommands.HIDE_COMMAND.getCommand())
				|| messageTextFromTelegram.equals(BotLabels.HIDE_MAIN_SCREEN.getLabel())) {
	
			BotHelper.sendMessageToTelegram(chatId, BotMessages.BYE.getMessage(), this);
	
		} else if (messageTextFromTelegram.equals(BotCommands.TODO_LIST.getCommand())
				|| messageTextFromTelegram.equals(BotLabels.LIST_ALL_ITEMS.getLabel())
				|| messageTextFromTelegram.equals(BotLabels.MY_TODO_LIST.getLabel())) {
	
			List<ToDoItem> allItems = getAllToDoItems();
			ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
			List<KeyboardRow> keyboard = new ArrayList<>();
	
			// command back to main screen
			KeyboardRow mainScreenRowTop = new KeyboardRow();
			mainScreenRowTop.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
			keyboard.add(mainScreenRowTop);
	
			KeyboardRow firstRow = new KeyboardRow();
			firstRow.add(BotLabels.ADD_NEW_ITEM.getLabel());
			keyboard.add(firstRow);
	
			KeyboardRow myTodoListTitleRow = new KeyboardRow();
			myTodoListTitleRow.add(BotLabels.MY_TODO_LIST.getLabel());
			keyboard.add(myTodoListTitleRow);
	
			List<ToDoItem> activeItems = allItems.stream().filter(item -> item.isDone() == false)
					.collect(Collectors.toList());
	
			for (ToDoItem item : activeItems) {
	
				KeyboardRow currentRow = new KeyboardRow();
				currentRow.add(item.getDescription());
				currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.DONE.getLabel());
				keyboard.add(currentRow);
			}
	
			List<ToDoItem> doneItems = allItems.stream().filter(item -> item.isDone() == true)
					.collect(Collectors.toList());
	
			for (ToDoItem item : doneItems) {
				KeyboardRow currentRow = new KeyboardRow();
				currentRow.add(item.getDescription());
				currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.UNDO.getLabel());
				currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.DELETE.getLabel());
				keyboard.add(currentRow);
			}

			// command back to main screen
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

		} else if (messageTextFromTelegram.equals(BotCommands.ADD_ITEM.getCommand())
				|| messageTextFromTelegram.equals(BotLabels.ADD_NEW_ITEM.getLabel())) {
			try {
				SendMessage messageToTelegram = new SendMessage();
				messageToTelegram.setChatId(chatId);
				messageToTelegram.setText(BotMessages.TYPE_NEW_TODO_ITEM.getMessage());
				// hide keyboard
				ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove(true);
				messageToTelegram.setReplyMarkup(keyboardMarkup);
	
				// send message
				execute(messageToTelegram);
	
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
	
		} else {
			try {
				ToDoItem newItem = new ToDoItem();
				newItem.setDescription(messageTextFromTelegram);
				newItem.setCreation_ts(OffsetDateTime.now());
				newItem.setDone(false);
				ResponseEntity entity = addToDoItem(newItem);

				SendMessage messageToTelegram = new SendMessage();
				messageToTelegram.setChatId(chatId);
				messageToTelegram.setText(BotMessages.NEW_ITEM_ADDED.getMessage());
			
				execute(messageToTelegram);
			} catch (Exception e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}}
		
	} */

	public class ToDoItemBotController extends TelegramLongPollingBot {

		private static final Logger logger = LoggerFactory.getLogger(ToDoItemBotController.class);
		private ToDoItemService toDoItemService;
		private String botName;
		private UserAuthentication userAuthentication;
	
		public ToDoItemBotController(String botToken, String botName, ToDoItemService toDoItemService, UserAuthentication userAuthentication) {
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
	
				if (messageTextFromTelegram.equals(BotCommands.START_COMMAND.getCommand())) {
					// Solo permitir el comando /login al iniciar el bot
					SendMessage messageToTelegram = new SendMessage();
					messageToTelegram.setChatId(chatId);
					messageToTelegram.setText("Por favor, introduce /login para autenticarte.");
					try {
						execute(messageToTelegram);
					} catch (TelegramApiException e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				} else if (messageTextFromTelegram.startsWith(BotCommands.LOG_IN.getCommand())) {
					// Lógica de autenticación
				String[] parts = messageTextFromTelegram.split(" ");
                if (parts.length != 3) {
                sendErrorMessage(chatId, "Por favor, introduce tu nombre de usuario y contraseña en el siguiente formato: /login usuario_contraseña");
                return;
                }
                String username = parts[1];
                String password = parts[2];
                String[] authenticationResult = userAuthentication.isAuthenticated(username, password);
                if (authenticationResult[0].equals("true")) {
                    String name = authenticationResult[1];
                    String role = authenticationResult[2];
                    sendSuccessMessage(chatId, "¡Hola " + name + "! Eres un " + role);
                } else {
                    sendErrorMessage(chatId, authenticationResult[1]);
                }
				} else {
					// Usuario no ha iniciado sesión
					sendErrorMessage(chatId, "Por favor, inicia sesión primero con /login");
				}
				
			}
		}
	
		private void sendSuccessMessage(long chatId, String role) {
			SendMessage messageToTelegram = new SendMessage();
			messageToTelegram.setChatId(chatId);
			messageToTelegram.setText("¡Autenticación exitosa! Ahora puedes acceder a las funcionalidades del bot como " + role);
			try {
				execute(messageToTelegram);
			} catch (TelegramApiException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
	
		private void sendErrorMessage(long chatId, String errorMessage) {
			SendMessage messageToTelegram = new SendMessage();
			messageToTelegram.setChatId(chatId);
			messageToTelegram.setText(errorMessage);
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