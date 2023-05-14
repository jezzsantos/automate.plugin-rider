package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class Automation {

    @SerializedName(value = "Id")
    private String id;
    @SerializedName(value = "Name")
    private String name;
    @SerializedName(value = "Type")
    private AutomateConstants.AutomationType type;
    @SerializedName(value = "CodeTemplateId")
    private String codeTemplateId;
    @SerializedName(value = "IsOneOff")
    private boolean isOneOff;
    @SerializedName(value = "FilePath")
    private String filePath;
    @SerializedName(value = "ApplicationName")
    private String applicationName;
    @SerializedName(value = "Arguments")
    private String arguments;
    @SerializedName(value = "CommandIds")
    private List<String> cmdIds;

    @UsedImplicitly
    public Automation() {}

    public Automation(@NotNull String id, @NotNull String name) {

        this(id, name, AutomateConstants.AutomationType.CODE_TEMPLATE_COMMAND);
    }

    public Automation(@NotNull String id, @NotNull String name, @NotNull AutomateConstants.AutomationType type) {

        this.id = id;
        this.name = name;
        this.type = type;
    }

    @TestOnly
    public static Automation createCodeTemplateCommand(@NotNull String id, @NotNull String name, @NotNull String templateId, boolean isOneOff, @NotNull String filePath) {

        var automation = new Automation(id, name, AutomateConstants.AutomationType.CODE_TEMPLATE_COMMAND);
        automation.codeTemplateId = templateId;
        automation.isOneOff = isOneOff;
        automation.filePath = filePath;
        return automation;
    }

    @TestOnly
    public static Automation createCliCommand(@NotNull String id, @NotNull String name, @NotNull String applicationName, @Nullable String arguments) {

        var automation = new Automation(id, name, AutomateConstants.AutomationType.CLI_COMMAND);
        automation.applicationName = applicationName;
        automation.arguments = arguments;
        return automation;
    }

    @TestOnly
    public static Automation createLaunchPoint(@NotNull String id, @NotNull String name, @Nullable String commandIds) {

        var automation = new Automation(id, name, AutomateConstants.AutomationType.COMMAND_LAUNCH_POINT);
        automation.cmdIds = commandIds == null
          ? new ArrayList<>()
          : Arrays.stream(commandIds.split(";")).toList();
        return automation;
    }

    @Override
    public String toString() {

        var id = String.format("%s: %s", AutomateBundle.message("general.Automation.Id.Title"), this.id);
        String data = "";
        switch (this.type) {
            case CODE_TEMPLATE_COMMAND -> {
                var onceOnly = this.isOneOff
                  ? String.format(", %s", AutomateBundle.message("general.Automation.CodeTemplateCommand.IsOneOff.True.Title"))
                  : String.format(", %s", AutomateBundle.message("general.Automation.CodeTemplateCommand.IsOneOff.False.Title"));
                data = String.format("%s: %s%s, %s: %s",
                                     AutomateBundle.message("general.Automation.CodeTemplateCommand.Template.Title"),
                                     this.codeTemplateId, onceOnly,
                                     AutomateBundle.message("general.Automation.CodeTemplateCommand.TargetPath.Title"),
                                     this.filePath);
            }
            case CLI_COMMAND -> data = String.format("%s: %s, %s: %s",
                                                     AutomateBundle.message("general.Automation.CliCommand.ApplicationName.Title"),
                                                     this.applicationName,
                                                     AutomateBundle.message("general.Automation.CliCommand.Arguments.Title"),
                                                     this.arguments);
            case COMMAND_LAUNCH_POINT -> data = String.format("%s: %s",
                                                              AutomateBundle.message("general.Automation.CommandLaunchPoint.CommandIds.Title"),
                                                              this.cmdIds.isEmpty()
                                                                ? AutomateBundle.message("general.Automation.CommandLaunchPoint.CommandIds.None.Title")
                                                                : String.join(";", this.cmdIds));
        }

        return String.format("%s (%s, %s)", this.name, id, data);
    }

    @NotNull
    public String getId() {return this.id;}

    @NotNull
    public String getName() {return this.name;}

    @NotNull
    public AutomateConstants.AutomationType getType() {return this.type;}

    @Nullable
    public String getCodeTemplateId() {

        if (this.type != AutomateConstants.AutomationType.CODE_TEMPLATE_COMMAND) {
            return null;
        }

        return this.codeTemplateId;
    }

    @Nullable
    public String getFilePath() {

        if (this.type != AutomateConstants.AutomationType.CODE_TEMPLATE_COMMAND) {
            return null;
        }

        return this.filePath;
    }

    public boolean getIsOneOff() {

        if (this.type != AutomateConstants.AutomationType.CODE_TEMPLATE_COMMAND) {
            return false;
        }

        return this.isOneOff;
    }

    @Nullable
    public String getApplicationName() {

        if (this.type != AutomateConstants.AutomationType.CLI_COMMAND) {
            return null;
        }

        return this.applicationName;
    }

    @Nullable
    public String getArguments() {

        if (this.type != AutomateConstants.AutomationType.CLI_COMMAND) {
            return null;
        }

        return this.arguments;
    }

    public List<String> getCommandIdentifiers() {

        if (this.type != AutomateConstants.AutomationType.COMMAND_LAUNCH_POINT) {
            return null;
        }

        return this.cmdIds;
    }
}
