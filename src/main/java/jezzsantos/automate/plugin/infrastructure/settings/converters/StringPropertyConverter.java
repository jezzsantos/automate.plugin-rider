package jezzsantos.automate.plugin.infrastructure.settings.converters;

import com.intellij.util.xmlb.Converter;
import com.jetbrains.rd.util.reactive.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StringPropertyConverter extends Converter<Property<String>> {
    @Override
    public @Nullable Property<String> fromString(@NotNull String s) {
        return new Property<>(s);
    }

    @Override
    public @Nullable String toString(@NotNull Property<String> stringProperty) {
        return stringProperty.getValue();
    }
}
