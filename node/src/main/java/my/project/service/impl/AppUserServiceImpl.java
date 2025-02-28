package my.project.service.impl;

import lombok.extern.slf4j.Slf4j;
import my.project.dao.AppUserDAO;
import my.project.dto.MailParams;
import my.project.entity.AppUser;
import my.project.entity.enums.UserState;
import my.project.service.AppUserService;
import my.project.utils.CryptoTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Slf4j
@Service
public class AppUserServiceImpl implements AppUserService {

	private final AppUserDAO appUserDAO;
	private final CryptoTool cryptoTool;

	@Value("${service.mail.uri}")
	private String mailServiceUri;

	public AppUserServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool) {
		this.appUserDAO = appUserDAO;
		this.cryptoTool = cryptoTool;
	}


	@Override
	public String registerUser(AppUser appUser) {
		if (appUser.getIsActive()) {
			return "Вы уже зарегистрированы!";
		} else if (appUser.getEmail() != null) {
			return "Вам на почту было отправлено письмо!";
		}
		appUser.setState(UserState.WAIT_FOR_EMAIL_STATE);
		appUserDAO.save(appUser);
		return "Введите, пожалуйста, ваш email:";
	}

	@Override
	public String setEmail(AppUser appUser, String email) {
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException e) {
			return "Пожалуйста, введите корректный email.";
		}
		var optional = appUserDAO.findByEmail(appUser.getEmail());
		if (optional.isEmpty()) {
			appUser.setEmail(email);
			appUser.setState(UserState.BASIC_STATE);
			appUser = appUserDAO.save(appUser);

			var cryptoUserId = cryptoTool.hashOf(appUser.getId());
			var response = sendRequestToMailService(cryptoUserId, email);
			if (response.getStatusCode() != HttpStatus.OK) {
				var msg = String.format("Отправка эл. письма на почту %s не удалась", email);
				log.error(msg);
				appUser.setEmail(null);
				appUserDAO.save(appUser);
				return msg;
			}
			return "Вам на почту было отправлено письмо!";
		} else {
			return "Этот email уже используется введите корректный email";
		}

	}

	private ResponseEntity<String> sendRequestToMailService(String cryptoUserId, String email) {
		var restTemplate = new RestTemplate();
		var headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		var mailParams = MailParams.builder()
				.id(cryptoUserId)
				.emailTo(email)
				.build();
		var request = new HttpEntity<>(mailParams, headers);
		return restTemplate.exchange(mailServiceUri,
				HttpMethod.POST,
				request,
				String.class);
	}
}
