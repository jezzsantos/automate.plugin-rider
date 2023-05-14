package jezzsantos.automate.plugin.application;

import com.intellij.openapi.project.Project;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.drafts.*;
import jezzsantos.automate.plugin.application.interfaces.patterns.*;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitDetailed;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
import jezzsantos.automate.plugin.application.services.interfaces.IApplicationConfiguration;
import jezzsantos.automate.plugin.application.services.interfaces.IAutomateCliService;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AutomateApplication implements IAutomateApplication {

    @NotNull
    private final IAutomateCliService automateService;
    @NotNull
    private final IApplicationConfiguration configuration;
    @NotNull
    private final String currentDirectory;
    private final IRecorder recorder;

    private boolean isWarmedUp;

    @UsedImplicitly
    public AutomateApplication(@NotNull Project project) {

        this(IRecorder.getInstance(), IApplicationConfiguration.getInstance(), IAutomateCliService.getInstance(), Objects.requireNonNull(project.getBasePath()));
    }

    @TestOnly
    public AutomateApplication(@NotNull IRecorder recorder, @NotNull IApplicationConfiguration configuration, @NotNull IAutomateCliService automateService, @NotNull String currentDirectory) {

        this.recorder = recorder;
        this.configuration = configuration;
        this.automateService = automateService;
        this.currentDirectory = currentDirectory;
        this.isWarmedUp = false;
    }

    @NotNull
    @Override
    public String getExecutableName() {

        return this.automateService.getExecutableName();
    }

    @Override
    public boolean isCliInstalled() {

        return this.automateService.isCliInstalled(this.currentDirectory);
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

    @NotNull
    @Override
    public List<PatternLite> listPatterns() {

        return this.automateService.listPatterns(this.currentDirectory);
    }

    @NotNull
    @Override
    public List<ToolkitLite> listToolkits() {

        return this.automateService.listToolkits(this.currentDirectory);
    }

    @NotNull
    @Override
    public List<DraftLite> listDrafts() {

        return this.automateService.listDrafts(this.currentDirectory);
    }

    @Nullable
    @Override
    public ToolkitLite findToolkitById(@NotNull String id) {

        return this.automateService.findToolkitById(this.currentDirectory, id);
    }

    @Override
    public boolean isAuthoringMode() {

        return this.configuration.getAuthoringMode();
    }

    @Override
    public void setAuthoringMode(boolean on) {

        this.configuration.setAuthoringMode(on);
        this.configuration.setEditingMode(on
                                            ? EditingMode.PATTERNS
                                            : EditingMode.DRAFTS);
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

        return this.automateService.getCurrentPatternDetailed(this.currentDirectory);
    }

    @Nullable
    @Override
    public PatternLite getCurrentPatternInfo() {

        return this.automateService.getCurrentPatternInfo(this.currentDirectory);
    }

    @Override
    public void setCurrentPattern(@NotNull String id) throws Exception {

        this.automateService.setCurrentPattern(this.currentDirectory, id);
    }

    @NotNull
    @Override
    public ToolkitDetailed getCurrentToolkitDetailed() throws Exception {

        return this.automateService.getCurrentToolkitDetailed(this.currentDirectory);
    }

    @NotNull
    @Override
    public DraftDetailed getCurrentDraftDetailed() throws Exception {

        return this.automateService.getCurrentDraftDetailed(this.currentDirectory);
    }

    @Nullable
    @Override
    public DraftLite getCurrentDraftInfo() {

        return this.automateService.getCurrentDraftInfo(this.currentDirectory);
    }

    @Override
    public void setCurrentDraft(@NotNull String id) throws Exception {

        this.automateService.setCurrentDraft(this.currentDirectory, id);
    }

    @NotNull
    @Override
    public DraftLite createDraft(@NotNull String toolkitName, @NotNull String name) throws Exception {

        return this.automateService.createDraft(this.currentDirectory, toolkitName, name);
    }

    @Override
    public void installToolkit(@NotNull String location) throws Exception {

        this.automateService.installToolkit(this.currentDirectory, location);
    }

    @NotNull
    @Override
    public AllStateLite warmupAllAutomation() {

        if (this.isWarmedUp) {
            throw new RuntimeException(AutomateBundle.message("general.AutomateApplication.WarmUp.AlreadyWarmed.Message"));
        }

        this.isWarmedUp = true;
        return this.recorder.withOperation("populate",
                                           () -> listAllAutomation(false),
                                           AutomateBundle.message("trace.Operation.Populate.Start.Message"), AutomateBundle.message("trace.Operation.Populate.End.Message"));
    }

    @NotNull
    @Override
    public AllStateLite listAllAutomation(boolean forceRefresh) {

        if (forceRefresh) {
            this.automateService.refreshCliExecutableStatus();
            var isCliInstalled = this.automateService.isCliInstalled(this.currentDirectory);
            if (!isCliInstalled) {
                return new AllStateLite();
            }
        }
        return this.automateService.listAllAutomation(this.currentDirectory, forceRefresh);
    }

    @NotNull
    @Override
    public PatternLite createPattern(@NotNull String name) throws Exception {

        return this.automateService.createPattern(this.currentDirectory, name);
    }

    @NotNull
    @Override
    public PatternElement updatePattern(@Nullable String name, @Nullable String displayName, @Nullable String description) throws Exception {

        return this.automateService.updatePattern(this.currentDirectory, name, displayName, description);
    }

    @NotNull
    @Override
    public Attribute addPatternAttribute(@NotNull String parentEditPath, @NotNull String id, boolean isRequired, @NotNull AutomateConstants.AttributeDataType type, @Nullable String defaultValue, @Nullable List<String> choices) throws Exception {

        return this.automateService.addPatternAttribute(this.currentDirectory, parentEditPath, id, isRequired, type, defaultValue, choices);
    }

    @NotNull
    @Override
    public Attribute updatePatternAttribute(@NotNull String editPath, @NotNull String id, @Nullable String name, boolean isRequired, @NotNull AutomateConstants.AttributeDataType type, @Nullable String defaultValue, @Nullable List<String> choices) throws Exception {

        return this.automateService.updatePatternAttribute(this.currentDirectory, editPath, id, name, isRequired, type, defaultValue, choices);
    }

    @Override
    public void deletePatternAttribute(@NotNull String editPath, @NotNull String name) throws Exception {

        this.automateService.deletePatternAttribute(this.currentDirectory, editPath, name);
    }

    @NotNull
    @Override
    public PatternElement addPatternElement(@NotNull String parentEditPath, @NotNull String id, boolean isCollection, boolean isRequired, @Nullable String displayName, @Nullable String description, boolean isAutoCreate) throws Exception {

        return this.automateService.addPatternElement(this.currentDirectory, parentEditPath, id, isCollection, isRequired, displayName, description, isAutoCreate);
    }

    @NotNull
    @Override
    public PatternElement updatePatternElement(@NotNull String editPath, @NotNull String id, @Nullable String name, boolean isCollection, boolean isRequired, @Nullable String displayName, @Nullable String description, boolean isAutoCreate) throws Exception {

        return this.automateService.updatePatternElement(this.currentDirectory, editPath, id, name, isCollection, isRequired, displayName, description, isAutoCreate);
    }

    @Override
    public void deletePatternElement(@NotNull String editPath, @NotNull String name, boolean isCollection) throws Exception {

        this.automateService.deletePatternElement(this.currentDirectory, editPath, name, isCollection);
    }

    @NotNull
    @Override
    public CodeTemplate addPatternCodeTemplate(@NotNull String parentEditPath, @Nullable String name, @NotNull String filePath) throws Exception {

        return this.automateService.addPatternCodeTemplate(this.currentDirectory, parentEditPath, name, filePath);
    }

    @NotNull
    @Override
    public CodeTemplateWithCommand addPatternCodeTemplateWithCommand(@NotNull String parentEditPath, @Nullable String name, @NotNull String filePath, @Nullable String commandName, @NotNull String targetPath, boolean isOneOff) throws Exception {

        return this.automateService.addPatternCodeTemplateWithCommand(this.currentDirectory, parentEditPath, name, filePath, commandName, targetPath, isOneOff);
    }

    @Nullable
    @Override
    public String getPatternCodeTemplateContent(@NotNull String editPath, @NotNull String templateName) throws Exception {

        return this.automateService.getPatternCodeTemplateContent(this.currentDirectory, editPath, templateName);
    }

    @Override
    public void deletePatternCodeTemplate(@NotNull String editPath, @NotNull String templateName) throws Exception {

        this.automateService.deletePatternCodeTemplate(this.currentDirectory, editPath, templateName);
    }

    @NotNull
    @Override
    public Automation addPatternCodeTemplateCommand(@NotNull String parentEditPath, @Nullable String name, @NotNull String codeTemplateName, @NotNull String targetPath, boolean isOneOff) throws Exception {

        return this.automateService.addPatternCodeTemplateCommand(this.currentDirectory, parentEditPath, name, codeTemplateName, targetPath, isOneOff);
    }

    @NotNull
    @Override
    public Automation updatePatternCodeTemplateCommand(@NotNull String editPath, @NotNull String id, @NotNull String name, @NotNull String targetPath, boolean isOneOff) throws Exception {

        return this.automateService.updatePatternCodeTemplateCommand(this.currentDirectory, editPath, id, name, targetPath, isOneOff);
    }

    @NotNull
    @Override
    public Automation addPatternCliCommand(@NotNull String parentEditPath, @Nullable String name, @NotNull String applicationName, @Nullable String arguments) throws Exception {

        return this.automateService.addPatternCliCommand(this.currentDirectory, parentEditPath, name, applicationName, arguments);
    }

    @NotNull
    @Override
    public Automation updatePatternCliCommand(@NotNull String editPath, @NotNull String id, @NotNull String name, @NotNull String applicationName, @Nullable String arguments) throws Exception {

        return this.automateService.updatePatternCliCommand(this.currentDirectory, editPath, id, name, applicationName, arguments);
    }

    @Override
    public void deleteCommand(@NotNull String editPath, @NotNull String commandName) throws Exception {

        this.automateService.deleteCommand(this.currentDirectory, editPath, commandName);
    }

    @NotNull
    @Override
    public Automation addPatternCommandLaunchPoint(@NotNull String parentEditPath, @Nullable String name, @NotNull List<String> commandIdentifiers, @Nullable String from) throws Exception {

        return this.automateService.addPatternCommandLaunchPoint(this.currentDirectory, parentEditPath, name, commandIdentifiers, from);
    }

    @NotNull
    @Override
    public Automation updatePatternCommandLaunchPoint(@NotNull String editPath, @NotNull String id, @NotNull String name, @NotNull List<String> addIdentifiers, @NotNull List<String> removeIdentifiers, @Nullable String from) throws Exception {

        return this.automateService.updatePatternCommandLaunchPoint(this.currentDirectory, editPath, id, name, addIdentifiers, removeIdentifiers, from);
    }

    @Override
    public void deleteLaunchPoint(@NotNull String editPath, @NotNull String launchPointName) throws Exception {

        this.automateService.deleteLaunchPoint(this.currentDirectory, editPath, launchPointName);
    }

    @Nullable
    @Override
    public String publishCurrentPattern(boolean installLocally, @Nullable String version) throws Exception {

        return this.automateService.publishCurrentPattern(this.currentDirectory, installLocally, version);
    }

    @NotNull
    @Override
    public DraftElement addDraftElement(@NotNull String parentConfigurePath, boolean isCollection, @NotNull String elementName, @NotNull Map<String, String> nameValuePairs) throws Exception {

        return this.automateService.addDraftElement(this.currentDirectory, parentConfigurePath, isCollection, elementName, nameValuePairs);
    }

    @NotNull
    @Override
    public DraftElement updateDraftElement(@NotNull String configurationPath, @NotNull Map<String, String> nameValuePairs) throws Exception {

        return this.automateService.updateDraftElement(this.currentDirectory, configurationPath, nameValuePairs);
    }

    @Override
    public void deleteDraftElement(@NotNull String expression) throws Exception {

        this.automateService.deleteDraftElement(this.currentDirectory, expression);
    }

    @NotNull
    @Override
    public DraftUpgradeReport upgradeCurrentDraft(boolean force) throws Exception {

        return this.automateService.upgradeCurrentDraft(this.currentDirectory, force);
    }

    @Override
    public void deleteCurrentDraft() throws Exception {

        this.automateService.deleteCurrentDraft(this.currentDirectory);
    }

    @NotNull
    @Override
    public LaunchPointExecutionResult executeLaunchPoint(@NotNull String configurationPath, @NotNull String launchPointName) throws Exception {

        return this.automateService.executeLaunchPoint(this.currentDirectory, configurationPath, launchPointName);
    }
}
