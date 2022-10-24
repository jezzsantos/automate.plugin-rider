package jezzsantos.automate.plugin.common.recording;

import org.jetbrains.annotations.NotNull;

public interface ISessionReporter {

    void enableReporting(@NotNull String machineId, @NotNull String sessionId);

    void measureStartSession();

    void measureEndSession(boolean success);

    void measureStartOperation(@NotNull String operationName);

    void measureEndOperation(@NotNull String operationName, boolean success);
}
