package jezzsantos.automate.plugin.application.services.interfaces;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;
import jezzsantos.automate.plugin.application.interfaces.PatternDefinition;
import jezzsantos.automate.plugin.application.interfaces.ToolkitDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IAutomateService {

    static IAutomateService getInstance(Project project) {
        return project.getService(IAutomateService.class);
    }

    @NotNull
    String getExecutableName();

    @NotNull
    String getDefaultInstallLocation();

    @Nullable
    String tryGetExecutableVersion(@Nullable String executablePath);

    @NotNull
    List<PatternDefinition> getPatterns(@Nullable String executablePath);

    @NotNull
    List<ToolkitDefinition> getToolkits(String executablePath);

    @NotNull
    List<DraftDefinition> getDrafts(@Nullable String executablePath);

    @NotNull
    PatternDefinition addPattern(@NotNull String executablePath, @NotNull String name) throws Exception;
}
