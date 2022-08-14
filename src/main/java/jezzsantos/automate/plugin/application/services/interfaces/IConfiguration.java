package jezzsantos.automate.plugin.application.services.interfaces;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import org.jetbrains.annotations.NotNull;

public interface IConfiguration {

    static IConfiguration getInstance(Project project) {
        return project.getService(IConfiguration.class);
    }

    @NotNull
    String getExecutablePath();

    Boolean getAuthoringMode();

    void setAuthoringMode(boolean on);

    EditingMode getEditingMode();

    void setEditingMode(EditingMode mode);
}
