package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;

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

    public Automation(@NotNull String id, @NotNull String name) {

        this.id = id;
        this.name = name;
    }

    public String getName() {

        return this.name;
    }

    @Override
    public String toString() {

        String data = "";
        switch (this.type) {
            case CodeTemplateCommand:
                var onceOnly = this.isOneOff
                  ? String.format(", %s", AutomateBundle.message("general.Automation.CodeTemplateCommand.IsOneOff.True.Title"))
                  : String.format(", %s", AutomateBundle.message("general.Automation.CodeTemplateCommand.IsOneOff.False.Title"));
                data = String.format("%s: %s%s, path: %s",
                                     AutomateBundle.message("general.Automation.CodeTemplateCommand.Template.Title"),
                                     this.templateId, onceOnly, this.targetPath);
                break;
            case CliCommand:
                data = String.format("%s: %s %s: %s",
                                     AutomateBundle.message("general.Automation.CliCommand.ApplicationName.Title"),
                                     this.applicationName,
                                     AutomateBundle.message("general.Automation.CliCommand.Arguments.Title"),
                                     this.arguments);
                break;
            case CommandLaunchPoint:
                data = String.format("%s: %s",
                                     AutomateBundle.message("general.Automation.CommandLaunchPoint.CommandIds.Title"),
                                     String.join(";", this.cmdIds));
                break;
        }

        return String.format("%s (%s) (%s)", this.name, this.type, data);
    }

    public AutomateConstants.AutomationType getType() {

        return this.type;
    }
}
