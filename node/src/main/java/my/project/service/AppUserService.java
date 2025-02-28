package my.project.service;

import my.project.entity.AppUser;

public interface AppUserService {

	String registerUser(AppUser user);

	String setEmail(AppUser user, String email);
}
