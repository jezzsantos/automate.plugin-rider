package jezzsantos.automate.plugin.infrastructure.ui.settings;

import com.intellij.ide.ui.search.SearchableOptionContributor;
import com.intellij.ide.ui.search.SearchableOptionProcessor;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.serviceContainer.NonInjectable;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.services.interfaces.IProjectConfiguration;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.IContainer;
import jezzsantos.automate.plugin.infrastructure.IOsPlatform;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.util.Objects;

public class ProjectSettingsConfigurable implements SearchableConfigurable {

    public static final String ConfigurableId = "jezzsantos.automate.infrastructure.settings.ProjectSettingsConfigurable";
    public static final String ConfigurableDisplayName = AutomateBundle.message("settings.Title");
    private final Project project;
    @NotNull
    private final IProjectConfiguration configuration;
    private final IOsPlatform platform;
    private ProjectSettingsComponent settingsComponent;

    @UsedImplicitly
    public ProjectSettingsConfigurable(@NotNull Project project) {

        this(project, IProjectConfiguration.getInstance(project), IContainer.getOsPlatform());
    }

    @NonInjectable
    @TestOnly
    public ProjectSettingsConfigurable(@NotNull Project project, @NotNull IProjectConfiguration configuration, @NotNull IOsPlatform platform) {

        this.project = project;
        this.configuration = configuration;
        this.platform = platform;
    }

    @Nls
    @Override
    public String getDisplayName() {

        return ConfigurableDisplayName;
    }

    @Nullable
    @Override
    public JComponent createComponent() {

        this.settingsComponent = new ProjectSettingsComponent(this.project, this.platform);
        return this.settingsComponent.getPanel();
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {

        return this.settingsComponent.getPreferredFocusedComponent();
    }

    @Override
    public boolean isModified() {

        var modified = this.settingsComponent.getAuthoringMode() != this.configuration.getAuthoringMode();
        modified |= !Objects.equals(this.settingsComponent.getExecutablePath(), this.configuration.getExecutablePath());
        modified |= !Objects.equals(this.settingsComponent.getViewCliLog(), this.configuration.getViewCliLog());
        modified |= !Objects.equals(this.settingsComponent.getCliInstallPolicy(), this.configuration.getCliInstallPolicy());
        return modified;
    }

    @Override
    public void apply() {

        this.configuration.setAuthoringMode(this.settingsComponent.getAuthoringMode());
        this.configuration.setExecutablePath(this.settingsComponent.getExecutablePath());
        this.configuration.setViewCliLog(this.settingsComponent.getViewCliLog());
        this.configuration.setCliInstallPolicy(this.settingsComponent.getCliInstallPolicy());
    }

    @Override
    public void reset() {

        this.settingsComponent.setAuthoringMode(this.configuration.getAuthoringMode());
        this.settingsComponent.setExecutablePath(this.configuration.getExecutablePath());
        this.settingsComponent.setViewCliLog(this.configuration.getViewCliLog());
        this.settingsComponent.setCliInstallPolicy(this.configuration.getCliInstallPolicy());
    }

    @Override
    public void disposeUIResources() {

        this.settingsComponent = null;
    }

    @NotNull
    @Override
    public String getId() {

        return ConfigurableId;
    }

    public static class OptionContributor extends SearchableOptionContributor {

        @Override
        public void processOptions(@NotNull SearchableOptionProcessor processor) {

            addOptions(processor, "cli", AutomateBundle.message("settings.ViewCliLog.Label.Title"));
            addOptions(processor, "authoring", AutomateBundle.message("settings.AuthoringMode.Label.Message"));
        }

        @SuppressWarnings("SameParameterValue")
        private static void addOptions(@NotNull SearchableOptionProcessor processor, @NotNull String text, @Nullable String path, @Nullable String hit, @NotNull String configurableId, @Nullable String configurableDisplayName, boolean applyStemming) {

            processor.addOptions(text, path, hit, configurableId, configurableDisplayName, applyStemming);
        }

        @SuppressWarnings("SameParameterValue")
        private void addOptions(@NotNull SearchableOptionProcessor processor, @NotNull String text, @NotNull String hit) {

            addOptions(processor, text, null, hit, ProjectSettingsConfigurable.ConfigurableId,
                       ProjectSettingsConfigurable.ConfigurableDisplayName, true);
        }
    }
}
