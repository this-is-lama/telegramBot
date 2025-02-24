package my.project.impl;

import my.project.dao.AppUserDAO;
import my.project.dao.RawDataDAO;
import my.project.entity.AppUser;
import my.project.entity.RawData;
import my.project.entity.enums.UserState;
import my.project.service.MainService;
import my.project.service.ProducerService;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

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
		var textMessage = update.getMessage();
		var telegramUser = textMessage.getFrom();
		var appUser = findOrSaveAppUser(telegramUser);

		var message = update.getMessage();
		var sendMessage	= new SendMessage();
		sendMessage.setChatId(message.getChatId().toString());
		sendMessage.setText("Hello from NODE");
		producerService.produceAnswer(sendMessage);
	}

	private void saveRawData(Update update) {
		RawData rawData = RawData.builder()
				.event(update)
				.build();

		rawDataDAO.save(rawData);
	}

	private AppUser findOrSaveAppUser(User telegramUser) {
		AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
		if (persistentAppUser == null) {
			AppUser transientAppUser = AppUser.builder()
					.telegramUserId(telegramUser.getId())
					.username(telegramUser.getUserName())
					.firstName(telegramUser.getFirstName())
					.lastName(telegramUser.getLastName())
					//TODO изменить значение
					.isActive(true)
					.state(UserState.BASIC_STATE)
					.build();
			return appUserDAO.save(transientAppUser);
		}
		return persistentAppUser;
	}
}
