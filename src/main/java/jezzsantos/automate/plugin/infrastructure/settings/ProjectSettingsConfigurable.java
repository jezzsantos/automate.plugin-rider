package jezzsantos.automate.plugin.infrastructure.settings;

import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.jetbrains.rd.util.UsedImplicitly;
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

    @UsedImplicitly
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

        return this.settingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {

        this.settingsComponent = new ProjectSettingsComponent(this.project);
        return this.settingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {

        var modified = this.settingsComponent.getAuthoringMode() != this.configuration.getAuthoringMode();
        modified |= !Objects.equals(this.settingsComponent.getPathToAutomateExecutable(), this.configuration.getExecutablePath());
        modified |= !Objects.equals(this.settingsComponent.getViewCliLog(), this.configuration.getViewCliLog());
        return modified;
    }

    @Override
    public void apply() {

        this.configuration.setAuthoringMode(this.settingsComponent.getAuthoringMode());
        this.configuration.setExecutablePath(this.settingsComponent.getPathToAutomateExecutable());
        this.configuration.setViewCliLog(this.settingsComponent.getViewCliLog());
    }

    @Override
    public void reset() {

        this.settingsComponent.setAuthoringMode(this.configuration.getAuthoringMode());
        this.settingsComponent.setPathToAutomateExecutable(this.configuration.getExecutablePath());
        this.settingsComponent.setViewCliLog(this.configuration.getViewCliLog());
    }

    @Override
    public void disposeUIResources() {

        this.settingsComponent = null;
    }

    @Override
    public @NotNull
    @NonNls String getId() {

        return "jezzsantos.automate.infrastructure.settings.ProjectSettingsConfigurable";
    }
}
