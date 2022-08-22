package jezzsantos.automate.plugin.application.services.interfaces;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftDetailed;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternDetailed;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;
import java.util.List;

public interface IAutomateService {

    static IAutomateService getInstance(Project project) {
        return project.getService(IAutomateService.class);
    }

    @NotNull String getExecutableName();

    @NotNull String getDefaultInstallLocation();

    @Nullable String tryGetExecutableVersion(@NotNull String executablePath);

    @NotNull AllStateLite listAllAutomation(boolean forceRefresh);

    @NotNull List<PatternLite> listPatterns();

    @NotNull List<ToolkitLite> listToolkits();

    @NotNull List<DraftLite> listDrafts();

    @NotNull PatternLite createPattern(@NotNull String name) throws Exception;

    @NotNull PatternDetailed getCurrentPatternDetailed() throws Exception;

    @Nullable PatternLite getCurrentPatternInfo();

    void setCurrentPattern(@NotNull String id) throws Exception;

    @NotNull DraftDetailed getCurrentDraftDetailed() throws Exception;

    @Nullable DraftLite getCurrentDraftInfo();

    void setCurrentDraft(@NotNull String id) throws Exception;

    @NotNull DraftLite createDraft(@NotNull String toolkitName, @NotNull String name) throws Exception;

    void installToolkit(@NotNull String location) throws Exception;

    void addPropertyChangedListener(@NotNull PropertyChangeListener listener);

    void removePropertyChangedListener(@NotNull PropertyChangeListener listener);

    @NotNull List<CliLogEntry> getCliLog();

    Attribute addAttribute(@NotNull String name, boolean isRequired, @NotNull String type, @Nullable String defaultValue, @Nullable List<String> choices) throws Exception;

    void deleteAttribute(@NotNull String name) throws Exception;
}
