package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.project.Project;
import com.intellij.ui.Colors;
import com.intellij.ui.treeStructure.Tree;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntryType;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.actions.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class AutomateToolWindow {
    @NotNull
    private final Project project;
    private JPanel mainPanel;
    private ActionToolbarImpl toolbar;
    private Tree patternsTree;
    private JTextPane cliLog;
    private JSplitPane windowSplit;

    public AutomateToolWindow(
            @NotNull Project project) {
        this.project = project;

        this.init();
    }

    @NotNull
    public JPanel getContent() {
        return mainPanel;
    }

    private void createUIComponents() {

        toolbar = createToolbar();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        var application = IAutomateApplication.getInstance(project);
        application.addCliLogListener(e -> {
            var entries = (List<CliLogEntry>) e.getNewValue();
            writeLogEntry(entries.get(0));
        });
        var log = application.getCliLog();
        for (var entry : log) {
            writeLogEntry(entry);
        }
        patternsTree.getEmptyText().setText(AutomateBundle.message("toolWindow.EmptyPatterns"));
        windowSplit.setResizeWeight(0.0d);
        var root = ((DefaultMutableTreeNode) patternsTree.getModel().getRoot());
        root.setUserObject(new DefaultMutableTreeNode(AutomateBundle.message("toolWindow.RootNode.Title")));
        this.refreshContents();
    }

    @NotNull
    private ActionToolbarImpl createToolbar() {
        final var actions = new DefaultActionGroup();

        actions.add(new TogglePatternEditingModeAction());
        actions.add(new ToggleDraftEditingModeAction());
        actions.addSeparator();
        actions.add(new PatternsListToolbarAction());
        actions.add(new AddPatternAction((pattern) -> refreshContents()));
        actions.add(new InstallToolkitToolbarAction());
        actions.add(new DraftsListToolbarAction());
        actions.add(new AddDraftAction((draft) -> refreshContents()));
        actions.add(new RefreshPatternsAction((refresh) -> refreshContents()));
        actions.addSeparator();
        actions.add(new ShowSettingsToolbarAction());
        actions.add(new AdvancedOptionsToolbarActionGroup());
        actions.addSeparator();
        actions.add(new ToggleAuthoringModeToolbarAction((isSelected) -> refreshContents()));

        var actionToolbar = new ActionToolbarImpl(ActionPlaces.TOOLWINDOW_CONTENT, actions, true);
        actionToolbar.setTargetComponent(mainPanel);
        actionToolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);

        return actionToolbar;
    }

    private void refreshContents() {
        var model = (DefaultTreeModel) patternsTree.getModel();
        var root = ((DefaultMutableTreeNode) model.getRoot());
        root.removeAllChildren();

        var patterns = IAutomateApplication.getInstance(project).getPatterns();
        for (var pattern : patterns) {
            root.add(new DefaultMutableTreeNode(pattern));
        }

        model.reload();
        patternsTree.expandRow(0);
    }

    private void writeLogEntry(CliLogEntry entry) {
        var attributes = new SimpleAttributeSet();
        if (entry.Type != CliLogEntryType.Normal) {
            StyleConstants.setForeground(attributes, entry.Type == CliLogEntryType.Error ? Colors.DARK_RED : Colors.DARK_GREEN);
        }
        var text = String.format("%s%s", entry.Text, System.lineSeparator());
        var document = cliLog.getDocument();
        try {
            document.insertString(document.getLength(), text, attributes);
        } catch (Exception ignored) {
        }
    }
}

