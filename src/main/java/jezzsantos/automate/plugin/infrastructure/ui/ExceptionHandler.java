package jezzsantos.automate.plugin.infrastructure.ui;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExceptionHandler {

    public static void handle(@Nullable Project project, @NotNull Exception exception, @NotNull String title) {

        NotificationGroupManager.getInstance()
          .getNotificationGroup("automate")
          .createNotification(title, exception.getMessage(), NotificationType.ERROR)
          .notify(project);
    }

}
