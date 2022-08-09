package jezzsantos.automate.core;

public class AutomateConstants {

    public static final String ExecutableName = (System.getProperty("os.name").startsWith("Windows")
            ? "automate.exe"
            : "automate");
    public static final String InstallLocation = (System.getProperty("os.name").startsWith("Windows")
            ? System.getenv("USERPROFILE") + "\\.dotnet\\tools\\"
            : System.getProperty("user.home") + "/.dotnet/tools/") + ExecutableName;
    public static final String PatternNameRegex = "^[a-zA-Z\\d_\\.\\-]+$";
}
