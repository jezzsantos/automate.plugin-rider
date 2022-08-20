package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import jezzsantos.automate.plugin.application.interfaces.patterns.PatternDetailed;
import org.jetbrains.annotations.NotNull;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

class PatternTreeModel implements TreeModel {
    @NotNull
    private final PatternDetailed pattern;

    public PatternTreeModel(@NotNull PatternDetailed pattern) {

        this.pattern = pattern;
    }

    @Override
    public Object getRoot() {
        return this.pattern;
    }

    @Override
    public Object getChild(Object parent, int index) {
        return null;
    }

    @Override
    public int getChildCount(Object parent) {
        return 0;
    }

    @Override
    public boolean isLeaf(Object node) {
        return false;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return 0;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {

    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {

    }
}
