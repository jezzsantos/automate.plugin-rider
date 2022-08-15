package jezzsantos.automate.plugin.infrastructure.common;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.services.interfaces.IConfiguration;
import jezzsantos.automate.plugin.infrastructure.settings.ProjectSettingsState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PluginConfiguration implements IConfiguration {
    @NotNull
    private final ProjectSettingsState settings;

    public PluginConfiguration(@NotNull Project project) {

        this.settings = ProjectSettingsState.getInstance(project);
    }

    @NotNull
    @Override
    public String getExecutablePath() {
        return Objects.requireNonNullElse(this.settings.pathToAutomateExecutable.getValue(), "");
    }

    @Override
    public Boolean getAuthoringMode() {
        return this.settings.authoringMode.getValue();
    }

    @Override
    public void setAuthoringMode(boolean on) {
        this.settings.authoringMode.setValue(on);
    }

    @Override
    public EditingMode getEditingMode() {
        return this.settings.editingMode.getValue();
    }

    @Override
    public void setEditingMode(EditingMode mode) {
        this.settings.editingMode.setValue(mode);
    }
}
