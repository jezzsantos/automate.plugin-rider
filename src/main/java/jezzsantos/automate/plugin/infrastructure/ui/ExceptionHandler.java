package jezzsantos.automate.plugin.infrastructure.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.notification.NotificationAction.createSimple;

public class ExceptionHandler {

    public static void handle(@NotNull jezzsantos.automate.plugin.application.services.interfaces.NotificationType type, @NotNull String title, @NotNull String message, @Nullable ExceptionHandler.LinkDescriptor link) {

        handleInternal(type, null, title, message, link);
    }

    public static void handleError(@NotNull Project project, @NotNull String title, @NotNull Exception exception, @Nullable ExceptionHandler.LinkDescriptor link) {

        handleInternal(jezzsantos.automate.plugin.application.services.interfaces.NotificationType.ERROR, project, title, exception.getMessage(), link);
    }

    private static void handleInternal(@NotNull jezzsantos.automate.plugin.application.services.interfaces.NotificationType type, @Nullable Project project, @NotNull String title, @NotNull String message, @Nullable ExceptionHandler.LinkDescriptor link) {

        NotificationType notificationType = null;
        switch (type) {

            case INFO -> notificationType = NotificationType.INFORMATION;
            case WARNING -> notificationType = NotificationType.WARNING;
            case ERROR -> notificationType = NotificationType.ERROR;
        }

        var notifier = NotificationGroupManager.getInstance();
        var notification = notifier
          .getNotificationGroup(AutomateBundle.message("notification.group.name"))
          .createNotification(title, message, notificationType);

        if (link != null) {
            notification.addAction(createSimple(link.getDescription(), () -> BrowserUtil.browse(link.getUrl())));
        }
        notification.notify(project);
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static class LinkDescriptor {

        private final String url;
        private final String description;

        public LinkDescriptor(@NotNull String url, @NotNull String description) {

            this.url = url;
            this.description = description;
        }

        @NotNull
        public String getUrl() {return this.url;}

        @NotNull
        public String getDescription() {return this.description;}
    }
}
