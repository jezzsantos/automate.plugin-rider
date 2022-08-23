package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
public class Automation {

    @SerializedName(value = "Id")
    private String id;
    @SerializedName(value = "Name")
    private String name;
    @SerializedName(value = "Type")
    private AutomationType type;
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
                        ? ", onceonly"
                        : ", always";
                data = String.format("template: %s%s, path: %s", this.templateId, onceOnly, this.targetPath);
                break;
            case CliCommand:
                data = String.format("app: %s args: %s", this.applicationName, this.arguments);
                break;
            case CommandLaunchPoint:
                data = String.format("ids: %s", String.join(";", this.cmdIds));
                break;
        }

        return String.format("%s (%s) (%s)", this.name, this.type, data);
    }
}
