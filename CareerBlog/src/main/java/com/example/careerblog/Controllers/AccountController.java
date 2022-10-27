package com.example.careerblog.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.careerblog.Models.User;
import com.example.careerblog.service.UserService;

@Controller
@RequestMapping("/account")
@PreAuthorize("hasAuthority('USER')")	//enabling USER authority for page
public class AccountController {
	@Autowired
	private UserService userService;
	

	@GetMapping
	public String getAccount(
			Model model, 
			@AuthenticationPrincipal User user
			) {
		model.addAttribute("username", user.getUsername());
		model.addAttribute("password", user.getPassword());
		model.addAttribute("email", user.getEmail());
		return "account";
	}
	
	@PostMapping
	public String updateAccount(
			@AuthenticationPrincipal User user, 
			@RequestParam String username,
			@RequestParam String password,
			@RequestParam String email
			){
		userService.updateAccount(user, username, password, email);
		return "redirect:/account";
	}
	
}
