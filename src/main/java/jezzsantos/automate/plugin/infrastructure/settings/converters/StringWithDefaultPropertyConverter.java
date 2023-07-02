package jezzsantos.automate.plugin.infrastructure.settings.converters;

import com.intellij.util.xmlb.Converter;
import com.jetbrains.rd.util.reactive.Property;
import jezzsantos.automate.plugin.common.StringWithDefault;
import jezzsantos.automate.plugin.infrastructure.settings.ProjectSettingsState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringWithDefaultPropertyConverter extends Converter<Property<StringWithDefault>> {

    @Override
    public @Nullable Property<StringWithDefault> fromString(@NotNull String s) {

        return new Property<>(ProjectSettingsState.createExecutablePathWithValue(s));
    }

    @Override
    public @Nullable String toString(@NotNull Property<StringWithDefault> property) {

        return property.getValue().getValue();
    }
}
