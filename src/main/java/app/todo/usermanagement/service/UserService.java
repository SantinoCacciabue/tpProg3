package app.todo.usermanagement.service;

import app.todo.taskmanagement.domain.Task;
import app.todo.usermanagement.domain.User;
import app.todo.usermanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }
    public void delete(User user) {
    	userRepository.delete(user);
    }
    
    public void updateuser(User user){
        userRepository.saveAndFlush(user);
    }
}
