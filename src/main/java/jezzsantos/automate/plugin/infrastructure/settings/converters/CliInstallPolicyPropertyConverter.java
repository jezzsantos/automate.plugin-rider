package jezzsantos.automate.plugin.infrastructure.settings.converters;

import com.intellij.util.xmlb.Converter;
import com.jetbrains.rd.util.reactive.Property;
import jezzsantos.automate.plugin.application.interfaces.CliInstallPolicy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CliInstallPolicyPropertyConverter extends Converter<Property<CliInstallPolicy>> {

    @Override
    public @Nullable Property<CliInstallPolicy> fromString(@NotNull String s) {

        return new Property<>(s.equalsIgnoreCase(CliInstallPolicy.NONE.toString())
                                ? CliInstallPolicy.NONE
                                : CliInstallPolicy.AUTO_UPGRADE);
    }

    @Override
    public @Nullable String toString(@NotNull Property<CliInstallPolicy> property) {

        return property.getValue().toString();
    }
}
