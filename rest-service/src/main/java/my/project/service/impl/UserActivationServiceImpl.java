package my.project.service.impl;

import my.project.dao.AppUserDAO;
import my.project.service.UserActivationService;
import my.project.utils.CryptoTool;
import org.springframework.stereotype.Service;

@Service
public class UserActivationServiceImpl implements UserActivationService {

	private final AppUserDAO appUserDAO;
	private final CryptoTool cryptoTool;

	public UserActivationServiceImpl(AppUserDAO appUserDAO, CryptoTool cryptoTool) {
		this.appUserDAO = appUserDAO;
		this.cryptoTool = cryptoTool;
	}

	@Override
	public boolean activation(String cryptoUserId) {
		var userId = cryptoTool.idOf(cryptoUserId);
		var optional = appUserDAO.findById(userId);
		if (optional.isPresent()) {
			var user = optional.get();
			user.setIsActive(true);
			appUserDAO.save(user);
			return true;
		}
		return false;
	}
}
