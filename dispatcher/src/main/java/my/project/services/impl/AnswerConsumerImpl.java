package my.project.services.impl;

import my.project.controllers.UpdateController;
import my.project.services.AnswerConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static my.project.model.RabbitQueue.ANSWER_MESSAGE;

public class AnswerConsumerImpl implements AnswerConsumer {

	private final UpdateController updateController;

	public AnswerConsumerImpl(UpdateController updateController) {
		this.updateController = updateController;
	}

	@Override
	@RabbitListener(queues = ANSWER_MESSAGE)
	public void consume(SendMessage message) {
		updateController.setView(message);
	}
}
