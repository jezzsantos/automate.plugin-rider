package jezzsantos.automate.plugin.infrastructure.settings;

import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.serviceContainer.NonInjectable;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.services.interfaces.IApplicationConfiguration;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.services.cli.IOsPlatform;
import jezzsantos.automate.plugin.infrastructure.services.cli.OsPlatform;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.util.Objects;

public class ApplicationSettingsConfigurable implements SearchableConfigurable {

    @NotNull
    private final IApplicationConfiguration configuration;
    private final IOsPlatform platform;
    private ApplicationSettingsComponent settingsComponent;

    @UsedImplicitly
    public ApplicationSettingsConfigurable() {

        this(IApplicationConfiguration.getInstance(), new OsPlatform());
    }

    @NonInjectable
    @TestOnly
    public ApplicationSettingsConfigurable(@NotNull IApplicationConfiguration configuration, @NotNull IOsPlatform platform) {

        this.configuration = configuration;
        this.platform = platform;
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

        this.settingsComponent = new ApplicationSettingsComponent(this.platform);
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

        return "jezzsantos.automate.infrastructure.settings.ApplicationSettingsConfigurable";
    }
}
