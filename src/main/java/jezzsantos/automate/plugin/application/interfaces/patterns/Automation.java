package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.List;

@SuppressWarnings("unused")
public class Automation {

    @SerializedName(value = "Id")
    private String id;
    @SerializedName(value = "Name")
    private String name;
    @SerializedName(value = "Type")
    private AutomateConstants.AutomationType type;
    @SerializedName(value = "TemplateId")
    private String templateId;
    @SerializedName(value = "IsOneOff")
    private boolean isOneOff;
    @SerializedName(value = "TargetPath")
    private String targetPath;
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
    public static Automation createCodeTemplateCommand(@NotNull String id, @NotNull String name, @NotNull String templateId, boolean isOneOff, @NotNull String targetPath) {

        var automation = new Automation(id, name, AutomateConstants.AutomationType.CODE_TEMPLATE_COMMAND);
        automation.templateId = templateId;
        automation.isOneOff = isOneOff;
        automation.targetPath = targetPath;
        return automation;
    }

    @TestOnly
    public static Automation createCliCommand(@NotNull String id, @NotNull String name, @NotNull String applicationName, @NotNull String arguments) {

        var automation = new Automation(id, name, AutomateConstants.AutomationType.CLI_COMMAND);
        automation.applicationName = applicationName;
        automation.arguments = arguments;
        return automation;
    }

    @TestOnly
    public static Automation createLaunchPoint(@NotNull String id, @NotNull String name, @NotNull List<String> commandIds) {

        var automation = new Automation(id, name, AutomateConstants.AutomationType.COMMAND_LAUNCH_POINT);
        automation.cmdIds = commandIds;
        return automation;
    }

    @NotNull
    public String getName() {

        return this.name;
    }

    @NotNull
    public AutomateConstants.AutomationType getType() {

        return this.type;
    }

    @Override
    public String toString() {

        String data = "";
        switch (this.type) {
            case CODE_TEMPLATE_COMMAND -> {
                var onceOnly = this.isOneOff
                  ? String.format(", %s", AutomateBundle.message("general.Automation.CodeTemplateCommand.IsOneOff.True.Title"))
                  : String.format(", %s", AutomateBundle.message("general.Automation.CodeTemplateCommand.IsOneOff.False.Title"));
                data = String.format("%s: %s%s, path: %s",
                                     AutomateBundle.message("general.Automation.CodeTemplateCommand.Template.Title"),
                                     this.templateId, onceOnly, this.targetPath);
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

        return String.format("%s (%s) (%s)", this.name, this.type.getDisplayName(), data);
    }
}
