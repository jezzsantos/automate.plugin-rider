package jezzsantos.automate.plugin.infrastructure.ui;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExceptionHandler {

    public static void handle(@NotNull jezzsantos.automate.plugin.application.services.interfaces.NotificationType type, @NotNull String title, @NotNull String message) {

        handleInternal(type, null, title, message);
    }

    public static void handleError(@NotNull Project project, @NotNull String title, @NotNull Exception exception) {

        handleInternal(jezzsantos.automate.plugin.application.services.interfaces.NotificationType.ERROR, project, title, exception.getMessage());
    }

    private static void handleInternal(@NotNull jezzsantos.automate.plugin.application.services.interfaces.NotificationType type, @Nullable Project project, @NotNull String title, @NotNull String message) {

        NotificationType notificationType = null;
        switch (type) {

            case INFO -> notificationType = NotificationType.INFORMATION;
            case ERROR -> notificationType = NotificationType.ERROR;
        }

        NotificationGroupManager.getInstance()
          .getNotificationGroup(AutomateBundle.message("notification.group.name"))
          .createNotification(title, message, notificationType)
          .notify(project);
    }
}
