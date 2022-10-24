package jezzsantos.automate.plugin.application.interfaces;

import com.google.gson.annotations.SerializedName;
import com.jetbrains.rd.util.UsedImplicitly;

@SuppressWarnings("unused")
public class CliCollectUsage {

    @SerializedName(value = "IsEnabled")
    private boolean isEnabled;
    @SerializedName(value = "MachineId")
    private String machineId;
    @SerializedName(value = "RequestId")
    private String requestId;

    @UsedImplicitly
    public CliCollectUsage() {

    }
}
