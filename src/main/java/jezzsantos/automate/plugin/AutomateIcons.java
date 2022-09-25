package jezzsantos.automate.plugin;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

@SuppressWarnings("unused")
public interface AutomateIcons {

    Icon StatusAborted = IconLoader.getIcon("/icons/StatusAborted.svg", AutomateIcons.class);
    Icon StatusError = IconLoader.getIcon("/icons/StatusError.svg", AutomateIcons.class);
    Icon StatusFailed = IconLoader.getIcon("/icons/StatusFailed.svg", AutomateIcons.class);
    Icon StatusSuccess = IconLoader.getIcon("/icons/StatusSuccess.svg", AutomateIcons.class);
    Icon StatusWarning = IconLoader.getIcon("/icons/StatusWarning.svg", AutomateIcons.class);
}
