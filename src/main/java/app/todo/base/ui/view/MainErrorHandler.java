package app.todo.base.ui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MainErrorHandler {

    private static final Logger log = LoggerFactory.getLogger(MainErrorHandler.class);

    @Bean
    public VaadinServiceInitListener errorHandlerInitializer() {
        return (event) -> event.getSource().addSessionInitListener(
                sessionInitEvent -> sessionInitEvent.getSession().setErrorHandler(errorEvent -> {
                    log.error("Ha ocurrido un error", errorEvent.getThrowable());
                    errorEvent.getComponent().flatMap(Component::getUI).ifPresent(ui -> {
                        var notification = new Notification(
                                "Ha ocurrido un error. Por favor vuelva a intentarlo");
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        notification.setPosition(Notification.Position.TOP_CENTER);
                        notification.setDuration(3000);
                        ui.access(notification::open);
                    });
                }));
    }
}
