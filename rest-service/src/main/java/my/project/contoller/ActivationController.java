package my.project.contoller;

import my.project.service.UserActivationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user")
@RestController
public class ActivationController {

	private final UserActivationService userActivationService;

	public ActivationController(UserActivationService userActivationService) {
		this.userActivationService = userActivationService;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/activation")
	public ResponseEntity<?> activation(@RequestParam("id") String id) {
		var res = userActivationService.activation(id);
		if (res) {
			return ResponseEntity.ok().body("Регистрация прошла успешно");
		}
		return ResponseEntity.internalServerError().build();
	}
}
