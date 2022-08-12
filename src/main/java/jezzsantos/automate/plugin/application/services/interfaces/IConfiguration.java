package jezzsantos.automate.plugin.application.services.interfaces;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public interface IConfiguration {

    static IConfiguration getInstance(Project project) {
        return project.getService(IConfiguration.class);
    }

    @NotNull
    String getExecutablePath();

    Boolean getAuthoringMode();

    void setAuthoringMode(boolean on);
}
