package jezzsantos.automate.settings;

import com.intellij.openapi.components.PersistentStateComponentWithModificationTracker;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SimpleModificationTracker;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "jezzsantos.automate.settings.ProjectSettingsState",
        storages = @Storage("automate.xml")
)
public class ProjectSettingsState implements PersistentStateComponentWithModificationTracker<ProjectSettingsState> {
    private final SimpleModificationTracker tracker = new SimpleModificationTracker();

    public ProjectSettingsState() {
    }

    public static ProjectSettingsState getInstance(Project project) {
        return project.getService(ProjectSettingsState.class);
    }

    @Nullable
    @Override
    public ProjectSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ProjectSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    @Override
    public long getStateModificationCount() {
        return this.tracker.getModificationCount();
    }
}
