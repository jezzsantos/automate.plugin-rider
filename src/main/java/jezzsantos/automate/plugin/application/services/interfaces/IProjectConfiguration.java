package jezzsantos.automate.plugin.application.services.interfaces;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.interfaces.CliInstallPolicy;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.StringWithDefault;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeListener;

@SuppressWarnings("unused")
public interface IProjectConfiguration {

    static IProjectConfiguration getInstance(Project project) {

        return project.getService(IProjectConfiguration.class);
    }

    void addListener(@NotNull PropertyChangeListener listener);

    void removeListener(@NotNull PropertyChangeListener listener);

    boolean getAuthoringMode();

    void setAuthoringMode(boolean on);

    @NotNull
    EditingMode getEditingMode();

    void setEditingMode(EditingMode mode);

    boolean getViewCliLog();

    void setViewCliLog(boolean view);

    @NotNull
    StringWithDefault getExecutablePath();

    void setExecutablePath(@NotNull StringWithDefault path);

    @NotNull
    CliInstallPolicy getCliInstallPolicy();

    void setCliInstallPolicy(CliInstallPolicy policy);

    boolean allowUsageCollection();

    void setAllowUsageCollection(boolean on);
}
