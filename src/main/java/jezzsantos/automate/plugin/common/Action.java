package jezzsantos.automate.plugin.common;

import java.util.function.Consumer;

public interface Action<T> {

    void run(Consumer<T> consumer);
}
