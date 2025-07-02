package app.todo.taskmanagement.domain;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import app.todo.usermanagement.domain.User;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    Slice<Task> findAllBy(Pageable pageable);
    List<Task> findByUser(User user);
    List<Task> findAll();
}
