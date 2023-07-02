package jezzsantos.automate.plugin.infrastructure.settings;

import com.intellij.openapi.components.PersistentStateComponentWithModificationTracker;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SimpleModificationTracker;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import com.jetbrains.rd.util.UsedImplicitly;
import com.jetbrains.rd.util.lifetime.LifetimeDefinition;
import com.jetbrains.rd.util.reactive.Property;
import jezzsantos.automate.plugin.application.interfaces.CliInstallPolicy;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.common.IContainer;
import jezzsantos.automate.plugin.common.StringWithDefault;
import jezzsantos.automate.plugin.infrastructure.services.cli.AutomateCliService;
import jezzsantos.automate.plugin.infrastructure.settings.converters.BooleanPropertyConverter;
import jezzsantos.automate.plugin.infrastructure.settings.converters.CliInstallPolicyPropertyConverter;
import jezzsantos.automate.plugin.infrastructure.settings.converters.EditingModePropertyConverter;
import jezzsantos.automate.plugin.infrastructure.settings.converters.StringWithDefaultPropertyConverter;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
@State(name = "jezzsantos.automate.infrastructure.settings.ProjectSettingsState", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class ProjectSettingsState implements PersistentStateComponentWithModificationTracker<ProjectSettingsState> {

    public static final String defaultExecutablePath = AutomateCliService.getDefaultExecutableLocation(IContainer.getOsPlatform());
    @OptionTag(converter = BooleanPropertyConverter.class)
    public final Property<Boolean> authoringMode = new Property<>(false);
    @OptionTag(converter = EditingModePropertyConverter.class)
    public final Property<EditingMode> editingMode = new Property<>(EditingMode.DRAFTS);
    @OptionTag(converter = BooleanPropertyConverter.class)
    public final Property<Boolean> viewCliLog = new Property<>(false);
    @OptionTag(converter = CliInstallPolicyPropertyConverter.class)
    public final Property<CliInstallPolicy> cliInstallPolicy = new Property<>(CliInstallPolicy.AUTO_UPGRADE);
    @OptionTag(converter = StringWithDefaultPropertyConverter.class)
    public final Property<StringWithDefault> executablePath = new Property<>(createExecutablePathWithDefaultValue());
    @OptionTag(converter = BooleanPropertyConverter.class)
    public final Property<Boolean> allowUsageCollection = new Property<>(true);
    private final SimpleModificationTracker tracker = new SimpleModificationTracker();

    @UsedImplicitly
    public ProjectSettingsState() {

        registerAllPropertyToIncrementTrackerOnChanges(this);
    }

    public static ProjectSettingsState getInstance(@NotNull Project project) {

        return project.getService(ProjectSettingsState.class);
    }

    @NotNull
    public static StringWithDefault createExecutablePathWithValue(@NotNull String value) {

        return new StringWithDefault(defaultExecutablePath, value);
    }

    @NotNull
    public static StringWithDefault createExecutablePathWithDefaultValue() {

        return new StringWithDefault(defaultExecutablePath);
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
        incrementTrackerWhenPropertyChanges(state.executablePath);
        incrementTrackerWhenPropertyChanges(state.viewCliLog);
        incrementTrackerWhenPropertyChanges(state.cliInstallPolicy);
        incrementTrackerWhenPropertyChanges(state.allowUsageCollection);
    }

    private <T> void incrementTrackerWhenPropertyChanges(Property<T> property) {

        property.advise(new LifetimeDefinition(), v -> {
            this.tracker.incModificationCount();
            return Unit.INSTANCE;
        });
    }
}
