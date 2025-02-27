package my.project.service;

import my.project.entity.enums.AppDocument;
import org.telegram.telegrambots.meta.api.objects.Message;


public interface FileService {

	AppDocument processDoc(Message externalMessage);
}
