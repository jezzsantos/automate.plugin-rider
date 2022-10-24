package jezzsantos.automate.plugin.infrastructure;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

public interface ITaskRunner {

    <TResult> TResult runModal(@NotNull String title, @NotNull Callable<TResult> task) throws Exception;

    <TResult> TResult runModal(@NotNull Project project, @NotNull String title, @NotNull Callable<TResult> task) throws Exception;

    void runModeless(@Nullable Project project, @NotNull String title, @NotNull Runnable task);
}

