package jezzsantos.automate.plugin.application.services.interfaces;

import org.jetbrains.annotations.NotNull;

public interface IConfiguration {
    @NotNull
    String getExecutablePath();

    Boolean getAuthoringMode();
}
