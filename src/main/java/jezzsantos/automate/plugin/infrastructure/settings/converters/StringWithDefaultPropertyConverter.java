package jezzsantos.automate.plugin.infrastructure.settings.converters;

import com.intellij.util.xmlb.Converter;
import com.jetbrains.rd.util.reactive.Property;
import jezzsantos.automate.plugin.common.StringWithImplicitDefault;
import jezzsantos.automate.plugin.infrastructure.settings.ApplicationSettingsState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringWithDefaultPropertyConverter extends Converter<Property<StringWithImplicitDefault>> {

    @Override
    public @Nullable Property<StringWithImplicitDefault> fromString(@NotNull String s) {

        return new Property<>(ApplicationSettingsState.createExecutablePathWithValue(s));
    }

    @Override
    public @Nullable String toString(@NotNull Property<StringWithImplicitDefault> property) {

        return property.getValue().getValue();
    }
}
