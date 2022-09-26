package jezzsantos.automate.plugin.application.services.interfaces;

import com.intellij.openapi.application.ApplicationManager;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeListener;

public interface IApplicationConfiguration {

    static IApplicationConfiguration getInstance() {

        return ApplicationManager.getApplication().getService(IApplicationConfiguration.class);
    }

    @NotNull
    String getExecutablePath();

    void setExecutablePath(@NotNull String path);

    boolean getAuthoringMode();

    void setAuthoringMode(boolean on);

    @NotNull
    EditingMode getEditingMode();

    void setEditingMode(EditingMode mode);

    boolean getViewCliLog();

    void setViewCliLog(boolean view);

    void addListener(@NotNull PropertyChangeListener listener);

    void removeListener(@NotNull PropertyChangeListener listener);
}
