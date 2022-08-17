package jezzsantos.automate.plugin.application.services.interfaces;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeListener;

public interface IConfiguration {

    static IConfiguration getInstance(Project project) {
        return project.getService(IConfiguration.class);
    }

    @NotNull
    String getExecutablePath();

    void setExecutablePath(@NotNull String path);

    boolean getAuthoringMode();

    void setAuthoringMode(boolean on);

    EditingMode getEditingMode();

    void setEditingMode(EditingMode mode);

    boolean getViewCliLog();

    void setViewCliLog(boolean view);

    void addListener(@NotNull PropertyChangeListener listener);

    void removeListener(@NotNull PropertyChangeListener listener);
}
