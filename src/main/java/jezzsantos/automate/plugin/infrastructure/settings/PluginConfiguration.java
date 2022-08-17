package jezzsantos.automate.plugin.infrastructure.settings;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.services.interfaces.IConfiguration;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class PluginConfiguration implements IConfiguration {
    @NotNull
    private final ProjectSettingsState settings;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);


    public PluginConfiguration(@NotNull Project project) {

        this.settings = ProjectSettingsState.getInstance(project);
    }

    @NotNull
    @Override
    public String getExecutablePath() {
        return Objects.requireNonNullElse(this.settings.pathToAutomateExecutable.getValue(), "");
    }

    @Override
    public void setExecutablePath(@NotNull String path) {
        var oldValue = settings.pathToAutomateExecutable.getValue();
        if (!Intrinsics.areEqual(oldValue, path)) {
            this.settings.pathToAutomateExecutable.setValue(path);
            support.firePropertyChange("ExecutablePath", oldValue, path);
        }
    }

    @Override
    public boolean getAuthoringMode() {
        return this.settings.authoringMode.getValue();
    }

    @Override
    public void setAuthoringMode(boolean on) {
        var oldValue = (boolean) settings.authoringMode.getValue();
        if (!Intrinsics.areEqual(oldValue, on)) {
            this.settings.authoringMode.setValue(on);
            support.firePropertyChange("AuthoringMode", oldValue, on);
        }
    }

    @Override
    public EditingMode getEditingMode() {
        return this.settings.editingMode.getValue();
    }

    @Override
    public void setEditingMode(EditingMode mode) {
        var oldValue = this.settings.editingMode.getValue();
        if (!Intrinsics.areEqual(oldValue, mode)) {
            this.settings.editingMode.setValue(mode);
            support.firePropertyChange("EditingMode", oldValue, mode);
        }
    }

    @Override
    public boolean getViewCliLog() {
        return this.settings.viewCliLog.getValue();
    }

    @Override
    public void setViewCliLog(boolean view) {
        var oldValue = (boolean) settings.viewCliLog.getValue();
        if (!Intrinsics.areEqual(oldValue, view)) {
            this.settings.viewCliLog.setValue(view);
            support.firePropertyChange("ViewCliLog", oldValue, view);
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
