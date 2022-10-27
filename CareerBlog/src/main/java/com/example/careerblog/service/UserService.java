package com.example.careerblog.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.careerblog.Models.Role;
import com.example.careerblog.Models.User;
import com.example.careerblog.Repository.UserRepository;

@Service
public class UserService implements UserDetailsService{
	
	@Autowired
	private /*final*/ UserRepository userRepository;
	
	/*public UserService(UserRepository userRepository) {	//commented code used for possible @Autowired removal
	this.userRepository = userRepository;
	}*/ 
	
	@Autowired
	private MailSender mailSender;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username);
	}
	
	private void sendMessage(User user) {					// activation code sender
		if (StringUtils.hasText(user.getEmail())) {
			String message = String.format(
					"Hello, %s! \n" + "welcome to the app. To confirm your email, go to: http://localhost:8080/careerblog/activate/%s",
					user.getUsername(),
					user.getActivationCode()
					);
			mailSender.sendAuthenticationCode(user.getEmail(), "Activation code", message);
		}
	}
	
	private void changeEmail(User user, String email) {		// changing email in account
		
		String userEmail = user.getEmail();	
		
		if (!userEmail.equals(email)){
			boolean isEmailchanged =  (StringUtils.hasText(email) && !email.equals(userEmail));
			if (isEmailchanged) {
				user.setEmail(email);
				if (StringUtils.hasText(user.getEmail())) {
					user.setActivationCode(UUID.randomUUID().toString());
					user.setActive(false);
				}
				userRepository.save(user);
				sendMessage(user);
			}
		}
	}
	
	public boolean addUser(User user) {
		
		User userFromDB = userRepository.findByUsername(user.getUsername());
		
		if (userFromDB != null) { // check if user already exists
			return false;
		}
		user.setActive(false);
		user.setRoles(Collections.singleton(Role.USER));
		user.setActivationCode(UUID.randomUUID().toString());			// generating activation code for email verification
		user.setPassword(passwordEncoder.encode(user.getPassword()));	// encoding user password before save
		// System.out.println(user); // show new user before saving
		userRepository.save(user);
		sendMessage(user);
		return true;
	}
	
	public boolean activateUser(String code) {
		
		User user = userRepository.findByActivationCode(code);
		
		if (user == null) {
			return false;
		}
		user.setActivationCode(null);
		user.setActive(true);
		userRepository.save(user);
		return true;
	}
	
	public List<User> findAll() {
		return userRepository.findAll();
	}
	
	public void saveUser(
			User user, 
			String username, 
			String password, 
			String email, 
			Map<String, String> form
			) {
		
		if (StringUtils.hasText(username)) {
			user.setUsername(username);
		}
		
		if (StringUtils.hasText(password)) {
			user.setPassword(passwordEncoder.encode(password));
		}
		
		changeEmail(user, email);
		//user.setActive(true);
		Set<String> roles = Arrays.stream(Role.values()) //create possible user role list
				.map(Role::name)
				.collect(Collectors.toSet()); 
		user.getRoles().clear();	//clear existing user roles
		
		for (String key : form.keySet()) {	//if role is checked add role to user

			if (roles.contains(key)){
				user.getRoles().add(Role.valueOf(key));
			}
		}

		userRepository.save(user);
	}

	public void removeUser(Long id) {
		User user = userRepository.findById(id).orElseThrow(null);
		userRepository.delete(user);		
	}

	public void updateAccount(
			User user,
			String username,
			String password,
			String email) {

		if (StringUtils.hasText(username)) {
			user.setUsername(username);
		}
		
		if (StringUtils.hasText(password)) {
			user.setPassword(passwordEncoder.encode(password));
		}
		
		userRepository.save(user);
		changeEmail(user, email);

	}
	
}
