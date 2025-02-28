package my.project.service;

import my.project.dto.MailParams;

public interface MailSenderService {
	void send(MailParams mailParams);
}