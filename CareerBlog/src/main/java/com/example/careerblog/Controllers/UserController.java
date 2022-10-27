package com.example.careerblog.Controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.careerblog.Models.Role;
import com.example.careerblog.Models.User;
import com.example.careerblog.service.UserService;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")	//enabling ADMIN authority for page
public class UserController {
	@Autowired
	private UserService userService;


	@GetMapping
	public String userList(Model model) {
		model.addAttribute("users", userService.findAll());
		return "user";
	}

	@GetMapping("{user}")
	public String userEditForm(@PathVariable User user, Model model) {
		model.addAttribute("user", user);
		model.addAttribute("roles", Role.values()); //all possible user roles transfer
		/*for ( Enum role : Role.values()) { // test use: if user has a role print it
			if (user.getRoles().contains(role)){
				System.out.println(role);
			}
		}*/
		return "user-edit";
	}
	
	@PostMapping
	public String userSave(
			@RequestParam String username,
			@RequestParam String password,
			@RequestParam String email,
			@RequestParam Map<String, String> form,
			@RequestParam("userId") User user,
			RedirectAttributes redirectAttributes
			) {
		
		userService.saveUser(user, username, password, email, form);
		redirectAttributes.addAttribute("id", user.getId());
		
		return "redirect:/user/{id}/";
	}
	
	@PostMapping("{id}/remove") // удаление пользователя из БД
	public String userRemove(@PathVariable(value = "id") long id, Model model) {
		
		userService.removeUser(id);
		return "redirect:/user";
	}
}
