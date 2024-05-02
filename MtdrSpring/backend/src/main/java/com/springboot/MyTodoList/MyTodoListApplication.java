package com.springboot.MyTodoList;
import com.springboot.MyTodoList.model.Roles;
import org.slf4j.Logger;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.springboot.MyTodoList.controller.ToDoItemBotController;
import com.springboot.MyTodoList.controller.RolesController;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.service.RolesService;
import com.springboot.MyTodoList.util.BotMessages;

@SpringBootApplication
public class MyTodoListApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(MyTodoListApplication.class);

	@Autowired
	private ToDoItemService toDoItemService;

	@Autowired
    private RolesService rolesService;

	@Value("${telegram.bot.token}")
	private String telegramBotToken;

	@Value("${telegram.bot.name}")
	private String botName;

	public static void main(String[] args) {
		SpringApplication.run(MyTodoListApplication.class, args);
	}

	private void printRolesToConsole() {
        List<Roles> roles = rolesService.findAll();
        logger.info("Roles disponibles:");
        for (Roles role : roles) {
            logger.info("ID: " + role.getId() + ", Nombre: " + role.getNombre());
        }
    }

	@Override
	public void run(String... args) throws Exception {
		try {
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(new ToDoItemBotController(telegramBotToken, botName, toDoItemService));
			logger.info(BotMessages.BOT_REGISTERED_STARTED.getMessage());

			printRolesToConsole();
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
