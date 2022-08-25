package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OsPlatform implements IOsPlatform {

    @NotNull
    private final Project project;

    public OsPlatform(@NotNull Project project) {

        this.project = project;
    }

    @NotNull
    @Override
    public String getCurrentDirectory() {

        return Objects.requireNonNull(this.project.getBasePath());
    }

    @Override
    public boolean getIsWindowsOs() {

        return System.getProperty("os.name").startsWith("Windows");
    }

    @Override
    public @NotNull String getDotNetInstallationDirectory() {

        return getIsWindowsOs()
          ? System.getenv("USERPROFILE") + "\\.dotnet\\tools\\"
          : System.getProperty("user.home") + "/.dotnet/tools/";
    }
}
