package jezzsantos.automate.listeners;

import com.intellij.xdebugger.XDebugProcess;
import com.intellij.xdebugger.XDebuggerManagerListener;
import jezzsantos.automate.AutomateSessionManager;
import org.jetbrains.annotations.NotNull;

public class DebugMessageListener implements XDebuggerManagerListener {
    @Override
    public void processStarted(@NotNull XDebugProcess debugProcess) {
        AutomateSessionManager.getInstance().startSession(debugProcess);
    }
}
