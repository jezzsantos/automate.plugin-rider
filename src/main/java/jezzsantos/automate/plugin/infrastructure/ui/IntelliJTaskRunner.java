package jezzsantos.automate.plugin.infrastructure.ui;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.infrastructure.ITaskRunner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

public class IntelliJTaskRunner implements ITaskRunner {

    @Override
    public <TResult> TResult runModal(@NotNull String title, @NotNull Callable<TResult> task) throws Exception {

        return runToCompletionInternal(null, title, task);
    }

    @Override
    public <TResult> TResult runModal(@NotNull Project project, @NotNull String title, @NotNull Callable<TResult> task) throws Exception {

        return runToCompletionInternal(project, title, task);
    }

    @Override
    public void runModeless(@Nullable Project project, @NotNull String title, @NotNull Runnable task) {

        new Task.Backgroundable(project, title) {

            @Override
            public void run(@NotNull ProgressIndicator indicator) {

                task.run();
            }
        }.queue();
    }

    private <TResult> TResult runToCompletionInternal(@Nullable Project project, @NotNull String title, @NotNull Callable<TResult> task) throws Exception {

        return ProgressManager.getInstance()
          .run(new Task.WithResult<TResult, Exception>(project, title, false) {
              @Override
              protected TResult compute(@NotNull ProgressIndicator progressIndicator) throws Exception {

                  return task.call();
              }
          });
    }
}
