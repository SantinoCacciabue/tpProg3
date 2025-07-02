package app.todo.taskmanagement.ui.view;

import app.todo.base.ui.component.ViewToolbar;
import app.todo.taskmanagement.domain.Task;
import app.todo.usermanagement.domain.User;
import app.todo.usermanagement.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

@Route("user-list")
@PageTitle("Lista de usuarios")
@Menu(order = 1, icon = "vaadin:user", title = "Usuarios")
@PermitAll
public class UserListView extends Main {

	private final UserService userService;

	private final TextField nameField;
	private final TextField surNameField;
	private final TextField dniField;
	private final IntegerField ageField;
	private final Button createUserBtn;
	private final Grid<User> userGrid;
	private User selectedUser;

	public UserListView(UserService userService) {
		this.userService = userService;

		nameField = new TextField("Nombre");
		surNameField = new TextField("Apellido");
		dniField = new TextField("DNI");
		ageField = new IntegerField("Edad");

		createUserBtn = new Button("Crear usuario", event -> createUser());
		createUserBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

		userGrid = new Grid<>();
		userGrid.addColumn(User::getDni).setHeader("Dni");
		userGrid.addColumn(User::getName).setHeader("Nombre");
		userGrid.addColumn(User::getSurName).setHeader("Apellido");
		userGrid.addColumn(User::getAge).setHeader("Edad");
		userGrid.setItems(userService.findAll());
		userGrid.addComponentColumn(user -> {
			Button deleteBtn = new Button("Eliminar", click -> showConfirmDialog(user));
			deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_SMALL);
			return deleteBtn;
		}).setHeader("Acciones");
		userGrid.setSizeFull();
		

		setSizeFull();
		addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL, LumoUtility.Display.FLEX,
				LumoUtility.FlexDirection.COLUMN);
		userGrid.asSingleSelect().addValueChangeListener(event -> {
		    selectedUser = event.getValue();
		    if (selectedUser != null) {
		        nameField.setValue(selectedUser.getName());
		        surNameField.setValue(selectedUser.getSurName());
		        dniField.setValue(selectedUser.getDni());
		        ageField.setValue(selectedUser.getAge());
		        createUserBtn.setText("Actualizar usuario");
		    } else {
		        clearForm();
		    }
		});


		add(new ViewToolbar("Usuarios",ViewToolbar.group()));
		add(nameField, surNameField, ageField, dniField, createUserBtn, userGrid);
	}

	private void createUser() {
		String dni = dniField.getValue();
		if (nameField.isEmpty() || surNameField.isEmpty() || dniField.isEmpty() || ageField.isEmpty()) {
	        Notification.show("Completa todos los campos", 3000, Notification.Position.MIDDLE)
	                .addThemeVariants(NotificationVariant.LUMO_ERROR);
	        return;
	    }
		
		if (!dni.matches("\\d{8}")) {
		    Notification.show("El DNI debe tener exactamente 8 dígitos numéricos", 3000, Notification.Position.MIDDLE)
		            .addThemeVariants(NotificationVariant.LUMO_ERROR);
		    return;
		}
		

	    if (selectedUser == null) {
	        User user = new User();
	        user.setName(nameField.getValue());
	        user.setSurName(surNameField.getValue());
	        user.setDni(dniField.getValue());
	        user.setAge(ageField.getValue());
	        userService.save(user);
	        Notification.show("Usuario creado", 3000, Notification.Position.BOTTOM_END)
	                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
	    } else {
	        selectedUser.setName(nameField.getValue());
	        selectedUser.setSurName(surNameField.getValue());
	        selectedUser.setDni(dniField.getValue());
	        selectedUser.setAge(ageField.getValue());
	        userService.save(selectedUser);
	        Notification.show("Usuario actualizado", 3000, Notification.Position.BOTTOM_END)
	                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
	    }

	    clearForm();
	    userGrid.setItems(userService.findAll());
	}

	private void showConfirmDialog(User user) {
		ConfirmDialog dialog = new ConfirmDialog();
		dialog.setHeader("¿Estás seguro?");
		dialog.setText("¿Quieres eliminar este usuario?");
		dialog.setCancelable(true);
		dialog.setConfirmText("Sí, eliminar");
		dialog.setConfirmButtonTheme("error primary");
		dialog.setCancelText("Cancelar");

		dialog.addConfirmListener(event -> {
			userService.delete(user);
			userGrid.setItems(userService.findAll());
			Notification.show("Usuario eliminada", 3000, Notification.Position.BOTTOM_END)
					.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		});

		dialog.open();
	}
	private void clearForm() {
	    selectedUser = null;
	    nameField.clear();
	    surNameField.clear();
	    dniField.clear();
	    ageField.clear();
	    createUserBtn.setText("Crear usuario");
	}
}
