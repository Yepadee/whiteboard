package org.microboard.whiteboard.services;

import java.util.Optional;

import org.microboard.whiteboard.model.task.Task;
import org.microboard.whiteboard.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
	@Autowired
	private TaskRepository taskRepository;
	
	public Optional<Task> getTask(long id) {
		return taskRepository.findById(id);
	}
}
