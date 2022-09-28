package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import jezzsantos.automate.plugin.infrastructure.ui.components.AutomateTree;

import javax.swing.tree.TreePath;

public class AutomateTreeSelector implements ITreeSelector {

    private final AutomateTree tree;

    public AutomateTreeSelector(AutomateTree tree) {

        this.tree = tree;
    }

    @Override
    public void selectAndExpandPath(TreePath path) {

        selectPath(path);
        this.tree.expandPath(path);
    }

    @Override
    public void selectPath(TreePath path) {

        this.tree.getSelectionModel().clearSelection();
        this.tree.addSelectionPath(path);
    }
}
