package jezzsantos.automate.plugin.infrastructure.ui;

import jezzsantos.automate.plugin.application.services.interfaces.INotifier;
import jezzsantos.automate.plugin.application.services.interfaces.NotificationType;
import org.jetbrains.annotations.NotNull;

public class IntelliJNotifier implements INotifier {

    @Override
    public void alert(@NotNull NotificationType type, @NotNull String title, @NotNull String message) {

        ExceptionHandler.handle(type, title, message);
    }
}
