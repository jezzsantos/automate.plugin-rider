package jezzsantos.automate.plugin.infrastructure.ui;

import com.intellij.ide.BrowserUtil;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.services.interfaces.INotifier;
import jezzsantos.automate.plugin.application.services.interfaces.NotificationType;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.notification.NotificationAction.createSimple;

public class IntelliJNotifier implements INotifier {

    @Override
    public void alert(@NotNull NotificationType type, @NotNull String title, @NotNull String message, @Nullable LinkDescriptor link) {

        alertInternal(type, null, title, message, link);
    }

    @Override
    public void alert(@NotNull NotificationType type, @Nullable Project project, @NotNull String title, @NotNull String message, @Nullable LinkDescriptor link) {

        alertInternal(type, project, title, message, link);
    }

    @Override
    public void alert(@NotNull NotificationType type, @NotNull String title, @NotNull String message, @Nullable LinkDescriptor link, @NotNull Runnable andThen) {

        alertInternal(type, null, title, message, link);
        ApplicationManager.getApplication().invokeLater(andThen);
    }

    @Override
    public void alert(@NotNull NotificationType type, @Nullable Project project, @NotNull String title, @NotNull String message, @Nullable LinkDescriptor link, @NotNull Runnable andThen) {

        alertInternal(type, project, title, message, link);
        ApplicationManager.getApplication().invokeLater(andThen);
    }

    /**
     * Displays a notification balloon in the IDE, that also supports a clickable hyperlink.
     * If {@code project} is null, then the balloon will not appear, but the notification will still be created.
     */
    private static void alertInternal(@NotNull NotificationType type, @Nullable Project project, @NotNull String title, @NotNull String message, @Nullable LinkDescriptor link) {

        com.intellij.notification.NotificationType notificationType = null;
        switch (type) {

            case INFO -> notificationType = com.intellij.notification.NotificationType.INFORMATION;
            case WARNING -> notificationType = com.intellij.notification.NotificationType.WARNING;
            case ERROR -> notificationType = com.intellij.notification.NotificationType.ERROR;
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
}
