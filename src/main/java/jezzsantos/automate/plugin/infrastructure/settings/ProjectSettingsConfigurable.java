package jezzsantos.automate.plugin.infrastructure.settings;

import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.services.interfaces.IConfiguration;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Objects;

public class ProjectSettingsConfigurable implements SearchableConfigurable {
    @NotNull
    private final Project project;
    @NotNull
    private final IConfiguration configuration;
    private ProjectSettingsComponent settingsComponent;

    public ProjectSettingsConfigurable(@NotNull Project project) {

        this.project = project;
        this.configuration = IConfiguration.getInstance(project);
    }

    @SuppressWarnings("DialogTitleCapitalization")
    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return AutomateBundle.message("settings.Title");
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return settingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        settingsComponent = new ProjectSettingsComponent(this.project);
        return settingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        var modified = settingsComponent.getAuthoringMode() != configuration.getAuthoringMode();
        modified |= !Objects.equals(settingsComponent.getPathToAutomateExecutable(), configuration.getExecutablePath());
        modified |= !Objects.equals(settingsComponent.getViewCliLog(), configuration.getViewCliLog());
        return modified;
    }

    @Override
    public void apply() {
        configuration.setAuthoringMode(settingsComponent.getAuthoringMode());
        configuration.setExecutablePath(settingsComponent.getPathToAutomateExecutable());
        configuration.setViewCliLog(settingsComponent.getViewCliLog());
    }

    @Override
    public void reset() {
        settingsComponent.setAuthoringMode(configuration.getAuthoringMode());
        settingsComponent.setPathToAutomateExecutable(configuration.getExecutablePath());
        settingsComponent.setViewCliLog(configuration.getViewCliLog());
    }

    @Override
    public void disposeUIResources() {
        settingsComponent = null;
    }

    @Override
    public @NotNull
    @NonNls String getId() {
        return "jezzsantos.automate.infrastructure.settings.ProjectSettingsConfigurable";
    }
}
