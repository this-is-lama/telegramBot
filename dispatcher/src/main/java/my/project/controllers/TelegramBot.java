package my.project.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

	@Value("${bot.name}")
	private String botName;

	@Value("${bot.token}")
	private String botToken;


	@Override
	public String getBotUsername() {
		return botName;
	}

	@Override
	public String getBotToken() {
		return botToken;
	}

	@Override
	public void onUpdateReceived(Update update) {
		var message = update.getMessage();
		log.debug(message.getText());

		var response = new SendMessage();
		response.setChatId(message.getChatId().toString());
		response.setText("Hello World");
		sendAnswerMessage(response);
	}

	public void sendAnswerMessage(SendMessage message) {
		if (message != null) {
			try {
				execute(message);
			} catch (TelegramApiException e) {
				log.debug("Пустой ответ");
			}
		}
	}
}
