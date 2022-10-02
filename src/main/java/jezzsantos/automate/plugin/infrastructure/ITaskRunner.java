package jezzsantos.automate.plugin.infrastructure;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.infrastructure.ui.IntelliJTaskRunner;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;

public interface ITaskRunner {

    static ITaskRunner getInstance() {

        return new IntelliJTaskRunner();
    }

    <TResult> TResult runToCompletion(@NotNull String title, @NotNull Callable<TResult> task) throws Exception;

    <TResult> TResult runToCompletion(@NotNull Project project, @NotNull String title, @NotNull Callable<TResult> task) throws Exception;
}

