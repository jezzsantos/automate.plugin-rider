package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;

import java.util.List;

public class NewDraftDialogContext {

    public List<ToolkitLite> InstalledToolkits;
    public List<DraftLite> Drafts;
    public String Name;
    public String ToolkitName;

    public NewDraftDialogContext(List<ToolkitLite> installedToolkits, List<DraftLite> drafts) {
        this.InstalledToolkits = installedToolkits;
        this.Drafts = drafts;
    }
}
