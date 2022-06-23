package jezzsantos.automate;

import com.intellij.xdebugger.XDebugProcess;
import com.jetbrains.rider.debugger.DotNetDebugProcess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutomateSessionManager {
    @Nullable
    private static AutomateSessionManager instance;
    @NotNull
    private final TelemetryFactory telemetryFactory = new TelemetryFactory();

    @NotNull
    public static AutomateSessionManager getInstance() {
        if (instance == null)
            instance = new AutomateSessionManager();
        return instance;
    }

    private AutomateSessionManager() {
    }

    @Nullable
    public AutomateSession startSession(XDebugProcess debugProcess) {
        if (!(debugProcess instanceof DotNetDebugProcess))
            return null;

        AutomateSession session = new AutomateSession(
                telemetryFactory,
                (DotNetDebugProcess) debugProcess
        );
        session.startListeningToOutputDebugMessage();

        return session;
    }
}
