package my.project.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService {

	void consumeTextMessageUpdates(Update update);

	void consumePhotoMessageUpdates(Update update);

	void consumeDocMessageUpdates(Update update);

	interface FileService {
	}
}
