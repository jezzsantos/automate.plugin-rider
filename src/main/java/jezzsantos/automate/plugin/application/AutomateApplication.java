package jezzsantos.automate.plugin.application;

import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;
import jezzsantos.automate.plugin.application.interfaces.PatternDefinition;
import jezzsantos.automate.plugin.application.interfaces.ToolkitDefinition;
import jezzsantos.automate.plugin.application.services.interfaces.IAutomateService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AutomateApplication implements IAutomateApplication {

    private final IAutomateService automateService;

    public AutomateApplication(@NotNull IAutomateService automateService) {

        this.automateService = automateService;
    }

    @NotNull
    @Override
    public String getExecutableName() {
        return this.automateService.getExecutableName();
    }

    @NotNull
    @Override
    public String getDefaultInstallLocation() {
        return this.automateService.getDefaultInstallLocation();
    }

    @Nullable
    @Override
    public String tryGetExecutableVersion(@Nullable String executablePath) {
        return this.automateService.tryGetExecutableVersion(executablePath);
    }

    @NotNull
    @Override
    public List<PatternDefinition> getPatterns(@Nullable String executablePath) {
        return this.automateService.getPatterns(executablePath);
    }

    @NotNull
    @Override
    public List<ToolkitDefinition> getToolkits(@Nullable String executablePath) {
        return this.automateService.getToolkits(executablePath);
    }

    @NotNull
    @Override
    public List<DraftDefinition> getDrafts(@Nullable String executablePath) {
        return this.automateService.getDrafts(executablePath);
    }
}
