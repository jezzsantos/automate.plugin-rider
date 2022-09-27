package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.CliInstallPolicy;
import jezzsantos.automate.plugin.application.services.interfaces.CliExecutableStatus;
import jezzsantos.automate.plugin.common.StringWithImplicitDefault;
import org.jetbrains.annotations.NotNull;

public interface ICliUpgrader {

    @NotNull
    CliExecutableStatus upgrade(@NotNull String currentDirectory, @NotNull StringWithImplicitDefault executablePath, @NotNull String executableName, @NotNull CliExecutableStatus executableStatus, @NotNull CliInstallPolicy installPolicy);
}
