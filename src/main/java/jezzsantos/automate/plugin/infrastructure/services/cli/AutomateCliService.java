package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.intellij.openapi.project.Project;
import com.intellij.serviceContainer.NonInjectable;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.CliInstallPolicy;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntryType;
import jezzsantos.automate.plugin.application.interfaces.drafts.*;
import jezzsantos.automate.plugin.application.interfaces.patterns.*;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitDetailed;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
import jezzsantos.automate.plugin.application.services.interfaces.CliExecutableStatus;
import jezzsantos.automate.plugin.application.services.interfaces.CliVersionCompatibility;
import jezzsantos.automate.plugin.application.services.interfaces.IAutomateCliService;
import jezzsantos.automate.plugin.application.services.interfaces.IProjectConfiguration;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.IContainer;
import jezzsantos.automate.plugin.common.StringWithDefault;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.IOsPlatform;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.CliStructuredResult;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.GetInfoStructuredOutput;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.ListAllDefinitionsStructuredOutput;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutput;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.drafts.*;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.patterns.*;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.toolkits.GetToolkitStructuredOutput;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.toolkits.InstallToolkitStructuredOutput;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.toolkits.ListToolkitsStructuredOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AutomateCliService implements IAutomateCliService {

    @NotNull
    private final ICliResponseCache cache;
    @NotNull
    private final IRecorder recorder;
    @NotNull
    private final IProjectConfiguration configuration;
    @NotNull
    private final IOsPlatform platform;

    @NotNull
    private final IAutomateCliRunner cliRunner;
    private final ICliUpgrader upgrader;

    @UsedImplicitly
    public AutomateCliService(@NotNull Project project) {

        this(IRecorder.getInstance(), IProjectConfiguration.getInstance(project), new InMemCliResponseCache(), IContainer.getOsPlatform());
    }

    @NonInjectable
    private AutomateCliService(@NotNull IRecorder recorder, @NotNull IProjectConfiguration configuration, @NotNull ICliResponseCache cache, @NotNull IOsPlatform platform) {

        this(recorder, configuration, cache, platform, new AutomateCliRunner());
    }

    @NonInjectable
    private AutomateCliService(@NotNull IRecorder recorder, @NotNull IProjectConfiguration configuration, @NotNull ICliResponseCache cache, @NotNull IOsPlatform platform, @NotNull IAutomateCliRunner runner) {

        this(recorder, configuration, cache, platform, runner, new AutomateCliUpgrader(recorder, runner, IContainer.getNotifier()));
    }

    @NonInjectable
    public AutomateCliService(@NotNull IRecorder recorder, @NotNull IProjectConfiguration configuration, @NotNull ICliResponseCache cache, @NotNull IOsPlatform platform, @NotNull IAutomateCliRunner runner, @NotNull ICliUpgrader upgrader) {

        this.recorder = recorder;
        this.configuration = configuration;
        this.cache = cache;
        this.platform = platform;
        this.cliRunner = runner;
        this.upgrader = upgrader;

        this.configuration.addListener(e -> {

            recorder.measureEvent("config.app.changed", Map.of(
              "Property Name", e.getPropertyName()
            ));
            if (e.getPropertyName().equalsIgnoreCase("ExecutablePath")) {
                var path = (StringWithDefault) e.getNewValue();
                logChangeInExecutablePath(path);
            }
        });

        init();
    }

    @NotNull
    public static String getExecutableName(@NotNull IOsPlatform platform) {

        return (platform.getIsWindowsOs()
          ? String.format("%s.exe", AutomateConstants.ExecutableName)
          : AutomateConstants.ExecutableName);
    }

    @NotNull
    public static String getDefaultExecutableLocation(@NotNull IOsPlatform platform) {

        return Paths.get(platform.getDotNetToolsDirectory()).resolve(getExecutableName(platform)).toString();
    }

    @NotNull
    @Override
    public String getExecutableName() {

        return getExecutableName(this.platform);
    }

    @NotNull
    @Override
    public String getDefaultExecutableLocation() {

        return getDefaultExecutableLocation(this.platform);
    }

    @NotNull
    @Override
    public CliExecutableStatus tryGetExecutableStatus(@NotNull String currentDirectory, @NotNull StringWithDefault executablePath) {

        var location = executablePath.getValueOrDefault();
        var file = new File(location);
        var executableName = getExecutableName();

        if (!file.isFile()) {
            return new CliExecutableStatus(executableName);
        }

        if (!file.getName().equalsIgnoreCase(executableName)) {
            return new CliExecutableStatus(executableName);
        }

        var reportingContext = this.recorder.getReportingContext();
        var context = new ExecutionContext(currentDirectory, executablePath, reportingContext.getAllowUsage(), reportingContext.getSessionId());
        var result = this.cliRunner.executeStructured(GetInfoStructuredOutput.class, context, new ArrayList<>(List.of("@info")));
        if (result.isError()) {
            return new CliExecutableStatus(executableName);
        }

        return new CliExecutableStatus(executableName, result.getOutput().getVersion());
    }

    @Override
    public boolean isCliInstalled(@NotNull String currentDirectory) {

        return this.cache.isCliInstalled(() -> {
            var executableStatus = tryGetExecutableStatus(currentDirectory, this.configuration.getExecutablePath());
            return executableStatus.getCompatibility() == CliVersionCompatibility.COMPATIBLE;
        });
    }

    @Override
    public boolean isAnyToolkitsInstalled() {

        return !this.cache.listToolkits(ArrayList::new).isEmpty();
    }

    @Override
    public void refreshCliExecutableStatus() {

        this.cache.invalidateIsCliInstalled();
    }

    @NotNull
    @Override
    public List<CliLogEntry> getCliLog() {

        return this.cliRunner.getLogs();
    }

    @Override
    public void addPropertyChangedListener(@NotNull PropertyChangeListener listener) {

        this.cliRunner.addLogListener(listener);
    }

    @Override
    public void removePropertyChangedListener(@NotNull PropertyChangeListener listener) {

        this.cliRunner.removeLogListener(listener);
    }

    @NotNull
    @Override
    public AllStateLite listAllAutomation(@NotNull String currentDirectory, boolean forceRefresh) {

        return this.cache.listAll(() -> {
            var result = runAutomateForStructuredOutput(ListAllDefinitionsStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("@list", "@all")));
            if (result.isError()) {
                return new AllStateLite();
            }
            else {
                return result.getOutput().getAll();
            }
        }, forceRefresh);
    }

    @NotNull
    @Override
    public List<PatternLite> listPatterns(@NotNull String currentDirectory) {

        return this.cache.listPatterns(() -> {
            var result = runAutomateForStructuredOutput(ListPatternsStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("@list", "@patterns")));
            if (result.isError()) {
                return new ArrayList<>();
            }
            else {
                var patterns = result.getOutput().getPatterns();
                return patterns != null
                  ? patterns
                  : new ArrayList<>();
            }
        });
    }

    @NotNull
    @Override
    public List<ToolkitLite> listToolkits(@NotNull String currentDirectory) {

        return this.cache.listToolkits(() -> {
            var result = runAutomateForStructuredOutput(ListToolkitsStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("@list", "@toolkits")));
            if (result.isError()) {
                return new ArrayList<>();
            }
            else {
                var toolkits = result.getOutput().getToolkits();
                return toolkits != null
                  ? toolkits
                  : new ArrayList<>();
            }
        });
    }

    @NotNull
    @Override
    public List<DraftLite> listDrafts(@NotNull String currentDirectory) {

        return this.cache.listDrafts(() -> {
            var result = runAutomateForStructuredOutput(ListDraftsStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("@list", "@drafts")));
            if (result.isError()) {
                return new ArrayList<>();
            }
            else {
                var drafts = result.getOutput().getDrafts();
                return drafts != null
                  ? drafts
                  : new ArrayList<>();
            }
        });
    }

    @Nullable
    @Override
    public ToolkitLite findToolkitById(@NotNull String currentDirectory, @NotNull String id) {

        var toolkits = listToolkits(currentDirectory);
        return toolkits.stream()
          .filter(toolkit -> toolkit.getId().equals(id))
          .findFirst()
          .orElse(null);
    }

    @NotNull
    @Override
    public PatternLite createPattern(@NotNull String currentDirectory, @NotNull String name) throws Exception {

        var result = runAutomateForStructuredOutput(CreatePatternStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("@create", "@pattern", name)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateAllPatterns();
            return result.getOutput().getPattern();
        }
    }

    @NotNull
    @Override
    public PatternElement updatePattern(@NotNull String currentDirectory, @Nullable String name, @Nullable String displayName, @Nullable String description) throws Exception {

        var args = new ArrayList<>(
          List.of("@edit", "@update-pattern"));
        if (name != null) {
            args.addAll(List.of("--name", name));
        }
        if (displayName != null && !displayName.isEmpty()) {
            args.addAll(List.of("--displayedas", displayName));
        }
        if (description != null && !description.isEmpty()) {
            args.addAll(List.of("--describedas", description));
        }
        var result = runAutomateForStructuredOutput(GetUpdatePatternStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateAllPatterns();
            return result.getOutput().getPattern().getPattern();
        }
    }

    @Nullable
    @Override
    public PatternLite getCurrentPatternInfo(@NotNull String currentDirectory) {

        return this.cache.getPatternInfo(() -> {
            var patterns = listPatterns(currentDirectory);

            return patterns.stream()
              .filter(PatternLite::getIsCurrent)
              .findFirst()
              .orElse(null);
        });
    }

    @NotNull
    @Override
    public PatternDetailed getCurrentPatternDetailed(@NotNull String currentDirectory) throws Exception {

        return this.cache.getPatternDetailed(() -> {
            var result = runAutomateForStructuredOutput(GetUpdatePatternStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("@view", "@pattern", "--all")));
            if (result.isError()) {
                throw new Exception(result.getError().getErrorMessage());
            }
            else {
                return result.getOutput().getPattern();
            }
        });
    }

    @Override
    public void setCurrentPattern(@NotNull String currentDirectory, @NotNull String id) throws Exception {

        var result = runAutomateForStructuredOutput(SwitchPatternStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("@edit", "@switch", id)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateAllPatterns();
            result.getOutput().getPattern();
        }
    }

    @Nullable
    @Override
    public String publishCurrentPattern(@NotNull String currentDirectory, boolean installLocally, @Nullable String version) throws Exception {

        var args = new ArrayList<>(List.of("@publish", "@toolkit"));
        if (installLocally) {
            args.add("--install");
        }
        if (version != null) {
            args.add("--asversion");
            args.add(version);
        }
        var result = runAutomateForStructuredOutput(PublishToolkitStructuredOutput.class, currentDirectory, new ArrayList<>(args));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateAllPatterns();
            this.cache.invalidateAllToolkits();
            return result.getOutput().getWarning();
        }
    }

    @NotNull
    @Override
    public Attribute addPatternAttribute(@NotNull String currentDirectory, @NotNull String parentEditPath, @NotNull String id, boolean isRequired, @NotNull AutomateConstants.AttributeDataType type, @Nullable String defaultValue, @Nullable List<String> choices) throws Exception {

        var args = new ArrayList<>(
          List.of("@edit", "@add-attribute", id, "--isrequired", Boolean.toString(isRequired), "--isoftype", type.getValue(), "--aschildof", parentEditPath));
        if (defaultValue != null && !defaultValue.isEmpty()) {
            args.addAll(List.of("--defaultvalueis", defaultValue));
        }
        if (choices != null && !choices.isEmpty()) {
            args.addAll(List.of("--isoneof", String.join(";", choices)));
        }
        var result = runAutomateForStructuredOutput(AddRemovePatternAttributeStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
            return result.getOutput().getAttribute();
        }
    }

    @NotNull
    @Override
    public Attribute updatePatternAttribute(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String id, @Nullable String name, boolean isRequired, @NotNull AutomateConstants.AttributeDataType type, @Nullable String defaultValue, @Nullable List<String> choices) throws Exception {

        var args = new ArrayList<>(
          List.of("@edit", "@update-attribute", id, "--isrequired", Boolean.toString(isRequired), "--isoftype", type.getValue(), "--aschildof", editPath));
        if (name != null) {
            args.addAll(List.of("--name", name));
        }
        if (defaultValue != null && !defaultValue.isEmpty()) {
            args.addAll(List.of("--defaultvalueis", defaultValue));
        }
        if (choices != null && !choices.isEmpty()) {
            args.addAll(List.of("--isoneof", String.join(";", choices)));
        }
        var result = runAutomateForStructuredOutput(AddRemovePatternAttributeStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
            return result.getOutput().getAttribute();
        }
    }

    @Override
    public void deletePatternAttribute(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String name) throws Exception {

        var result = runAutomateForStructuredOutput(AddRemovePatternAttributeStructuredOutput.class, currentDirectory,
                                                    new ArrayList<>(List.of("@edit", "@delete-attribute", name, "--aschildof", editPath)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
        }
    }

    @NotNull
    @Override
    public PatternElement addPatternElement(@NotNull String currentDirectory, @NotNull String parentEditPath, @NotNull String id, boolean isCollection, boolean isRequired, @Nullable String displayName, @Nullable String description, boolean isAutoCreate) throws Exception {

        var args = new ArrayList<>(
          List.of("@edit", isCollection
            ? "@add-collection"
            : "@add-element", id, "--isrequired", Boolean.toString(isRequired), "--autocreate", Boolean.toString(isAutoCreate), "--aschildof", parentEditPath));
        if (displayName != null && !displayName.isEmpty()) {
            args.addAll(List.of("--displayedas", displayName));
        }
        if (description != null && !description.isEmpty()) {
            args.addAll(List.of("--describedas", description));
        }
        var result = runAutomateForStructuredOutput(AddRemovePatternElementStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
            return result.getOutput().getElement();
        }
    }

    @NotNull
    @Override
    public PatternElement updatePatternElement(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String id, @Nullable String name, boolean isCollection, boolean isRequired, @Nullable String displayName, @Nullable String description, boolean isAutoCreate) throws Exception {

        var args = new ArrayList<>(
          List.of("@edit", isCollection
            ? "@update-collection"
            : "@update-element", id, "--isrequired", Boolean.toString(isRequired), "--autocreate", Boolean.toString(isAutoCreate), "--aschildof", editPath));
        if (name != null) {
            args.addAll(List.of("--name", name));
        }
        if (displayName != null && !displayName.isEmpty()) {
            args.addAll(List.of("--displayedas", displayName));
        }
        if (description != null && !description.isEmpty()) {
            args.addAll(List.of("--describedas", description));
        }
        var result = runAutomateForStructuredOutput(AddRemovePatternElementStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
            return result.getOutput().getElement();
        }
    }

    @Override
    public void deletePatternElement(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String name, boolean isCollection) throws Exception {

        var result = runAutomateForStructuredOutput(AddRemovePatternElementStructuredOutput.class, currentDirectory,
                                                    new ArrayList<>(List.of("@edit", isCollection
                                                      ? "@delete-collection"
                                                      : "@delete-element", name, "--aschildof", editPath)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
        }
    }

    @NotNull
    @Override
    public CodeTemplate addPatternCodeTemplate(@NotNull String currentDirectory, @NotNull String parentEditPath, @Nullable String name, @NotNull String filePath) throws Exception {

        var args = new ArrayList<>(
          List.of("@edit", "@add-codetemplate", filePath, "--aschildof", parentEditPath));
        if (name != null && !name.isEmpty()) {
            args.addAll(List.of("--name", name));
        }
        var result = runAutomateForStructuredOutput(AddRemovePatternCodeTemplateStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
            var codeTemplate = result.getOutput().getCodeTemplate();
            this.cache.setPatternCodeTemplateContent(parentEditPath, codeTemplate.getName(), Objects.requireNonNull(codeTemplate.getEditorPath()));
            return codeTemplate;
        }
    }

    @NotNull
    @Override
    public CodeTemplateWithCommand addPatternCodeTemplateWithCommand(@NotNull String currentDirectory, @NotNull String parentEditPath, @Nullable String name, @NotNull String filePath, @Nullable String commandName, @NotNull String targetPath, boolean isOneOff) throws Exception {

        var args = new ArrayList<>(
          List.of("@edit", "@add-codetemplate-with-command", filePath, "--targetpath", targetPath, "--aschildof", parentEditPath));
        if (isOneOff) {
            args.add("--isoneoff");
        }
        if (name != null && !name.isEmpty()) {
            args.addAll(List.of("--name", name));
        }
        if (commandName != null && !commandName.isEmpty()) {
            args.addAll(List.of("--commandname", commandName));
        }
        var result = runAutomateForStructuredOutput(AddRemovePatternCodeTemplateWithCommandStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
            var codeTemplateAndCommand = result.getOutput().getCodeTemplateWithCommand();
            var codeTemplate = codeTemplateAndCommand.getCodeTemplate();
            this.cache.setPatternCodeTemplateContent(parentEditPath, codeTemplate.getName(), Objects.requireNonNull(codeTemplate.getEditorPath()));
            return codeTemplateAndCommand;
        }
    }

    @Nullable
    @Override
    public String getPatternCodeTemplateContent(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String templateName) throws Exception {

        return this.cache.getPatternCodeTemplateContent(editPath, templateName, () -> {
            var args = new ArrayList<>(
              List.of("@view", "@codetemplate", templateName, "--aschildof", editPath));
            var result = runAutomateForStructuredOutput(AddRemovePatternCodeTemplateStructuredOutput.class, currentDirectory, args);
            if (result.isError()) {
                throw new Exception(result.getError().getErrorMessage());
            }
            else {
                return result.getOutput().getCodeTemplate().getEditorPath();
            }
        });
    }

    @Override
    public void deletePatternCodeTemplate(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String templateName) throws Exception {

        var result = runAutomateForStructuredOutput(AddRemovePatternCodeTemplateStructuredOutput.class, currentDirectory,
                                                    new ArrayList<>(List.of("@edit", "@delete-codetemplate", templateName, "--aschildof", editPath)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidatePatternCodeTemplateContent(editPath, templateName);
            this.cache.invalidateCurrentPattern();
        }
    }

    @NotNull
    @Override
    public Automation addPatternCodeTemplateCommand(@NotNull String currentDirectory, @NotNull String parentEditPath, @Nullable String name, @NotNull String codeTemplateName, @NotNull String targetPath, boolean isOneOff) throws Exception {

        var args = new ArrayList<>(
          List.of("@edit", "@add-codetemplate-command", codeTemplateName, "--targetpath", targetPath, "--aschildof", parentEditPath));
        if (isOneOff) {
            args.add("--isoneoff");
        }
        if (name != null && !name.isEmpty()) {
            args.addAll(List.of("--name", name));
        }
        var result = runAutomateForStructuredOutput(AddRemovePatternAutomationStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
            return result.getOutput().getCodeTemplateCommand();
        }
    }

    @NotNull
    @Override
    public Automation updatePatternCodeTemplateCommand(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String id, @NotNull String name, @NotNull String targetPath, boolean isOneOff) throws Exception {

        var args = new ArrayList<>(
          List.of("@edit", "@update-codetemplate-command", id, "--targetpath", targetPath, "--name", name, "--aschildof", editPath));
        if (isOneOff) {
            args.add("--isoneoff");
        }
        var result = runAutomateForStructuredOutput(AddRemovePatternAutomationStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
            return result.getOutput().getCodeTemplateCommand();
        }
    }

    @Override
    public @NotNull Automation addPatternCliCommand(@NotNull String currentDirectory, @NotNull String parentEditPath, @Nullable String name, @NotNull String applicationName, @Nullable String arguments) throws Exception {

        var args = new ArrayList<>(
          List.of("@edit", "@add-cli-command", applicationName, "--aschildof", parentEditPath));
        if (arguments != null && !arguments.isEmpty()) {
            args.addAll(List.of("--arguments", arguments));
        }
        if (name != null && !name.isEmpty()) {
            args.addAll(List.of("--name", name));
        }
        var result = runAutomateForStructuredOutput(AddRemovePatternAutomationStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
            return result.getOutput().getCliCommand();
        }
    }

    @Override
    public @NotNull Automation updatePatternCliCommand(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String id, @NotNull String name, @NotNull String applicationName, @Nullable String arguments) throws Exception {

        var args = new ArrayList<>(
          List.of("@edit", "@update-cli-command", id, "--app", applicationName, "--name", name, "--aschildof", editPath));
        if (arguments != null && !arguments.isEmpty()) {
            args.addAll(List.of("--arguments", arguments));
        }
        var result = runAutomateForStructuredOutput(AddRemovePatternAutomationStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
            return result.getOutput().getCliCommand();
        }
    }

    @Override
    public void deleteCommand(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String commandName) throws Exception {

        var result = runAutomateForStructuredOutput(AddRemovePatternAutomationStructuredOutput.class, currentDirectory,
                                                    new ArrayList<>(List.of("@edit", "@delete-command", commandName, "--aschildof", editPath)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
        }
    }

    @NotNull
    @Override
    public Automation addPatternCommandLaunchPoint(@NotNull String currentDirectory, @NotNull String parentEditPath, @Nullable String name, @NotNull List<String> commandIdentifiers, @Nullable String from) throws Exception {

        var identifiersToAdd = String.join(";", commandIdentifiers);
        var args = new ArrayList<>(
          List.of("@edit", "@add-command-launchpoint", identifiersToAdd, "--aschildof", parentEditPath));
        if (from != null && !from.isEmpty()) {
            args.addAll(List.of("--from", from));
        }
        if (name != null && !name.isEmpty()) {
            args.addAll(List.of("--name", name));
        }
        var result = runAutomateForStructuredOutput(AddRemovePatternAutomationStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
            return result.getOutput().getLaunchPoint();
        }
    }

    @NotNull
    @Override
    public Automation updatePatternCommandLaunchPoint(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String id, @NotNull String name, @NotNull List<String> addIdentifiers, @NotNull List<String> removeIdentifiers, @Nullable String from) throws Exception {

        var args = new ArrayList<>(
          List.of("@edit", "@update-command-launchpoint", id, "--name", name, "--aschildof", editPath));
        if (!addIdentifiers.isEmpty()) {
            var identifiersToAdd = String.join(";", addIdentifiers);
            args.addAll(List.of("--add", identifiersToAdd));
        }
        if (!removeIdentifiers.isEmpty()) {
            var identifiersToRemove = String.join(";", removeIdentifiers);
            args.addAll(List.of("--remove", identifiersToRemove));
        }
        if (from != null && !from.isEmpty()) {
            args.addAll(List.of("--from", from));
        }
        var result = runAutomateForStructuredOutput(AddRemovePatternAutomationStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
            return result.getOutput().getLaunchPoint();
        }
    }

    @Override
    public void deleteLaunchPoint(@NotNull String currentDirectory, @NotNull String editPath, @NotNull String launchPointName) throws Exception {

        var result = runAutomateForStructuredOutput(AddRemovePatternAutomationStructuredOutput.class, currentDirectory,
                                                    new ArrayList<>(List.of("@edit", "@delete-command-launchpoint", launchPointName, "--aschildof", editPath)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentPattern();
        }
    }

    @Override
    public void installToolkit(@NotNull String currentDirectory, @NotNull String location) throws Exception {

        var result = runAutomateForStructuredOutput(InstallToolkitStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("@install", "@toolkit", location)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        this.cache.invalidateAllToolkits();
    }

    @NotNull
    @Override
    public ToolkitDetailed getCurrentToolkitDetailed(@NotNull String currentDirectory) throws Exception {

        return this.cache.getToolkitDetailed(() -> {
            var result = runAutomateForStructuredOutput(GetToolkitStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("@view", "@toolkit")));
            if (result.isError()) {
                throw new Exception(result.getError().getErrorMessage());
            }
            else {
                return result.getOutput().getToolkit();
            }
        });
    }

    @NotNull
    @Override
    public DraftLite createDraft(@NotNull String currentDirectory, @NotNull String toolkitName, @NotNull String name) throws Exception {

        var result = runAutomateForStructuredOutput(CreateDraftStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("@run", "@toolkit", toolkitName, "--name", name)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateAllDrafts();
            return result.getOutput().getDraft();
        }
    }

    @Nullable
    @Override
    public DraftLite getCurrentDraftInfo(@NotNull String currentDirectory) {

        return this.cache.getDraftInfo(() -> {
            var drafts = listDrafts(currentDirectory);

            return drafts.stream()
              .filter(DraftLite::getIsCurrent)
              .findFirst()
              .orElse(null);
        });
    }

    @NotNull
    @Override
    public DraftDetailed getCurrentDraftDetailed(@NotNull String currentDirectory) throws Exception {

        return this.cache.getDraftDetailed(() -> {
            var incompatibleDraft = getInCompatibleDraftSafely(currentDirectory);
            if (incompatibleDraft != null) {
                return incompatibleDraft;
            }

            var result = runAutomateForStructuredOutput(GetDraftStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("@view", "@draft")));
            if (result.isError()) {
                throw new Exception(result.getError().getErrorMessage());
            }
            else {
                return result.getOutput().getDraft();
            }
        });
    }

    @Override
    public void setCurrentDraft(@NotNull String currentDirectory, @NotNull String id) throws Exception {

        var result = runAutomateForStructuredOutput(SwitchDraftStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("@run", "@switch", id)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateAllDrafts();
            this.cache.invalidateCurrentToolkit();
            result.getOutput().getDraft();
        }
    }

    @NotNull
    @Override
    public DraftElement addDraftElement(@NotNull String currentDirectory, @NotNull String parentConfigurePath, boolean isCollection, @NotNull String elementName, @NotNull Map<String, String> nameValuePairs) throws Exception {

        var configurePath = extendConfigurePath(parentConfigurePath, elementName);
        var args = new ArrayList<>(List.of("@configure", isCollection
          ? "@add-one-to"
          : "@add", configurePath));
        if (!nameValuePairs.isEmpty()) {
            nameValuePairs
              .forEach((key, value) -> {
                  args.add("--and-set");
                  args.add(String.format("%s=%s", key, value));
              });
        }
        var result = runAutomateForStructuredOutput(AddRemoveDraftElementStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentDraft();
            return result.getOutput().getElement();
        }
    }

    @NotNull
    @Override
    public DraftElement updateDraftElement(@NotNull String currentDirectory, @NotNull String configurationPath, @NotNull Map<String, String> nameValuePairs) throws Exception {

        var args = new ArrayList<>(List.of("@configure", "@on", configurationPath));
        if (!nameValuePairs.isEmpty()) {
            nameValuePairs
              .forEach((key, value) -> {
                  args.add("--and-set");
                  args.add(String.format("%s=%s", key, value));
              });
        }
        var result = runAutomateForStructuredOutput(AddRemoveDraftElementStructuredOutput.class, currentDirectory, args);
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentDraft();
            return result.getOutput().getElement();
        }
    }

    @Override
    public void deleteDraftElement(@NotNull String currentDirectory, @NotNull String expression) throws Exception {

        var result = runAutomateForStructuredOutput(AddRemoveDraftElementStructuredOutput.class, currentDirectory, new ArrayList<>(List.of("@configure", "@delete", expression)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateCurrentDraft();
        }
    }

    @NotNull
    @Override
    public DraftUpgradeReport upgradeCurrentDraft(@NotNull String currentDirectory, boolean force) throws Exception {

        var args = new ArrayList<>(List.of("@upgrade", "@draft"));
        if (force) {
            args.add("--force");
        }
        var result = runAutomateForStructuredOutput(UpgradeDraftStructuredOutput.class, currentDirectory, new ArrayList<>(args));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateAllDrafts();
            return result.getOutput().getReport();
        }
    }

    @Override
    public void deleteCurrentDraft(String currentDirectory) throws Exception {

        var result = runAutomateForStructuredOutput(DeleteDraftStructuredOutput.class, currentDirectory, List.of("@delete", "@draft"));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            this.cache.invalidateAllDrafts();
            this.cache.invalidateCurrentToolkit();
        }
    }

    @Override
    @NotNull
    public LaunchPointExecutionResult executeLaunchPoint(@NotNull String currentDirectory, @NotNull String configurationPath, @NotNull String launchPointName) throws Exception {

        var result = runAutomateForStructuredOutput(ExecuteLaunchPointStructuredOutput.class, currentDirectory,
                                                    new ArrayList<>(List.of("@execute", "@command", launchPointName, "--on", configurationPath)));
        if (result.isError()) {
            throw new Exception(result.getError().getErrorMessage());
        }
        else {
            return result.getOutput().getResult();
        }
    }

    @Nullable
    private DraftDetailed getInCompatibleDraftSafely(@NotNull String currentDirectory) {

        var draftInfo = getCurrentDraftInfo(currentDirectory);
        if (draftInfo != null) {
            if (draftInfo.isIncompatible()) {
                return DraftDetailed.createIncompatible(draftInfo.getId(), draftInfo.getName(), draftInfo.getToolkitId(), draftInfo.getToolkitName(), draftInfo.getVersion());
            }
        }

        return null;
    }

    private void init() {

        var executablePath = this.configuration.getExecutablePath();
        var installPolicy = this.configuration.getCliInstallPolicy();
        var executableName = this.getExecutableName();

        var executableStatus = upgradeRuntime(executablePath, installPolicy, executableName);
        logChangeInExecutablePath(executableStatus, executablePath);
    }

    @NotNull
    private CliExecutableStatus upgradeRuntime(StringWithDefault executablePath, CliInstallPolicy installPolicy, String executableName) {

        return this.recorder.withOperation("auto-upgrade", () -> {
                                               var status = refreshExecutableStatus(executablePath);
                                               if (status.getCompatibility() != CliVersionCompatibility.COMPATIBLE) {
                                                   status = this.upgrader.upgrade(executablePath, executableName, status, installPolicy);
                                                   saveStatusIfSupported(status);
                                               }

                                               return status;
                                           },
                                           AutomateBundle.message("trace.Operation.UpgradeCli.Start.Message"),
                                           AutomateBundle.message("trace.Operation.UpgradeCli.End.Message"));
    }

    @NotNull
    private <TResult extends StructuredOutput<?>> CliStructuredResult<TResult> runAutomateForStructuredOutput(@NotNull Class<TResult> outputClass, @NotNull String currentDirectory, @NotNull List<String> args) {

        if (!isCliInstalled(currentDirectory)) {
            throw new RuntimeException(AutomateBundle.message("exception.AutomateCliService.CliNotInstalled.Message"));
        }

        var reportingContext = this.recorder.getReportingContext();
        var executablePath = this.configuration.getExecutablePath();
        var context = new ExecutionContext(currentDirectory, executablePath, reportingContext.getAllowUsage(), reportingContext.getSessionId());
        return this.cliRunner.executeStructured(outputClass, context, args);
    }

    @NotNull
    private String extendConfigurePath(@NotNull String parentConfigurePath, String elementName) {

        var stringToInsert = String.format(".%s", elementName);
        var insertionIndex = parentConfigurePath.length() - 1;

        var buffer = new StringBuilder(parentConfigurePath);
        buffer.insert(insertionIndex, stringToInsert);
        return buffer.toString();
    }

    private void logChangeInExecutablePath(@NotNull StringWithDefault executablePath) {

        var executableStatus = refreshExecutableStatus(executablePath);

        logChangeInExecutablePath(executableStatus, executablePath);
    }

    private void logChangeInExecutablePath(@NotNull CliExecutableStatus executableStatus, @NotNull StringWithDefault executablePath) {

        String message;
        CliLogEntryType type;
        var path = executablePath.getValueOrDefault();
        switch (executableStatus.getCompatibility()) {
            case INCOMPATIBLE -> {
                message = AutomateBundle.message("general.AutomateCliService.ExecutablePathChanged.UnSupported.Message", path,
                                                 executableStatus.getMinCompatibleVersion());
                type = CliLogEntryType.ERROR;
            }
            case COMPATIBLE -> {
                message = AutomateBundle.message("general.AutomateCliService.ExecutablePathChanged.Supported.Message", path);
                type = CliLogEntryType.NORMAL;
            }
            default -> {
                message = AutomateBundle.message("general.AutomateCliService.ExecutablePathChanged.Unknown.Message", path);
                type = CliLogEntryType.ERROR;
            }
        }

        var entry = new CliLogEntry(message, type);
        this.cliRunner.log(entry);
    }

    @NotNull
    private CliExecutableStatus refreshExecutableStatus(@NotNull StringWithDefault executablePath) {

        var currentDirectory = this.platform.getDotNetToolsDirectory();
        var executableStatus = tryGetExecutableStatus(currentDirectory, executablePath);
        saveStatusIfSupported(executableStatus);

        return executableStatus;
    }

    private void saveStatusIfSupported(CliExecutableStatus executableStatus) {

        this.cache.setIsCliInstalled(executableStatus.getCompatibility() == CliVersionCompatibility.COMPATIBLE);
    }
}
