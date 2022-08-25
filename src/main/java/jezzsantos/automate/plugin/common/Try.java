package jezzsantos.automate.plugin.common;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;

public class Try {

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
