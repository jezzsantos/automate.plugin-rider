package jezzsantos.automate.plugin.infrastructure.settings;

import com.intellij.openapi.project.Project;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.services.interfaces.IProjectConfiguration;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class PluginProjectConfiguration implements IProjectConfiguration {

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    @NotNull
    private final ProjectSettingsState settings;
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    @UsedImplicitly
    public PluginProjectConfiguration(@NotNull Project project) {

        this.settings = ProjectSettingsState.getInstance(project);
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
