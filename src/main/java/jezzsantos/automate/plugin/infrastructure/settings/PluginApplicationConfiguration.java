package jezzsantos.automate.plugin.infrastructure.settings;

import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.interfaces.CliInstallPolicy;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.services.interfaces.IApplicationConfiguration;
import jezzsantos.automate.plugin.common.StringWithDefault;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PluginApplicationConfiguration implements IApplicationConfiguration {

    @NotNull
    private final ApplicationSettingsState settings;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    @UsedImplicitly
    public PluginApplicationConfiguration() {

        this.settings = ApplicationSettingsState.getInstance();
    }

    @Override
    public void addListener(@NotNull PropertyChangeListener listener) {

        this.support.addPropertyChangeListener(listener);
    }

    @Override
    public void removeListener(@NotNull PropertyChangeListener listener) {

        this.support.removePropertyChangeListener(listener);
    }

    @NotNull
    @Override
    public StringWithDefault getExecutablePath() {

        return this.settings.executablePath.getValue();
    }

    @Override
    public void setExecutablePath(@NotNull StringWithDefault newValue) {

        var oldValue = (StringWithDefault) this.settings.executablePath.getValue();
        if (!Intrinsics.areEqual(oldValue, newValue)) {
            this.settings.executablePath.setValue(newValue);
            this.support.firePropertyChange("ExecutablePath", oldValue, newValue);
        }
    }

    @Override
    public boolean getAuthoringMode() {

        return this.settings.authoringMode.getValue();
    }

    @Override
    public void setAuthoringMode(boolean newValue) {

        var oldValue = (boolean) this.settings.authoringMode.getValue();
        if (!Intrinsics.areEqual(oldValue, newValue)) {
            this.settings.authoringMode.setValue(newValue);
            this.support.firePropertyChange("AuthoringMode", oldValue, newValue);
        }
    }

    @NotNull
    @Override
    public EditingMode getEditingMode() {

        return this.settings.editingMode.getValue();
    }

    @Override
    public void setEditingMode(EditingMode newValue) {

        var oldValue = (EditingMode) this.settings.editingMode.getValue();
        if (!Intrinsics.areEqual(oldValue, newValue)) {
            this.settings.editingMode.setValue(newValue);
            this.support.firePropertyChange("EditingMode", oldValue, newValue);
        }
    }

    @Override
    public boolean getViewCliLog() {

        return this.settings.viewCliLog.getValue();
    }

    @Override
    public void setViewCliLog(boolean newValue) {

        var oldValue = (boolean) this.settings.viewCliLog.getValue();
        if (!Intrinsics.areEqual(oldValue, newValue)) {
            this.settings.viewCliLog.setValue(newValue);
            this.support.firePropertyChange("ViewCliLog", oldValue, newValue);
        }
    }

    @Override
    public @NotNull CliInstallPolicy getCliInstallPolicy() {

        return this.settings.cliInstallPolicy.getValue();
    }

    @Override
    public void setCliInstallPolicy(CliInstallPolicy newValue) {

        var oldValue = (CliInstallPolicy) this.settings.cliInstallPolicy.getValue();
        if (!Intrinsics.areEqual(oldValue, newValue)) {
            this.settings.cliInstallPolicy.setValue(newValue);
            this.support.firePropertyChange("CliInstallPolicy", oldValue, newValue);
        }
    }

    @Override
    public boolean allowUsageCollection() {

        return this.settings.allowUsageCollection.getValue();
    }

    @Override
    public void setAllowUsageCollection(boolean newValue) {

        var oldValue = (boolean) this.settings.allowUsageCollection.getValue();
        if (!Intrinsics.areEqual(oldValue, newValue)) {
            this.settings.allowUsageCollection.setValue(newValue);
            this.support.firePropertyChange("AllowUsageCollection", oldValue, newValue);
        }
    }
}
