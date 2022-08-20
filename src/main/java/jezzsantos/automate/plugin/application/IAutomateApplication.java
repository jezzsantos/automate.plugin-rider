package jezzsantos.automate.plugin.application;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftDetailed;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternDetailed;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface IAutomateApplication {

    static IAutomateApplication getInstance(Project project) {
        return project.getService(IAutomateApplication.class);
    }

    @NotNull String getDefaultInstallLocation();

    @Nullable String tryGetExecutableVersion(@NotNull String executablePath);

    @NotNull List<PatternLite> listPatterns();

    @NotNull List<ToolkitLite> listToolkits();

    @NotNull List<DraftLite> listDrafts();

    @SuppressWarnings("UnusedReturnValue")
    @NotNull PatternLite createPattern(@NotNull String name) throws Exception;

    boolean isAuthoringMode();

    void setAuthoringMode(boolean on);

    EditingMode getEditingMode();

    void setEditingMode(@NotNull EditingMode mode);

    @NotNull PatternDetailed getCurrentPatternDetailed() throws Exception;

    @Nullable PatternLite getCurrentPatternInfo();

    void setCurrentPattern(@NotNull String id) throws Exception;

    @NotNull DraftDetailed getCurrentDraftDetailed() throws Exception;

    @Nullable DraftLite getCurrentDraftInfo();

    void setCurrentDraft(@NotNull String id) throws Exception;

    @SuppressWarnings("UnusedReturnValue")
    @NotNull DraftLite createDraft(@NotNull String toolkitName, @NotNull String name) throws Exception;

    void installToolkit(@NotNull String location) throws Exception;

    @NotNull AllStateLite refreshLocalState();

    void addPropertyListener(@NotNull PropertyChangeListener listener);

    void removePropertyListener(@NotNull PropertyChangeListener listener);

    void addConfigurationListener(@NotNull PropertyChangeListener listener);

    void removeConfigurationListener(@NotNull PropertyChangeListener listener);

    @NotNull List<CliLogEntry> getCliLogEntries();

    boolean getViewCliLog();
}
