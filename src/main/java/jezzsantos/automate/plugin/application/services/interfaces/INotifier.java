package jezzsantos.automate.plugin.application.services.interfaces;

import org.jetbrains.annotations.NotNull;

public interface INotifier {

    void alert(@NotNull NotificationType type, @NotNull String title, @NotNull String message);
}
