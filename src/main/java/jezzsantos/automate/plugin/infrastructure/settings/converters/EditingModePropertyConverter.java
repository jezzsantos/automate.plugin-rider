package jezzsantos.automate.plugin.infrastructure.settings.converters;

import com.intellij.util.xmlb.Converter;
import com.jetbrains.rd.util.reactive.Property;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EditingModePropertyConverter extends Converter<Property<EditingMode>> {

    @Override
    public @Nullable Property<EditingMode> fromString(@NotNull String s) {

        return new Property<>(s.equalsIgnoreCase(EditingMode.DRAFTS.toString())
                                ? EditingMode.DRAFTS
                                : EditingMode.PATTERNS);
    }

    @Override
    public @Nullable String toString(@NotNull Property<EditingMode> mode) {

        return mode.getValue().toString();
    }
}
