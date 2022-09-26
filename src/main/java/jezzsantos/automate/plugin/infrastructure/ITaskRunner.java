package jezzsantos.automate.plugin.infrastructure;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;

public interface ITaskRunner {

    <TResult> TResult runToCompletion(@NotNull String title, @NotNull Callable<TResult> task) throws Exception;

    <TResult> TResult runToCompletion(@NotNull Project project, @NotNull String title, @NotNull Callable<TResult> task) throws Exception;
}

