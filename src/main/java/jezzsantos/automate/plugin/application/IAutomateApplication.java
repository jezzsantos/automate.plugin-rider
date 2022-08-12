package jezzsantos.automate.plugin.application;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;
import jezzsantos.automate.plugin.application.interfaces.PatternDefinition;
import jezzsantos.automate.plugin.application.interfaces.ToolkitDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IAutomateApplication {

    static IAutomateApplication getInstance(Project project) {
        return project.getService(IAutomateApplication.class);
    }

    @NotNull
    String getExecutableName();

    @NotNull
    String getDefaultInstallLocation();

    @Nullable
    String tryGetExecutableVersion(@Nullable String executablePath);

    @NotNull
    List<PatternDefinition> getPatterns();

    @NotNull
    List<ToolkitDefinition> getToolkits();

    @NotNull
    List<DraftDefinition> getDrafts();

    @NotNull
    PatternDefinition addPattern(@NotNull String name) throws Exception;

    boolean isAuthoringMode();

    void setAuthoringMode(boolean on);
}
