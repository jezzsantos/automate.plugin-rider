package jezzsantos.automate.plugin.application.services.interfaces;

import com.intellij.openapi.application.ApplicationManager;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeListener;

@SuppressWarnings("unused")
public interface IApplicationConfiguration {

    static IApplicationConfiguration getInstance() {

        return ApplicationManager.getApplication().getService(IApplicationConfiguration.class);
    }

    void addListener(@NotNull PropertyChangeListener listener);

    void removeListener(@NotNull PropertyChangeListener listener);
}
