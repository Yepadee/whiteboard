package org.microboard.whiteboard.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.microboard.whiteboard.model.task.Task;
import org.microboard.whiteboard.model.task.visitors.TaskAccessValidator;
import org.microboard.whiteboard.model.user.User;
import org.microboard.whiteboard.model.user.visitors.HomePageGetter;
import org.microboard.whiteboard.services.task.TaskService;
import org.microboard.whiteboard.services.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private TaskService taskService;
	
	Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	
	
	@GetMapping("/tasks")
	public String getOutstandingTaskPage(Model model) {
		User user = userService.getLoggedInUser();
		HomePageGetter homePageGetter = new HomePageGetter(model);
		user.accept(homePageGetter);
		return homePageGetter.getResult();
	}
	
	@GetMapping("/tasks/{id}")
	public String getSubmissionPage(Model model, @PathVariable long id) {
		User user = userService.getLoggedInUser();
		Optional<Task> maybeTask = taskService.getTask(id);
		if (maybeTask.isPresent()) {
			Task task = maybeTask.get();
			TaskAccessValidator accessValidator = new TaskAccessValidator(user);
			task.accept(accessValidator);
			if (accessValidator.getResult()) {
				model.addAttribute("task", task);
				model.addAttribute("user", user);
				return "submission";
			} else {
				return "access_denied";
			}
		} else {
			return "error";
		}
	}
	
	@PostMapping("/tasks/{id}")
	public String submitTask(@PathVariable long id, @ModelAttribute(name = "comments") String comments) {
		Optional<Task> maybeTask = taskService.getTask(id);
		if (maybeTask.isPresent()) {
			Task task = maybeTask.get();
			User user = userService.getLoggedInUser();
			TaskAccessValidator accessValidator = new TaskAccessValidator(user);
			task.accept(accessValidator);
			if (accessValidator.getResult()) {
				task.setTxtSubmission(comments);
				taskService.updateTask(task);
				/*TODO:
				 * Record date
				 * Add action to log
				 * Build file uploader/downloader
				 */
				return "redirect:/user/tasks/" + id;
			} else {
				return "access_denied";
			}
		} else {
			return "error";
		}
	}
	
	@PostMapping("/testUpload")
	public String UploadPage(Model model, @RequestParam("files") MultipartFile[] files) {
		String uploadDirectory = System.getProperty("user.dir") + "/uploads";
		StringBuilder fileNames = new StringBuilder();
		for (MultipartFile file : files) {
			Path fileNameAndPath = Paths.get(uploadDirectory,file.getOriginalFilename());
			fileNames.append(file.getOriginalFilename());
			try {
				Files.write(fileNameAndPath, file.getBytes());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		model.addAttribute("msg","Success: "+fileNames.toString());
		return "submission";
	}
	

}

