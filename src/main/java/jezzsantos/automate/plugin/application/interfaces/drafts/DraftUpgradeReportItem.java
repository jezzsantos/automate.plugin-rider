package jezzsantos.automate.plugin.application.interfaces.drafts;

import com.google.gson.annotations.SerializedName;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.core.AutomateConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.Map;

import static jezzsantos.automate.plugin.common.StringExtensions.formatStructured;

public class DraftUpgradeReportItem {

    @SerializedName(value = "Type")
    public AutomateConstants.UpgradeLogType type;
    @SerializedName(value = "MessageTemplate")
    public String messageTemplate;
    @SerializedName(value = "Arguments")
    public Map<String, Object> arguments;

    @UsedImplicitly
    public DraftUpgradeReportItem() {}

    @TestOnly
    public DraftUpgradeReportItem(@NotNull AutomateConstants.UpgradeLogType type, @NotNull String messageTemplate, @NotNull Map<String, Object> arguments) {

        this.type = type;
        this.messageTemplate = messageTemplate;
        this.arguments = arguments;
    }

    @NotNull
    public AutomateConstants.UpgradeLogType getType() {return this.type;}

    @NotNull
    public String getMessage() {

        return formatStructured(this.messageTemplate, this.arguments);
    }
}
