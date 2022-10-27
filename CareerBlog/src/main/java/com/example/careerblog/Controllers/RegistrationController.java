package com.example.careerblog.Controllers;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.careerblog.Models.Message;
import com.example.careerblog.Models.User;
import com.example.careerblog.service.UserService;

// controller for signup page processing
@Controller
public class RegistrationController {

	@Autowired
	private UserService userService;

	@GetMapping("/signup")
	public String registration(
			@ModelAttribute("user") User user,
			Model model) {
		model.addAttribute("title", "Страница регистрации нового пользователя");
		//model.addAttribute("message", "");
		return "signup";
	}
	
	/*
	 * @PostMapping("/signup_from_login") public String registrationFromLogin(Model
	 * model) { model.addAttribute("title",
	 * "Страница регистрации нового пользователя"); model.addAttribute("message",
	 * ""); return "signup"; }
	 */

	@PostMapping("/signup")
	public String addUser(
				@Valid User user,
				BindingResult bindingResult,
				Model model
		 ) {
		System.out.println(user.getPassword());
		System.out.println(user.getPassword2());
		
		
		 if (user.getPassword()!=null && user.getPassword2()!=null &&
				 !(user.getPassword().equals(user.getPassword2())) 
			) {
			  model.addAttribute("passEqualError", "Пароль отличается от подтверждения!");
			  return "signup"; 
		}
		 
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = UtilsController.getErrors(bindingResult);
			model.addAttribute("errors", errors);
			return "signup";
		}
		if (!userService.addUser(user)) { // check if user already exists
			model.addAttribute("message", "Пользователь уже существует!");
			return "signup";
		}
		model.addAttribute("message", "Вам был выслан код активации. Подтвердите свой адрес почты, пожалуйста!");
		return "login";
	}
	
	@GetMapping("/activate/{code}")
	public String activateEmail(Model model, @PathVariable String code) {
		boolean isActivated = userService.activateUser(code);
		if (isActivated) {
			model.addAttribute("message", "Пользователь успешно активизирован!"); 
		} else {
			model.addAttribute("message", "Не найден код активации. Возможно, Вы уже его использовали.");
		}
		return "login";
	}
}
