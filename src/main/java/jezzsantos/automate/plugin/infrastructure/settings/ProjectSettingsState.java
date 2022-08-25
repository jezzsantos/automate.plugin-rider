package jezzsantos.automate.plugin.infrastructure.settings;

import com.intellij.openapi.components.PersistentStateComponentWithModificationTracker;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SimpleModificationTracker;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import com.jetbrains.rd.util.UsedImplicitly;
import com.jetbrains.rd.util.lifetime.LifetimeDefinition;
import com.jetbrains.rd.util.reactive.Property;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.infrastructure.settings.converters.BooleanPropertyConverter;
import jezzsantos.automate.plugin.infrastructure.settings.converters.EditingModePropertyConverter;
import jezzsantos.automate.plugin.infrastructure.settings.converters.StringPropertyConverter;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "jezzsantos.automate.infrastructure.settings.ProjectSettingsState", storages = @Storage("automate.xml"))
public class ProjectSettingsState implements PersistentStateComponentWithModificationTracker<ProjectSettingsState> {

    @OptionTag(converter = BooleanPropertyConverter.class)
    public final Property<Boolean> authoringMode = new Property<>(false);
    @OptionTag(converter = EditingModePropertyConverter.class)
    public final Property<EditingMode> editingMode = new Property<>(EditingMode.Drafts);
    @OptionTag(converter = StringPropertyConverter.class)
    public final Property<String> pathToAutomateExecutable = new Property<>("");
    @OptionTag(converter = BooleanPropertyConverter.class)
    public final Property<Boolean> viewCliLog = new Property<>(false);
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

        incrementTrackerWhenPropertyChanges(state.authoringMode);
        incrementTrackerWhenPropertyChanges(state.editingMode);
        incrementTrackerWhenPropertyChanges(state.pathToAutomateExecutable);
        incrementTrackerWhenPropertyChanges(state.viewCliLog);
    }

    private <T> void incrementTrackerWhenPropertyChanges(Property<T> property) {

        property.advise(new LifetimeDefinition(), v -> {
            this.tracker.incModificationCount();
            return Unit.INSTANCE;
        });
    }
}
