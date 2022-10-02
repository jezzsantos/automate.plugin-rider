package jezzsantos.automate.plugin.application.interfaces.drafts;

import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class DraftUpgradeReport {

    private final String oldVersion;
    private final String newVersion;
    private final List<DraftUpgradeReportItem> changes;

    public DraftUpgradeReport(@NotNull String oldVersion, @NotNull String newVersion, @NotNull List<DraftUpgradeReportItem> changes) {

        this.oldVersion = oldVersion;
        this.newVersion = newVersion;
        this.changes = changes;
    }

    @NotNull
    public List<DraftUpgradeReportItem> getChanges() {return this.changes;}

    public String getOldVersion() {return this.oldVersion;}

    public String getNewVersion() {return this.newVersion;}
}
