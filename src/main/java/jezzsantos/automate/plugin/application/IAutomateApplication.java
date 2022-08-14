package jezzsantos.automate.plugin.application;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
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
    PatternDefinition createPattern(@NotNull String name) throws Exception;

    boolean isAuthoringMode();

    void setAuthoringMode(boolean on);

    EditingMode getEditingMode();

    void setEditingMode(@NotNull EditingMode mode);

    @Nullable
    PatternDefinition getCurrentPattern();

    void setCurrentPattern(@NotNull String id) throws Exception;

    @Nullable
    DraftDefinition getCurrentDraft();

    void setCurrentDraft(@NotNull String id) throws Exception;

    @NotNull
    DraftDefinition createDraft(@NotNull String toolkitName, @NotNull String name) throws Exception;
}
