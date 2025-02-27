package my.project.impl;

import lombok.extern.slf4j.Slf4j;
import my.project.dao.AppUserDAO;
import my.project.dao.RawDataDAO;
import my.project.entity.AppUser;
import my.project.entity.RawData;
import my.project.service.MainService;
import my.project.service.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static my.project.entity.enums.UserState.*;
import static my.project.service.enums.ServiceCommands.*;

@Slf4j
@Service
public class MainServiceImpl implements MainService {

	private final RawDataDAO rawDataDAO;

	private final ProducerService producerService;

	private final AppUserDAO appUserDAO;

	public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO) {
		this.rawDataDAO = rawDataDAO;
		this.producerService = producerService;
		this.appUserDAO = appUserDAO;
	}

	@Override
	public void processTextMessage(Update update) {
		saveRawData(update);
		var appUser = findOrSaveAppUser(update);
		var userState = appUser.getState();
		var text = update.getMessage().getText();
		var output = "";

		if (CANCEL.equals(text)) {
			output  = cancelProcess(appUser);
		} else if (BASIC_STATE.equals(userState)) {
			output = processServiceCommand(appUser, text);
		} else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
			// add action for email
		} else {
			log.error("Unknown user state: {}", userState);
			output = "Неизвестная ошибка, введите /cancel и попробуйте снова!";
		}

		var chatId = update.getMessage().getChatId();
		sendAnswer(output, chatId);
	}

	@Override
	public void processPhotoMessage(Update update) {
		saveRawData(update);
		var appUser = findOrSaveAppUser(update);
		var chatId = update.getMessage().getChatId();
		if (isNotAllowToSendContent(chatId, appUser)) {
			return;
		}

		//добавить сохранение фото
		var answer = "Фото успешно загружено! Ссылка для скачивания: ссылка в сибирь";
		sendAnswer(answer, chatId);
	}

	private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
		var userState = appUser.getState();
		if (!appUser.getIsActive()) {
			var error = "Зарегистрируйтесь или активируйте свою учетную запись для загрузки контента!";
			sendAnswer(error, chatId);
			return true;
		} else if (!BASIC_STATE.equals(userState)) {
			var error = "Отмените текущую команду с помощью /cancel для отправки файлов!";
			sendAnswer(error, chatId);
			return true;
		}
		return false;
	}

	@Override
	public void processDocMessage(Update update) {
		saveRawData(update);
		var appUser = findOrSaveAppUser(update);
		var chatId = update.getMessage().getChatId();
		if (isNotAllowToSendContent(chatId, appUser)) {
			return;
		}

		//добавить сохранение фото
		var answer = "Документ успешно загружен! Ссылка для скачивания: ссылка в сибирь";
		sendAnswer(answer, chatId);
	}

	private void sendAnswer(String output, Long chatId) {
		SendMessage sendMessage	= new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(output);
		producerService.producerAnswer(sendMessage);
	}

	private String processServiceCommand(AppUser appUser, String cmd) {
		if (REGISTRATION.equals(cmd)) {
			//добавить регистрацию
			return "Временно недоступно!";
		} else if (HELP.equals(cmd)) {
			return help();
		} else if (START.equals(cmd)) {
			return "Приветствую! Чтобы посмотреть список доступных команд введите /help!";
		} else {
			return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help!";
		}
	}

	private String help() {
		return "Список доступных команд: \n" +
				"/cancel - отмена выполнения текущей команды\n" +
				"/registration - регистрация пользователя.\n";
	}

	private String 	cancelProcess(AppUser appUser) {
		appUser.setState(BASIC_STATE);
		appUserDAO.save(appUser);
		return "Команда отменена!";
	}

	private void saveRawData(Update update) {
		RawData rawData = RawData.builder()
				.event(update)
				.build();

		rawDataDAO.save(rawData);
	}

	private AppUser findOrSaveAppUser(Update update) {
		var telegramUser = update.getMessage().getFrom();
		AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
		if (persistentAppUser == null) {
			AppUser transientAppUser = AppUser.builder()
					.telegramUserId(telegramUser.getId())
					.username(telegramUser.getUserName())
					.firstName(telegramUser.getFirstName())
					.lastName(telegramUser.getLastName())
					//TODO изменить значение
					.isActive(true)
					.state(BASIC_STATE)
					.build();
			return appUserDAO.save(transientAppUser);
		}
		return persistentAppUser;
	}
}
