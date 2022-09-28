package jezzsantos.automate.plugin.common;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.infrastructure.ui.ExceptionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

public class Try {

    @Nullable
    public static <T> T andHandle(@NotNull Project project, @NotNull Callable<T> action, @NotNull String errorMessage) {

        try {
            return action.call();
        } catch (Exception ex) {
            ExceptionHandler.handleError(project, errorMessage, ex, null);
        }

        return null;
    }

    public static void andHandle(@NotNull Project project, @NotNull VoidCallable action, @NotNull String errorMessage) {

        try {
            action.call();
        } catch (Exception ex) {
            ExceptionHandler.handleError(project, errorMessage, ex, null);
        }
    }

    public static void andHandle(@NotNull Project project, @NotNull VoidCallable action, @NotNull Runnable andThen, @NotNull String errorMessage) {

        try {
            action.call();
        } catch (Exception ex) {
            ExceptionHandler.handleError(project, errorMessage, ex, null);
            return;
        }

        andThen.run();
    }

    @Nullable
    public static <T> T safely(@NotNull Callable<T> action) {

        try {
            return action.call();
        } catch (Exception ignored) {
        }

        return null;
    }

    public static void safely(@NotNull VoidCallable action) {

        try {
            action.call();
        } catch (Exception ignored) {
        }
    }
}
