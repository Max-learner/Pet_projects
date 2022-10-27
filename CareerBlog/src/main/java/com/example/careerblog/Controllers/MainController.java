package com.example.careerblog.Controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.careerblog.Models.Message;
import com.example.careerblog.Models.User;
import com.example.careerblog.Repository.MessageRepository;

@Controller
public class MainController {

	@Autowired
	private MessageRepository messageRepository;
	
	@Value("${upload.path}") 	//	Spring finds property "upload.path" in application.properties  or in context
	private String uploadPath;	//	upload.path is set in the variable uploadPath

	@GetMapping("/greeting")
	public String greeting(Model model) {
		model.addAttribute("title", "Добро пожаловать!");
		return "greeting";
	}

	@GetMapping("/") // main page
	public String showHomePage(Model model) {
		return "home";
	}
	
	@GetMapping("/blog")	 // вывод списка сообщений с фильтрацией
	public String showMessages(
			@ModelAttribute("message") Message message,
			@RequestParam(required = false, defaultValue = "") String filter,
			Model model
			) {
		model.addAttribute("title", "Список сообщений");
		Iterable<Message> messages = messageRepository.findAll();
		
		if (filter != null && !filter.isEmpty()) {
			messages = messageRepository.findByTag(filter);
		} else {
			messages = messageRepository.findAll();
		}
		
		model.addAttribute("messages", messages);
		model.addAttribute("filter", filter);
		return "blog";
	}
	
	@GetMapping("/blog-edit") // вывод списка сообщений с фильтрацией
	public String showMessagesEdit(
			@ModelAttribute("message") Message message,
			@RequestParam(required = false, defaultValue = "") String filter,
			Model model
			) {
		model.addAttribute("title", "Список сообщений");
		Iterable<Message> messages = messageRepository.findAll();
		
		if (filter != null && !filter.isEmpty()) {
			messages = messageRepository.findByTag(filter);
		} else {
			messages = messageRepository.findAll();
		}
		
		model.addAttribute("messages", messages);
		model.addAttribute("filter", filter);
		return "blog-edit";
	}

	@PostMapping("/blog-edit")	//saving new message
	public String addMessage(
			@AuthenticationPrincipal User user,
			@Valid Message message,			// blog post from form
			BindingResult bindingResult,	// arguments and validation error list, should be BEFORE Model model
			Model model,
			@RequestParam("file") MultipartFile file
			) throws IOException {
		
		message.setAuthor(user);
		
		if (bindingResult.hasErrors()) {			//	collect form validation errors
			 Map<String, String> errorsMap = UtilsController.getErrors(bindingResult);
			 model.addAttribute("textError", errorsMap.get("textError"));
			 model.addAttribute("tagError", errorsMap.get("tagError"));
			 model.addAttribute("errorsMap", errorsMap);
		} else {

				if (file != null && !file.getOriginalFilename().isEmpty()) { 	//	empty filename and empty file check
					
					File uploadDir = new File(uploadPath);
					
					if (!uploadDir.exists()) {						//	create file directory if not exist
						uploadDir.mkdir();
					}
					String uuidFile = UUID.randomUUID().toString();	//	Universal unique ID
					String uploadedFilename = uuidFile + "."	+ file.getOriginalFilename();	//	set name for uploaded file
					
					file.transferTo(new File(uploadPath + "/" + uploadedFilename));
					
					message.setFilename(uploadedFilename);
				}
				messageRepository.save(message);
			}
		
		Iterable<Message> messages = messageRepository.findAll();
		model.addAttribute("messages", messages);

		return "blog-edit";
	}

	@GetMapping("/edit/{id}") //вывод поста для редактирования
	public String blogPostEdit(
			@PathVariable(value = "id") long id, 
			Model model
			) {
		
		if (!messageRepository.existsById(id)) {
			return "redirect:/blog-edit";
		}
		
		Optional<Message> message = messageRepository.findById(id);
		ArrayList<Message> res = new ArrayList<>();
		message.ifPresent(res::add);
		model.addAttribute("message", res);
		System.out.println(message);
		return "blog-post-edit";
	}
	
	@PostMapping("/edit/{id}") //сохранение изменений поста в БД
	public String blogPostUpdate(
					@AuthenticationPrincipal User user,
					@Valid Message message,
					BindingResult bindingResult,	// arguments and validation error list, should be BEFORE Model model
					//@RequestParam String text,
					//@RequestParam String tag,
					@PathVariable(value = "id") long id,
					@RequestParam("file") MultipartFile file,
					Model model
				) throws IOException {
				
				message.setAuthor(user);
				
				if (bindingResult.hasErrors()) { 
					 Map<String, String> errorsMap = UtilsController.getErrors(bindingResult);
					 model.addAttribute("textError", errorsMap.get("textError"));
					 model.addAttribute("tagError", errorsMap.get("tagError"));
					 model.addAttribute("errorsMap", errorsMap);
					 System.out.println(errorsMap);
				  } else {
				 

						if (file != null && !file.getOriginalFilename().isEmpty()) { 	//	empty filename and empty file check
							File uploadDir = new File(uploadPath);
							if (!uploadDir.exists()) {						//	create file directory if not exist
								uploadDir.mkdir();
							}
							String uuidFile = UUID.randomUUID().toString();	//	Universal unique ID
							String uploadedFilename = uuidFile + "."	+ file.getOriginalFilename();	//	set name for uploaded file
							
							file.transferTo(new File(uploadPath + "/" + uploadedFilename));
							
							message.setFilename(uploadedFilename);
						}
						
						messageRepository.save(message);
					}
		return "redirect:/blog-edit";
	}
	
	@PostMapping("/remove/{id}") // удаление поста из БД
	public String messageRemove(
			@PathVariable(value = "id") long id, 
			Model model
			) {
		Message message = messageRepository.findById(id).orElseThrow(null);
		messageRepository.delete(message);
		
		return "redirect:/blog-edit";
	}

}
