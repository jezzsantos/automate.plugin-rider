package jezzsantos.automate.plugin.infrastructure.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponentWithModificationTracker;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.SimpleModificationTracker;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.jetbrains.rd.util.UsedImplicitly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "jezzsantos.automate.infrastructure.settings.ApplicationSettingsState", storages = @Storage("automate.xml"))
public class ApplicationSettingsState implements PersistentStateComponentWithModificationTracker<ApplicationSettingsState> {

    private final SimpleModificationTracker tracker = new SimpleModificationTracker();

    @UsedImplicitly
    public ApplicationSettingsState() {

        registerAllPropertyToIncrementTrackerOnChanges(this);
    }

    @NotNull
    public static ApplicationSettingsState getInstance() {

        return ApplicationManager.getApplication().getService(ApplicationSettingsState.class);
    }

    @Nullable
    @Override
    public ApplicationSettingsState getState() {

        return this;
    }

    @Override
    public void loadState(@NotNull ApplicationSettingsState state) {

        XmlSerializerUtil.copyBean(state, this);
        registerAllPropertyToIncrementTrackerOnChanges(state);
    }

    @Override
    public long getStateModificationCount() {

        return this.tracker.getModificationCount();
    }

    @SuppressWarnings("unused")
    private void registerAllPropertyToIncrementTrackerOnChanges(@NotNull ApplicationSettingsState state) {

    }
}
