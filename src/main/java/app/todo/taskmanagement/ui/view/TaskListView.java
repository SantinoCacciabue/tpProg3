package app.todo.taskmanagement.ui.view;

import app.todo.base.ui.component.ViewToolbar;
import app.todo.taskmanagement.domain.Task;
import app.todo.taskmanagement.service.TaskService;
import app.todo.usermanagement.domain.User;
import app.todo.usermanagement.service.UserService;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Optional;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route("task-list")
@PageTitle("Lista de tareas")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Lista de tareas")
@PermitAll
public class TaskListView extends Main {

    private final TaskService taskService;

    final TextField description;
    final DatePicker dueDate;
    final Button createBtn;
    Grid<Task> taskGrid = new Grid<Task>();
    private final UserService userService;
    private final ComboBox<User> userComboBox;
    private final ComboBox<User> filterByUserComboBox;


    public TaskListView(TaskService taskService, Clock clock, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
        

        filterByUserComboBox = new ComboBox<>();
        filterByUserComboBox.setWidth("250px");
        filterByUserComboBox.setItems(userService.findAll());
        filterByUserComboBox.setItemLabelGenerator(user -> user.toString());
        filterByUserComboBox.setClearButtonVisible(true);
        filterByUserComboBox.setPlaceholder("Filtrar por usuario");

        
        filterByUserComboBox.addValueChangeListener(event -> {
            User selected = event.getValue();
            if (selected != null) {
                taskGrid.setItems(taskService.findByUser(selected));
            } else {
                taskGrid.setItems(taskService.findAll());
            }
        });
        userComboBox = new ComboBox<>();
        userComboBox.setItems(userService.findAll());
        userComboBox.setItemLabelGenerator(User::toString);
        userComboBox.setPlaceholder("Asignar a...");

        description = new TextField();
        description.setPlaceholder("Que quieres hacer?");
        description.setAriaLabel("Descripcion de la tarea");
        description.setMaxLength(Task.DESCRIPTION_MAX_LENGTH);
        description.setMinWidth("20em");

        dueDate = new DatePicker();
        dueDate.setPlaceholder("Vencimiento");
        dueDate.setAriaLabel("Vencimiento");

        createBtn = new Button("Crear", event -> createTask());
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withZone(clock.getZone())
                .withLocale(getLocale());
        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale());

        taskGrid = new Grid<>();
        taskGrid.setItems(query -> taskService.list(toSpringPageRequest(query)).stream());

        taskGrid.addComponentColumn(task -> {
                        Checkbox checkbox = new Checkbox(task.isDone());
                        checkbox.addValueChangeListener(event -> {
                                task.setDone(event.getValue());
                                taskService.updateTask(task);
                        });
                        return checkbox;
                }).setHeader("Hecho");

        taskGrid.addColumn(Task::getDescription).setHeader("Descripcion");
        taskGrid.addColumn(task -> Optional.ofNullable(task.getDueDate()).map(dateFormatter::format).orElse("Nunca"))
                .setHeader("Vencimiento");
        taskGrid.addColumn(task -> dateTimeFormatter.format(task.getCreationDate())).setHeader("Creación");
        taskGrid.addColumn(Task::getUser).setHeader("Asignado a:");
        taskGrid.addComponentColumn(task -> {
            Button deleteBtn = new Button("Eliminar", click -> showConfirmDialog(task));
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
            return deleteBtn;
        }).setHeader("Acciones");
        taskGrid.setSizeFull();

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);

        add(new ViewToolbar("Lista de tareas", ViewToolbar.group(description, dueDate, userComboBox, createBtn,filterByUserComboBox)));
        add(taskGrid);
    }

    private void createTask() {
    	if (userComboBox.getValue() == null) {
            Notification.show("Debes seleccionar un usuario", 3000, Notification.Position.MIDDLE)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        taskService.createTask(description.getValue(), dueDate.getValue(), userComboBox.getValue());
        taskGrid.getDataProvider().refreshAll();
        description.clear();
        dueDate.clear();
        userComboBox.clear();
        Notification.show("Tarea añadida", 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
    private void showConfirmDialog(Task task) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("¿Estás seguro?");
        dialog.setText("¿Querés eliminar esta tarea?");
        dialog.setCancelable(true);
        dialog.setConfirmText("Sí, eliminar");
        dialog.setConfirmButtonTheme("error primary");
        dialog.setCancelText("Cancelar");

        dialog.addConfirmListener(event -> {
            taskService.delete(task);
            taskGrid.getDataProvider().refreshAll();
            Notification.show("Tarea eliminada", 3000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        dialog.open();
    }

}
