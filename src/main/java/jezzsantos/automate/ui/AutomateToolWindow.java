package jezzsantos.automate.ui;

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.treeStructure.Tree;
import jezzsantos.automate.AutomateBundle;
import jezzsantos.automate.data.PatternDefinition;
import jezzsantos.automate.ui.components.AddPatternAction;
import jezzsantos.automate.ui.components.OptionsToolbarAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.List;

public class AutomateToolWindow {
    @NotNull
    private final Project project;
    @NotNull
    private final ToolWindow toolWindow;

    private List<PatternDefinition> patterns;
    private JPanel mainPanel;
    private ActionToolbarImpl toolbar;
    private Tree patternsTree;

    public AutomateToolWindow(
            @NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;

        this.init();
    }

    @NotNull
    public JPanel getContent() {
        return mainPanel;
    }

    private void createUIComponents() {

        patterns = new ArrayList<>();
        toolbar = createToolbar();
    }

    private void init() {
        patternsTree.getEmptyText().setText(AutomateBundle.message("toolWindow.EmptyPatterns"));
        var root = ((DefaultMutableTreeNode) patternsTree.getModel().getRoot());
        root.setUserObject(new DefaultMutableTreeNode("Patterns"));
        this.refreshContents();
    }

    @NotNull
    private ActionToolbarImpl createToolbar() {
        final DefaultActionGroup actions = new DefaultActionGroup();

        actions.add(new AddPatternAction(this.patterns, (result) -> refreshContents()));
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

