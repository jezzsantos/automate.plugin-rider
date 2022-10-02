package jezzsantos.automate.plugin.application.interfaces.drafts;

import com.google.gson.annotations.SerializedName;
import jezzsantos.automate.core.AutomateConstants;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DraftUpgradeReportItem {

    @SerializedName(value = "Type")
    public AutomateConstants.UpgradeLogType type;
    @SerializedName(value = "MessageTemplate")
    public String messageTemplate;
    @SerializedName(value = "Arguments")
    public List<Object> arguments;

    public DraftUpgradeReportItem(@NotNull AutomateConstants.UpgradeLogType type, @NotNull String messageTemplate, @NotNull List<Object> arguments) {

        this.type = type;
        this.messageTemplate = messageTemplate;
        this.arguments = arguments;
    }

    @NotNull
    public AutomateConstants.UpgradeLogType getType() {return this.type;}

    @NotNull
    public String getMessage() {

        return String.format(this.messageTemplate, this.arguments);
    }
}
