package jezzsantos.automate.plugin.common.recording;

import org.jetbrains.annotations.NotNull;

public class ReportingContext {

    private final String sessionId;
    private final String machineId;
    private boolean isAllowUsage;

    public ReportingContext(boolean allowUsage, @NotNull String machineId, @NotNull String sessionId) {

        this.isAllowUsage = allowUsage;
        this.machineId = machineId;
        this.sessionId = sessionId;
    }

    public boolean getAllowUsage() {return this.isAllowUsage;}

    public void setAllowUsage(boolean allowUsage) {

        this.isAllowUsage = allowUsage;
    }

    @NotNull
    public String getMachineId() {return this.machineId;}

    @NotNull
    public String getSessionId() {return this.sessionId;}
}
