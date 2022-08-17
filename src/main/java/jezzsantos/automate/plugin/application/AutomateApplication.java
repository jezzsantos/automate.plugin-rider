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
    public List<PatternDefinition> listPatterns() {
        return this.automateService.listPatterns();
    }

    @NotNull
    @Override
    public List<ToolkitDefinition> listToolkits() {
        return this.automateService.listToolkits();
    }

    @NotNull
    @Override
    public List<DraftDefinition> listDrafts() {
        return this.automateService.listDrafts();
    }

    @NotNull
    @Override
    public PatternDefinition createPattern(@NotNull String name) throws Exception {
        return this.automateService.createPattern(name);
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
        return this.automateService.getCurrentPattern();
    }

    @Override
    public void setCurrentPattern(@NotNull String id) throws Exception {
        this.automateService.setCurrentPattern(id);
    }

    @Nullable
    @Override
    public DraftDefinition getCurrentDraft() {
        return this.automateService.getCurrentDraft();
    }

    @Override
    public void setCurrentDraft(@NotNull String id) throws Exception {
        this.automateService.setCurrentDraft(id);
    }

    @NotNull
    @Override
    public DraftDefinition createDraft(@NotNull String toolkitName, @NotNull String name) throws Exception {
        return this.automateService.createDraft(toolkitName, name);
    }

    @Override
    public void installToolkit(@NotNull String location) throws Exception {
        this.automateService.installToolkit(location);
    }

    @NotNull
    @Override
    public AllDefinitions listAllAutomation(boolean forceRefresh) {
        return this.automateService.listAllAutomation(forceRefresh);
    }

    @Override
    public void addPropertyChangedListener(@NotNull PropertyChangeListener listener) {
        this.automateService.addPropertyChangedListener(listener);
    }

    @Override
    public void removePropertyChangedListener(@NotNull PropertyChangeListener listener) {
        this.automateService.removePropertyChangedListener(listener);
    }

    @NotNull
    @Override
    public List<CliLogEntry> getCliLog() {
        return this.automateService.getCliLog();
    }
}
