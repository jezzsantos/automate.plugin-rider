package jezzsantos.automate.ui;

import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import jezzsantos.automate.AutomateBundle;
import jezzsantos.automate.data.PatternDefinition;
import jezzsantos.automate.ui.components.AddPatternAction;
import jezzsantos.automate.ui.components.OptionsToolbarAction;
import jezzsantos.automate.ui.components.RefreshPatternsAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("FieldCanBeLocal")
public class AutomateToolWindow {
    @NotNull
    private final Project project;

    private List<PatternDefinition> patterns;
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

        patterns = new ArrayList<>();
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

