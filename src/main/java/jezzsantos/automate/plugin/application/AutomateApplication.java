package jezzsantos.automate.plugin.application;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class AutomateApplication implements IAutomateApplication {

    @NotNull
    @Override
    public String getExecutableName() {
        return (IsWindowsOs()
                ? "automate.exe"
                : "automate");
    }

    @NotNull
    @Override
    public String getDefaultInstallLocation() {
        return (IsWindowsOs()
                ? System.getenv("USERPROFILE") + "\\.dotnet\\tools\\"
                : System.getProperty("user.home") + "/.dotnet/tools/") + this.getExecutableName();
    }

    @Nullable
    @Override
    public String tryGetExecutableVersion(@Nullable String executablePath) {

        var path = executablePath == null || executablePath.isEmpty() ? getDefaultInstallLocation() : executablePath;
        var args = "--version";

        return runAutomate(path, args);

    }


    @Nullable
    private String runAutomate(String path, String args) {
        String result;
        try {
            var process = new ProcessBuilder(path, args).start();
            var success = process.waitFor(5, TimeUnit.SECONDS);
            if (!success) {
                return null;
            }
            if (process.exitValue() != 0) {
                return null;
            } else {
                var outputStream = process.getInputStream();
                var output = outputStream.readAllBytes();
                outputStream.close();
                result = new String(output, StandardCharsets.UTF_8).trim();
            }

            process.destroy();
            return result;

        } catch (InterruptedException | IOException e) {
            return null;
        }
    }


    private Boolean IsWindowsOs() {
        return System.getProperty("os.name").startsWith("Windows");
    }
}
