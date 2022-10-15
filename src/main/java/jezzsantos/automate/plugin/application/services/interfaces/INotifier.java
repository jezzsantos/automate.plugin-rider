package jezzsantos.automate.plugin.application.services.interfaces;

import jezzsantos.automate.plugin.infrastructure.ui.ExceptionHandler;
import jezzsantos.automate.plugin.infrastructure.ui.IntelliJNotifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface INotifier {

    static INotifier getInstance() {

        return new IntelliJNotifier();
    }

    void alert(@NotNull NotificationType type, @NotNull String title, @NotNull String message, @Nullable ExceptionHandler.LinkDescriptor link);
}
