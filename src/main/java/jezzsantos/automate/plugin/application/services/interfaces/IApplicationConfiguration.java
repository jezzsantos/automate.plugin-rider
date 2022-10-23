package jezzsantos.automate.plugin.application.services.interfaces;

import com.intellij.openapi.application.ApplicationManager;
import jezzsantos.automate.plugin.application.interfaces.CliInstallPolicy;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.StringWithDefault;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeListener;

@SuppressWarnings("unused")
public interface IApplicationConfiguration {

    static IApplicationConfiguration getInstance() {

        return ApplicationManager.getApplication().getService(IApplicationConfiguration.class);
    }

    void addListener(@NotNull PropertyChangeListener listener);

    void removeListener(@NotNull PropertyChangeListener listener);

    @NotNull
    StringWithDefault getExecutablePath();

    void setExecutablePath(@NotNull StringWithDefault path);

    boolean getAuthoringMode();

    void setAuthoringMode(boolean on);

    @NotNull
    EditingMode getEditingMode();

    void setEditingMode(EditingMode mode);

    boolean getViewCliLog();

    void setViewCliLog(boolean view);

    @NotNull
    CliInstallPolicy getCliInstallPolicy();

    void setCliInstallPolicy(CliInstallPolicy policy);

    boolean allowUsageCollection();

    void setAllowUsageCollection(boolean on);
}
