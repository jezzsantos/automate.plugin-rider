package jezzsantos.automate.plugin.application.services.interfaces;

import jezzsantos.automate.plugin.infrastructure.ui.ExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface INotifier {

    void alert(@NotNull NotificationType type, @NotNull String title, @NotNull String message, @Nullable ExceptionHandler.LinkDescriptor link);
}
