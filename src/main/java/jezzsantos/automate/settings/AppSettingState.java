package jezzsantos.automate.settings;

import com.intellij.openapi.components.PersistentStateComponentWithModificationTracker;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.SimpleModificationTracker;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "jezzsantos.automate.settings.AppSettingsState",
        storages = @Storage("automate.xml")
)
public class AppSettingState implements PersistentStateComponentWithModificationTracker<AppSettingState> {
    private final SimpleModificationTracker tracker = new SimpleModificationTracker();

    public AppSettingState() {

    }


    @Override
    public @Nullable AppSettingState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    @Override
    public long getStateModificationCount() {
        return this.tracker.getModificationCount();
    }
}
