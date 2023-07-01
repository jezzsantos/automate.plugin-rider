package jezzsantos.automate.plugin.common;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

public class Try {

    /**
     * This method is required so that we can run a task in places where we are unable to show progress.
     * For example in {@code AnAction.update}
     */
    @SuppressWarnings("unused")
    @Nullable
    public static <TResult> TResult andHandleWithoutProgress(@NotNull Project project, @NotNull String description, @NotNull Callable<TResult> action, @NotNull String errorMessage) {

        try {
            return action.call();
        } catch (Exception ex) {
            var notifier = IContainer.getNotifier();
            notifier.alert(jezzsantos.automate.plugin.application.services.interfaces.NotificationType.ERROR, project, errorMessage, ex.getMessage(), null);
        }

        return null;
    }

    @Nullable
    public static <TResult> TResult andHandle(@NotNull Project project, @NotNull String description, @NotNull Callable<TResult> action, @NotNull String errorMessage) {

        var progressManager = ProgressManager.getInstance();

        try {
            return progressManager
              .runProcessWithProgressSynchronously(action::call, description, false, project);
        } catch (Exception ex) {
            var notifier = IContainer.getNotifier();
            notifier.alert(jezzsantos.automate.plugin.application.services.interfaces.NotificationType.ERROR, project, errorMessage, ex.getMessage(), null);
        }

        return null;
    }

    public static void andHandle(@NotNull Project project, @NotNull String description, @NotNull VoidCallable action, @NotNull String errorMessage) {

        var progressManager = ProgressManager.getInstance();

        try {
            progressManager
              .runProcessWithProgressSynchronously(() -> {

                  action.call();
                  return true;
              }, description, false, project);
        } catch (Exception ex) {
            var notifier = IContainer.getNotifier();
            notifier.alert(jezzsantos.automate.plugin.application.services.interfaces.NotificationType.ERROR, project, errorMessage, ex.getMessage(), null);
        }
    }

    public static void andHandle(@NotNull Project project, @NotNull String description, @NotNull VoidCallable action, @NotNull Runnable andThen, @NotNull String errorMessage) {

        var progressManager = ProgressManager.getInstance();

        try {
            progressManager
              .runProcessWithProgressSynchronously(() -> {

                  action.call();
                  return true;
              }, description, false, project);
        } catch (Exception ex) {
            var notifier = IContainer.getNotifier();
            notifier.alert(jezzsantos.automate.plugin.application.services.interfaces.NotificationType.ERROR, project, errorMessage, ex.getMessage(), null);
            return;
        }

        andThen.run();
    }

    @Nullable
    public static <TResult> TResult safely(@NotNull Callable<TResult> action) {

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
