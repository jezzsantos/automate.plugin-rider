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
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitDetailed;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
import jezzsantos.automate.plugin.application.services.interfaces.CliExecutableStatus;
import jezzsantos.automate.plugin.application.services.interfaces.IAutomateService;
import jezzsantos.automate.plugin.application.services.interfaces.IConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.beans.PropertyChangeListener;
import java.util.List;

public class AutomateApplication implements IAutomateApplication {

    @NotNull
    private final IAutomateService automateService;
    @NotNull
    private final IConfiguration configuration;

    @UsedImplicitly
    public AutomateApplication(@NotNull Project project) {

        this(project.getService(IConfiguration.class), project.getService(IAutomateService.class));
    }

    @TestOnly
    public AutomateApplication(@NotNull IConfiguration configuration, @NotNull IAutomateService automateService) {

        this.configuration = configuration;
        this.automateService = automateService;
    }

    @NotNull
    @Override
    public String getExecutableName() {

        return this.automateService.getExecutableName();
    }

    @NotNull
    @Override
    public String getDefaultExecutableLocation() {

        return this.automateService.getDefaultExecutableLocation();
    }

    @NotNull
    @Override
    public CliExecutableStatus tryGetExecutableStatus(@NotNull String executablePath) {

        return this.automateService.tryGetExecutableStatus(executablePath);
    }

    @Override
    public boolean isCliInstalled() {

        return this.automateService.isCliInstalled();
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
    public ToolkitDetailed getCurrentToolkitDetailed() throws Exception {

        return this.automateService.getCurrentToolkitDetailed();
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
    public AllStateLite listAllAutomation(boolean forceRefresh) {

        if (forceRefresh) {
            this.automateService.refreshCliExecutableStatus();
            var isCliInstalled = this.automateService.isCliInstalled();
            if (!isCliInstalled) {
                return new AllStateLite();
            }
        }
        return this.automateService.listAllAutomation(forceRefresh);
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
