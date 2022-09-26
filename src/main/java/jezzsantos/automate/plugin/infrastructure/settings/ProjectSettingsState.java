package jezzsantos.automate.plugin.infrastructure.settings;

import com.intellij.openapi.components.PersistentStateComponentWithModificationTracker;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SimpleModificationTracker;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.jetbrains.rd.util.UsedImplicitly;
import com.jetbrains.rd.util.lifetime.LifetimeDefinition;
import com.jetbrains.rd.util.reactive.Property;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
@State(name = "jezzsantos.automate.infrastructure.settings.ProjectSettingsState", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class ProjectSettingsState implements PersistentStateComponentWithModificationTracker<ProjectSettingsState> {

    private final SimpleModificationTracker tracker = new SimpleModificationTracker();

    @UsedImplicitly
    public ProjectSettingsState() {

        registerAllPropertyToIncrementTrackerOnChanges(this);
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
        registerAllPropertyToIncrementTrackerOnChanges(state);
    }

    @Override
    public long getStateModificationCount() {

        return this.tracker.getModificationCount();
    }

    private void registerAllPropertyToIncrementTrackerOnChanges(@NotNull ProjectSettingsState state) {

    }

    private <T> void incrementTrackerWhenPropertyChanges(Property<T> property) {

        property.advise(new LifetimeDefinition(), v -> {
            this.tracker.incModificationCount();
            return Unit.INSTANCE;
        });
    }
}
