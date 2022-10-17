package jezzsantos.automate;

import jezzsantos.automate.plugin.common.Try;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.Properties;

public class ApplicationSettings {

    @NonNls
    private static final String BUNDLE = "application";
    private static Properties properties;

    @NotNull
    public static String setting(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object... params) {

        ensureConfiguration();

        var setting = properties.getProperty(String.format(key, params));
        return setting == null
          ? ""
          : setting;
    }

    private static void ensureConfiguration() {

        if (properties == null) {
            properties = new Properties();
            addConfiguration("application.properties");
            addConfiguration("application.local.properties");
        }
    }

    private static void addConfiguration(String relativePath) {

        Try.safely(() -> {
            var stream = ApplicationSettings.class.getClassLoader().getResourceAsStream(relativePath);
            if (stream != null) {
                ApplicationSettings.properties.load(stream);
                stream.close();
            }
        });
    }
}
