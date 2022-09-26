package jezzsantos.automate.plugin.application.services.interfaces;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeListener;

@SuppressWarnings("unused")
public interface IProjectConfiguration {

    static IProjectConfiguration getInstance(Project project) {

        return project.getService(IProjectConfiguration.class);
    }

    void addListener(@NotNull PropertyChangeListener listener);

    void removeListener(@NotNull PropertyChangeListener listener);
}
