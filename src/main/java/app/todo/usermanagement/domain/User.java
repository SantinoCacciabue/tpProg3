package app.todo.usermanagement.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

import app.todo.taskmanagement.domain.Task;

import java.util.HashSet;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String surName;
    
    @Column(nullable =false)
    private int age;
    
    
    @Size(min = 8, max = 8, message = "El DNI debe tener exactamente 8 caracteres")
    @Column (nullable = false)
    private String dni;

    public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Task> tasks = new HashSet<>();
    

    public Long getId() {
        return id;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurName() {
		return surName;
	}

	public void setSurName(String userName) {
		this.surName = userName;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
        task.setUser(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setUser(null);
    }
    
    @Override
    public String toString() {
    	return getName() + " " + getSurName();
    }
}
