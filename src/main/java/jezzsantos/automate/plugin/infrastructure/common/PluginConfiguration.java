package jezzsantos.automate.plugin.infrastructure.common;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.services.interfaces.IConfiguration;
import jezzsantos.automate.plugin.infrastructure.settings.ProjectSettingsState;
import org.jetbrains.annotations.NotNull;

public class PluginConfiguration implements IConfiguration {

    private final Project project;

    public PluginConfiguration(@NotNull Project project) {
        this.project = project;
    }

    @NotNull
    @Override
    public String getExecutablePath() {
        return ProjectSettingsState.getInstance(this.project).pathToAutomateExecutable.getValue();
    }

    @Override
    public Boolean getAuthoringMode() {
        return ProjectSettingsState.getInstance(this.project).authoringMode.getValue();
    }
}
