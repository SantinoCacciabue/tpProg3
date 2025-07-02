package app.todo.taskmanagement.service;

import app.todo.taskmanagement.domain.Task;
import app.todo.taskmanagement.domain.TaskRepository;
import app.todo.usermanagement.domain.User;

import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class TaskService {

    private final TaskRepository taskRepository;

    private final Clock clock;

    TaskService(TaskRepository taskRepository, Clock clock) {
        this.taskRepository = taskRepository;
        this.clock = clock;
    }

    public void createTask(String description, LocalDate dueDate, User user) {
        Task task = new Task();
        task.setDescription(description);
        task.setCreationDate(Instant.now());
        task.setDueDate(dueDate);
        task.setDone(false);
        task.setUser(user);
        taskRepository.save(task);
    }

    public void updateTask(Task task){
        taskRepository.saveAndFlush(task);
    }

    public List<Task> list(Pageable pageable) {
        return taskRepository.findAllBy(pageable).toList();
    }
    public void delete(Task task) {
        taskRepository.delete(task);
    }
    public List<Task> findByUser(User user) {
        return taskRepository.findByUser(user);
    }
    public List<Task> findAll() {
        return taskRepository.findAll();
    }
    
    

}
