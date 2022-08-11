package jezzsantos.automate.plugin.infrastructure.ui;

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;
import jezzsantos.automate.plugin.application.interfaces.PatternDefinition;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.settings.ProjectSettingsState;
import jezzsantos.automate.plugin.infrastructure.ui.components.AddPatternAction;
import jezzsantos.automate.plugin.infrastructure.ui.components.OptionsToolbarAction;
import jezzsantos.automate.plugin.infrastructure.ui.components.RefreshPatternsAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class AutomateToolWindow {
    @NotNull
    private final Project project;
    private final IAutomateApplication automateApplication;

    private List<PatternDefinition> patterns;
    private List<DraftDefinition> drafts;
    private JPanel mainPanel;
    private ActionToolbarImpl toolbar;
    private Tree patternsTree;

    public AutomateToolWindow(
            @NotNull Project project, @NotNull IAutomateApplication automateApplication) {
        this.project = project;
        this.automateApplication = automateApplication;

        this.init();
    }

    @NotNull
    public JPanel getContent() {
        return mainPanel;
    }

    private void createUIComponents() {

        var executablePath = ProjectSettingsState.getInstance(this.project).pathToAutomateExecutable.getValue();
        patterns = this.automateApplication.getPatterns(executablePath);
        drafts = this.automateApplication.getDrafts(executablePath);
        toolbar = createToolbar();
    }

    private void init() {
        patternsTree.getEmptyText().setText(AutomateBundle.message("toolWindow.EmptyPatterns"));
        var root = ((DefaultMutableTreeNode) patternsTree.getModel().getRoot());
        root.setUserObject(new DefaultMutableTreeNode(AutomateBundle.message("toolWindow.RootNode.Title")));
        this.refreshContents();
    }

    @NotNull
    private ActionToolbarImpl createToolbar() {
        final DefaultActionGroup actions = new DefaultActionGroup();

        actions.add(new AddPatternAction(this.patterns, (result) -> refreshContents()));
        actions.add(new RefreshPatternsAction((result) -> refreshContents()));
        actions.addSeparator();
        actions.add(new OptionsToolbarAction());

        var actionToolbar = new ActionToolbarImpl(ActionPlaces.CONTEXT_TOOLBAR, actions, true);
        actionToolbar.setTargetComponent(mainPanel);
        actionToolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);

        return actionToolbar;
    }

    public void refreshContents() {
        var model = (DefaultTreeModel) patternsTree.getModel();
        var root = ((DefaultMutableTreeNode) model.getRoot());
        root.removeAllChildren();

        for (var pattern : patterns) {
            root.add(new DefaultMutableTreeNode(pattern));
        }

        model.reload();
        patternsTree.expandRow(0);
    }

}

