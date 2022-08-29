package jezzsantos.automate.plugin.application;

import com.intellij.openapi.project.Project;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftDetailed;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternDetailed;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
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

    @UsedImplicitly
    public AutomateApplication(@NotNull Project project) {

        this.configuration = project.getService(IConfiguration.class);
        this.automateService = project.getService(IAutomateService.class);
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
    public List<PatternLite> listPatterns() {

        return this.automateService.listPatterns();
    }

    @NotNull
    @Override
    public List<ToolkitLite> listToolkits() {

        return this.automateService.listToolkits();
    }

    @NotNull
    @Override
    public List<DraftLite> listDrafts() {

        return this.automateService.listDrafts();
    }

    @NotNull
    @Override
    public PatternLite createPattern(@NotNull String name) throws Exception {

        return this.automateService.createPattern(name);
    }

    @Override
    public boolean isAuthoringMode() {

        return this.configuration.getAuthoringMode();
    }

    @Override
    public void setAuthoringMode(boolean on) {

        this.configuration.setAuthoringMode(on);
        this.configuration.setEditingMode(on
                                            ? EditingMode.Patterns
                                            : EditingMode.Drafts);
    }

    @Override
    public EditingMode getEditingMode() {

        return this.configuration.getEditingMode();
    }

    @Override
    public void setEditingMode(@NotNull EditingMode mode) {

        this.configuration.setEditingMode(mode);
    }

    @NotNull
    @Override
    public PatternDetailed getCurrentPatternDetailed() throws Exception {

        return this.automateService.getCurrentPatternDetailed();
    }

    @Nullable
    @Override
    public PatternLite getCurrentPatternInfo() {

        return this.automateService.getCurrentPatternInfo();
    }

    @Override
    public void setCurrentPattern(@NotNull String id) throws Exception {

        this.automateService.setCurrentPattern(id);
    }

    @NotNull
    @Override
    public DraftDetailed getCurrentDraftDetailed() throws Exception {

        return this.automateService.getCurrentDraftDetailed();
    }

    @Nullable
    @Override
    public DraftLite getCurrentDraftInfo() {

        return this.automateService.getCurrentDraftInfo();
    }

    @Override
    public void setCurrentDraft(@NotNull String id) throws Exception {

        this.automateService.setCurrentDraft(id);
    }

    @NotNull
    @Override
    public DraftLite createDraft(@NotNull String toolkitName, @NotNull String name) throws Exception {

        return this.automateService.createDraft(toolkitName, name);
    }

    @Override
    public void installToolkit(@NotNull String location) throws Exception {

        this.automateService.installToolkit(location);
    }

    @NotNull
    @Override
    public AllStateLite refreshLocalState() {

        return this.automateService.listAllAutomation(true);
    }

    @Override
    public void addPropertyListener(@NotNull PropertyChangeListener listener) {

        this.automateService.addPropertyChangedListener(listener);
    }

    @Override
    public void removePropertyListener(@NotNull PropertyChangeListener listener) {

        this.automateService.removePropertyChangedListener(listener);
    }

    @Override
    public void addConfigurationListener(@NotNull PropertyChangeListener listener) {

        this.configuration.addListener(listener);
    }

    @Override
    public void removeConfigurationListener(@NotNull PropertyChangeListener listener) {

        this.configuration.removeListener(listener);
    }

    @NotNull
    @Override
    public List<CliLogEntry> getCliLogEntries() {

        return this.automateService.getCliLog();
    }

    public boolean getViewCliLog() {

        return this.configuration.getViewCliLog();
    }

    @Override
    public Attribute addPatternAttribute(@NotNull String editPath, @NotNull String name, boolean isRequired, @NotNull String type, @Nullable String defaultValue, @Nullable List<String> choices) throws Exception {

        return this.automateService.addPatternAttribute(editPath, name, isRequired, type, defaultValue, choices);
    }

    @Override
    public void deletePatternAttribute(@NotNull String editPath, @NotNull String name) throws Exception {

        this.automateService.deletePatternAttribute(editPath, name);
    }

    @Override
    public void deleteDraftElement(@NotNull String expression) throws Exception {

        this.automateService.deleteDraftElement(expression);
    }
}
