package my.project.impl;

import lombok.extern.slf4j.Slf4j;
import my.project.service.ConsumerService;
import my.project.service.ProducerService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;


import static my.project.model.RabbitQueue.*;

@Slf4j
@Service
public class ConsumerServiceImpl implements ConsumerService {

	private final ProducerService producerService;

	public ConsumerServiceImpl(ProducerService producerService) {
		this.producerService = producerService;
	}

	@Override
	@RabbitListener(queues = TEXT_MESSAGE_UPDATE)
	public void consumeTextMessageUpdates(Update update) {
		log.debug("NODE: Text message is received");

		var message = update.getMessage();
		var sendMessage	= new SendMessage();
		sendMessage.setChatId(message.getChatId().toString());
		sendMessage.setText("Hello from NODE");
		producerService.produceAnswer(sendMessage);
	}

	@Override
	@RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
	public void consumePhotoMessageUpdates(Update update) {
		log.debug("NODE: Photo message is received");
	}

	@Override
	@RabbitListener(queues = DOC_MESSAGE_UPDATE)
	public void consumeDocMessageUpdates(Update update) {
		log.debug("NODE: Doc message is received");
	}
}
