package jezzsantos.automate.plugin.infrastructure.services.cli.responses.drafts;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftUpgradeReport;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftUpgradeReportItem;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutput;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutputOutput;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
class UpgradedDraft {

    public String DraftName;
    public String DraftId;
    public String ToolkitName;
    public String OldVersion;
    public String NewVersion;
    public List<DraftUpgradeReportItem> Log;
}

public class UpgradeDraftStructuredOutput extends StructuredOutput<UpgradedDraft> {

    @TestOnly
    public UpgradeDraftStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new UpgradedDraft();
        }})));
    }

    public DraftUpgradeReport getReport() {

        var values = this.Output.get(0).Values;
        return new DraftUpgradeReport(values.OldVersion, values.NewVersion, Objects.requireNonNullElse(values.Log, List.of()));
    }
}
