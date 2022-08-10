package jezzsantos.automate.plugin.application;

import org.jetbrains.annotations.Nullable;

public interface IAutomateService {
    String getExecutableName();

    String getDefaultInstallLocation();

    String tryGetExecutableVersion(@Nullable String executablePath);
}
