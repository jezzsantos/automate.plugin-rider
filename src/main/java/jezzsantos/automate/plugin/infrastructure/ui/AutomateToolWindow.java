package jezzsantos.automate.plugin.infrastructure.ui;

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.components.actions.AddPatternAction;
import jezzsantos.automate.plugin.infrastructure.ui.components.actions.OptionsToolbarAction;
import jezzsantos.automate.plugin.infrastructure.ui.components.actions.RefreshPatternsAction;
import jezzsantos.automate.plugin.infrastructure.ui.components.actions.SelectAuthoringModeAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class AutomateToolWindow {
    @NotNull
    private final Project project;
    private JPanel mainPanel;
    private ActionToolbarImpl toolbar;
    private Tree patternsTree;

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

        var application = project.getService(IAutomateApplication.class);
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
        final var actions = new DefaultActionGroup();

        actions.add(new AddPatternAction((pattern) -> refreshContents()));
        actions.add(new RefreshPatternsAction((refresh) -> refreshContents()));
        actions.addSeparator();
        actions.add(new OptionsToolbarAction());
        actions.addSeparator();
        actions.add(new SelectAuthoringModeAction((isSelected) -> refreshContents()));

        var actionToolbar = new ActionToolbarImpl(ActionPlaces.TOOLWINDOW_CONTENT, actions, true);
        actionToolbar.setTargetComponent(mainPanel);
        actionToolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);

        return actionToolbar;
    }

    public void refreshContents() {
        var model = (DefaultTreeModel) patternsTree.getModel();
        var root = ((DefaultMutableTreeNode) model.getRoot());
        root.removeAllChildren();

        var patterns = project.getService(IAutomateApplication.class).getPatterns();
        for (var pattern : patterns) {
            root.add(new DefaultMutableTreeNode(pattern));
        }

        model.reload();
        patternsTree.expandRow(0);
    }
}

