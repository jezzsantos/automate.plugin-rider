package jezzsantos.automate.plugin.infrastructure.settings;

import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
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
    private final ProjectSettingsState settings;
    private ProjectSettingsComponent settingsComponent;

    public ProjectSettingsConfigurable(@NotNull Project project) {

        this.project = project;
        this.settings = ProjectSettingsState.getInstance(project);
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
        var modified = settingsComponent.getAuthoringMode() != settings.authoringMode.getValue();
        modified |= !Objects.equals(settingsComponent.getPathToAutomateExecutable(), settings.pathToAutomateExecutable.getValue());
        modified |= !Objects.equals(settingsComponent.getViewCliLog(), settings.viewCliLog.getValue());
        return modified;
    }

    @Override
    public void apply() {
        settings.authoringMode.setValue(settingsComponent.getAuthoringMode());
        settings.pathToAutomateExecutable.setValue(settingsComponent.getPathToAutomateExecutable());
        settings.viewCliLog.setValue(settingsComponent.getViewCliLog());
    }

    @Override
    public void reset() {
        settingsComponent.setAuthoringMode(settings.authoringMode.getValue());
        settingsComponent.setPathToAutomateExecutable(settings.pathToAutomateExecutable.getValue());
        settingsComponent.setViewCliLog(settings.viewCliLog.getValue());
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
