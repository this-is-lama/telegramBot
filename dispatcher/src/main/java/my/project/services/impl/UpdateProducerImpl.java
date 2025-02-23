package my.project.services.impl;

import lombok.extern.slf4j.Slf4j;
import my.project.services.UpdateProducer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Service
public class UpdateProducerImpl implements UpdateProducer {

	private final RabbitTemplate rabbitTemplate;

	public UpdateProducerImpl(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void produce(String rabbitQueue, Update update) {
		log.debug(update.getMessage().getText());
		rabbitTemplate.convertAndSend(rabbitQueue, update);
	}
}
