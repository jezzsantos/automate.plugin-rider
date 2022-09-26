package jezzsantos.automate.plugin.infrastructure.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponentWithModificationTracker;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.SimpleModificationTracker;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import com.jetbrains.rd.util.UsedImplicitly;
import com.jetbrains.rd.util.lifetime.LifetimeDefinition;
import com.jetbrains.rd.util.reactive.Property;
import jezzsantos.automate.plugin.application.interfaces.CliInstallPolicy;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.infrastructure.settings.converters.BooleanPropertyConverter;
import jezzsantos.automate.plugin.infrastructure.settings.converters.CliInstallPolicyPropertyConverter;
import jezzsantos.automate.plugin.infrastructure.settings.converters.EditingModePropertyConverter;
import jezzsantos.automate.plugin.infrastructure.settings.converters.StringPropertyConverter;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "jezzsantos.automate.infrastructure.settings.ApplicationSettingsState", storages = @Storage("automate.xml"))
public class ApplicationSettingsState implements PersistentStateComponentWithModificationTracker<ApplicationSettingsState> {

    @OptionTag(converter = BooleanPropertyConverter.class)
    public final Property<Boolean> authoringMode = new Property<>(false);
    @OptionTag(converter = EditingModePropertyConverter.class)
    public final Property<EditingMode> editingMode = new Property<>(EditingMode.DRAFTS);
    @OptionTag(converter = StringPropertyConverter.class)
    public final Property<String> pathToAutomateExecutable = new Property<>("");
    @OptionTag(converter = BooleanPropertyConverter.class)
    public final Property<Boolean> viewCliLog = new Property<>(false);
    @OptionTag(converter = CliInstallPolicyPropertyConverter.class)
    public final Property<CliInstallPolicy> cliInstallPolicy = new Property<>(CliInstallPolicy.AUTO_UPGRADE);
    private final SimpleModificationTracker tracker = new SimpleModificationTracker();

    @UsedImplicitly
    public ApplicationSettingsState() {

        registerAllPropertyToIncrementTrackerOnChanges(this);
    }

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

    private void registerAllPropertyToIncrementTrackerOnChanges(@NotNull ApplicationSettingsState state) {

        incrementTrackerWhenPropertyChanges(state.authoringMode);
        incrementTrackerWhenPropertyChanges(state.editingMode);
        incrementTrackerWhenPropertyChanges(state.pathToAutomateExecutable);
        incrementTrackerWhenPropertyChanges(state.viewCliLog);
        incrementTrackerWhenPropertyChanges(state.cliInstallPolicy);
    }

    private <T> void incrementTrackerWhenPropertyChanges(Property<T> property) {

        property.advise(new LifetimeDefinition(), v -> {
            this.tracker.incModificationCount();
            return Unit.INSTANCE;
        });
    }
}
