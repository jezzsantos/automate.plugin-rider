package jezzsantos.automate.ui;

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.treeStructure.Tree;
import jezzsantos.automate.AutomateBundle;
import jezzsantos.automate.ui.components.AddPatternAction;
import jezzsantos.automate.ui.components.OptionsToolbarAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class AutomateToolWindow {
    @NotNull
    private final Project project;
    @NotNull
    private final ToolWindow toolWindow;
    private JPanel mainPanel;
    private ActionToolbarImpl toolbar;
    private Tree patterns;

    public AutomateToolWindow(
            @NotNull Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;

        this.refreshContents();
    }

    @NotNull
    public JPanel getContent() {
        return mainPanel;
    }

    private void createUIComponents() {

        toolbar = createToolbar();
    }

    @NotNull
    private ActionToolbarImpl createToolbar() {
        final DefaultActionGroup actions = new DefaultActionGroup();

        actions.add(new AddPatternAction());
        actions.addSeparator();
        actions.add(new OptionsToolbarAction());

        var actionToolbar = new ActionToolbarImpl(ActionPlaces.CONTEXT_TOOLBAR, actions, true);
        actionToolbar.setTargetComponent(mainPanel);
        actionToolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);

        return actionToolbar;
    }

    public void refreshContents() {
        patterns.getEmptyText().setText(AutomateBundle.message("toolWindow.EmptyPatterns"));

        var root = ((DefaultMutableTreeNode) patterns.getModel().getRoot());
        root.setUserObject(new DefaultMutableTreeNode("Patterns"));
        root.add(new DefaultMutableTreeNode("apattername"));
        patterns.expandRow(0);

        //TODO: update the tree
    }

}

