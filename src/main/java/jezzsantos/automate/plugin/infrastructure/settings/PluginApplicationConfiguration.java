package jezzsantos.automate.plugin.infrastructure.settings;

import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.interfaces.CliInstallPolicy;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.services.interfaces.IApplicationConfiguration;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class PluginApplicationConfiguration implements IApplicationConfiguration {

    @NotNull
    private final ApplicationSettingsState settings;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    @UsedImplicitly
    public PluginApplicationConfiguration() {

        this.settings = ApplicationSettingsState.getInstance();
    }

    @NotNull
    @Override
    public String getExecutablePath() {

        return Objects.requireNonNullElse(this.settings.pathToAutomateExecutable.getValue(), "");
    }

    @Override
    public void setExecutablePath(@NotNull String path) {

        var oldValue = this.settings.pathToAutomateExecutable.getValue();
        if (!Intrinsics.areEqual(oldValue, path)) {
            this.settings.pathToAutomateExecutable.setValue(path);
            this.support.firePropertyChange("ExecutablePath", oldValue, path);
        }
    }

    @Override
    public boolean getAuthoringMode() {

        return this.settings.authoringMode.getValue();
    }

    @Override
    public void setAuthoringMode(boolean on) {

        var oldValue = (boolean) this.settings.authoringMode.getValue();
        if (!Intrinsics.areEqual(oldValue, on)) {
            this.settings.authoringMode.setValue(on);
            this.support.firePropertyChange("AuthoringMode", oldValue, on);
        }
    }

    @NotNull
    @Override
    public EditingMode getEditingMode() {

        return this.settings.editingMode.getValue();
    }

    @Override
    public void setEditingMode(EditingMode mode) {

        var oldValue = this.settings.editingMode.getValue();
        if (!Intrinsics.areEqual(oldValue, mode)) {
            this.settings.editingMode.setValue(mode);
            this.support.firePropertyChange("EditingMode", oldValue, mode);
        }
    }

    @Override
    public boolean getViewCliLog() {

        return this.settings.viewCliLog.getValue();
    }

    @Override
    public void setViewCliLog(boolean view) {

        var oldValue = (boolean) this.settings.viewCliLog.getValue();
        if (!Intrinsics.areEqual(oldValue, view)) {
            this.settings.viewCliLog.setValue(view);
            this.support.firePropertyChange("ViewCliLog", oldValue, view);
        }
    }

    @Override
    public @NotNull CliInstallPolicy getCliInstallPolicy() {

        return this.settings.cliInstallPolicy.getValue();
    }

    @Override
    public void setCliInstallPolicy(CliInstallPolicy policy) {

        var oldValue = (CliInstallPolicy) this.settings.cliInstallPolicy.getValue();
        if (!Intrinsics.areEqual(oldValue, policy)) {
            this.settings.cliInstallPolicy.setValue(policy);
            this.support.firePropertyChange("CliInstallPolicy", oldValue, policy);
        }
    }

    @Override
    public void addListener(@NotNull PropertyChangeListener listener) {

        this.support.addPropertyChangeListener(listener);
    }

    @Override
    public void removeListener(@NotNull PropertyChangeListener listener) {

        this.support.removePropertyChangeListener(listener);
    }
}
