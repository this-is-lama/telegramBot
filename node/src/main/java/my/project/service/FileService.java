package my.project.service;

import my.project.entity.AppDocument;
import my.project.entity.AppPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;


public interface FileService {

	AppDocument processDoc(Message telegaramMessage);

	AppPhoto processPhoto(Message telegramMessage);
}
