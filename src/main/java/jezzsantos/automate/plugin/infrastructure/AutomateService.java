package jezzsantos.automate.plugin.infrastructure;

import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.IAutomateService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutomateService implements IAutomateService {

    private final IAutomateApplication application;

    public AutomateService(@NotNull IAutomateApplication application) {
        this.application = application;
    }

    @Override
    public String getExecutableName() {
        return this.application.getExecutableName();
    }

    @Override
    public String getDefaultInstallLocation() {
        return this.application.getDefaultInstallLocation();
    }


    @Override
    public String tryGetExecutableVersion(@Nullable String executablePath) {

        return this.application.tryGetExecutableVersion(executablePath);
    }
}
