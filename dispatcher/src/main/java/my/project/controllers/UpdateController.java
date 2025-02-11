package my.project.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import my.project.services.UpdateProducer;
import my.project.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
public class UpdateController {

	public static final String DOC_MESSAGE_UPDATE = "doc_message_update";
	public static final String PHOTO_MESSAGE_UPDATE = "photo_message_update";
	public static final String TEXT_MESSAGE_UPDATE = "text_message_update";

	private TelegramBot telegramBot;

	private MessageUtils messageUtils;

	private UpdateProducer updateProducer;

	public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
		this.messageUtils = messageUtils;
		this.updateProducer = updateProducer;
	}

	public void registerBot(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
	}

	public void processUpdate(Update update) {
		if (update == null) {
			log.error("Received null update");
			return;
		}

		if (update.getMessage() != null) {
			distributeMessageByType(update);
		} else {
			log.error("Unsupported message type is received : {}", update);
		}
	}

	private void distributeMessageByType(Update update) {
		var message = update.getMessage();
		if (message.getText() != null) {
			processTextMessage(update);
		} else if (message.getDocument() != null) {
			processDocument(update);
		} else if (message.getPhoto() != null) {
			processPhoto(update);
		} else {
			setUnsupportedMessageTypeView(update);
		}
	}

	private void setUnsupportedMessageTypeView(Update update) {
		var sendMessage = messageUtils.generateSendMessageWithText(
				update, "Неподдерживаемый тип сообщения");
		setView(sendMessage);
	}

	private void setFileIsReceivedView(Update update) {
		var sendMessage = messageUtils.generateSendMessageWithText(
				update, "Файл получен! Обрабатывается...");
		setView(sendMessage);
	}

	private void setView(SendMessage sendMessage) {
		telegramBot.sendAnswerMessage(sendMessage);
	}

	private void processTextMessage(Update update) {
		updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
	}

	private void processPhoto(Update update) {
		updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
		setFileIsReceivedView(update);
	}

	private void processDocument(Update update) {
		updateProducer.produce(DOC_MESSAGE_UPDATE, update);
		setFileIsReceivedView(update);
	}
}
