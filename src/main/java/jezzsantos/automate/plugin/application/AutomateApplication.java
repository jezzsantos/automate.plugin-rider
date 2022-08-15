package jezzsantos.automate.plugin.application;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.interfaces.*;
import jezzsantos.automate.plugin.application.services.interfaces.IAutomateService;
import jezzsantos.automate.plugin.application.services.interfaces.IConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.util.List;

public class AutomateApplication implements IAutomateApplication {
    @NotNull
    private final IAutomateService automateService;
    @NotNull
    private final IConfiguration configuration;

    public AutomateApplication(@NotNull Project project) {
        this.configuration = project.getService(IConfiguration.class);
        this.automateService = project.getService(IAutomateService.class);
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
    public String tryGetExecutableVersion(@NotNull String executablePath) {
        return this.automateService.tryGetExecutableVersion(executablePath);
    }

    @NotNull
    @Override
    public List<PatternDefinition> getPatterns() {
        var executablePath = this.configuration.getExecutablePath();
        return this.automateService.getPatterns(executablePath);
    }

    @NotNull
    @Override
    public List<ToolkitDefinition> getToolkits() {
        var executablePath = this.configuration.getExecutablePath();
        return this.automateService.getToolkits(executablePath);
    }

    @NotNull
    @Override
    public List<DraftDefinition> getDrafts() {
        var executablePath = this.configuration.getExecutablePath();
        return this.automateService.getDrafts(executablePath);
    }

    @NotNull
    @Override
    public PatternDefinition createPattern(@NotNull String name) throws Exception {
        var executablePath = this.configuration.getExecutablePath();
        return this.automateService.createPattern(executablePath, name);
    }

    @Override
    public boolean isAuthoringMode() {
        return this.configuration.getAuthoringMode();
    }

    @Override
    public void setAuthoringMode(boolean on) {

        this.configuration.setAuthoringMode(on);
        this.configuration.setEditingMode(on ? EditingMode.Patterns : EditingMode.Drafts);
    }

    @Override
    public EditingMode getEditingMode() {
        return this.configuration.getEditingMode();
    }

    @Override
    public void setEditingMode(@NotNull EditingMode mode) {
        this.configuration.setEditingMode(mode);
    }

    @Nullable
    @Override
    public PatternDefinition getCurrentPattern() {
        var executablePath = this.configuration.getExecutablePath();
        return this.automateService.getCurrentPattern(executablePath);
    }

    @Override
    public void setCurrentPattern(@NotNull String id) throws Exception {
        var executablePath = this.configuration.getExecutablePath();
        this.automateService.setCurrentPattern(executablePath, id);
    }

    @Nullable
    @Override
    public DraftDefinition getCurrentDraft() {
        var executablePath = this.configuration.getExecutablePath();
        return this.automateService.getCurrentDraft(executablePath);
    }

    @Override
    public void setCurrentDraft(@NotNull String id) throws Exception {
        var executablePath = this.configuration.getExecutablePath();
        this.automateService.setCurrentDraft(executablePath, id);
    }

    @NotNull
    @Override
    public DraftDefinition createDraft(@NotNull String toolkitName, @NotNull String name) throws Exception {
        var executablePath = this.configuration.getExecutablePath();
        return this.automateService.createDraft(executablePath, toolkitName, name);
    }

    @Override
    public void installToolkit(@NotNull String location) throws Exception {
        var executablePath = this.configuration.getExecutablePath();
        this.automateService.installToolkit(executablePath, location);
    }

    @NotNull
    @Override
    public AllDefinitions getAllAutomation() {
        return getAllAutomation(false);
    }


    @NotNull
    @Override
    public AllDefinitions getAllAutomation(boolean forceRefresh) {
        var executablePath = this.configuration.getExecutablePath();
        return this.automateService.getAllAutomation(executablePath, forceRefresh);
    }

    @Override
    public boolean getViewCliLog() {
        return this.configuration.getViewCliLog();
    }

    @Override
    public void addCliLogListener(@NotNull PropertyChangeListener listener) {
        this.automateService.addCliLogListener(listener);
    }

    @NotNull
    @Override
    public List<CliLogEntry> getCliLog() {
        return this.automateService.getCliLog();
    }
}
