package jezzsantos.automate.plugin.infrastructure.settings;

import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.services.interfaces.IApplicationConfiguration;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PluginApplicationConfiguration implements IApplicationConfiguration {

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    @NotNull
    private final ApplicationSettingsState settings;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    @UsedImplicitly
    public PluginApplicationConfiguration() {

        this.settings = ApplicationSettingsState.getInstance();
    }

    @Override
    public void addListener(@NotNull PropertyChangeListener listener) {

        this.support.addPropertyChangeListener(listener);
    }

    @Override
    public void removeListener(@NotNull PropertyChangeListener listener) {

        this.support.removePropertyChangeListener(listener);
    }
}
