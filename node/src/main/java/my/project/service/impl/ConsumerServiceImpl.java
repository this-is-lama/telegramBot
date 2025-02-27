package my.project.service.impl;

import lombok.extern.slf4j.Slf4j;
import my.project.service.ConsumerService;
import my.project.service.MainService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;


import static my.project.model.RabbitQueue.*;

@Slf4j
@Service
public class ConsumerServiceImpl implements ConsumerService {

	private final MainService mainService;

	public ConsumerServiceImpl(MainService mainService) {
		this.mainService = mainService;
	}

	@Override
	@RabbitListener(queues = TEXT_MESSAGE_UPDATE)
	public void consumeTextMessageUpdates(Update update) {
		log.debug("NODE: Text message is received");
		mainService.processTextMessage(update);
	}

	@Override
	@RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
	public void consumePhotoMessageUpdates(Update update) {
		log.debug("NODE: Photo message is received");
		mainService.processPhotoMessage(update);
	}

	@Override
	@RabbitListener(queues = DOC_MESSAGE_UPDATE)
	public void consumeDocMessageUpdates(Update update) {
		log.debug("NODE: Doc message is received");
		mainService.processDocMessage(update);
	}
}
