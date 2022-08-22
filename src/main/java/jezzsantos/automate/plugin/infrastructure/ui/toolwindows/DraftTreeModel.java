package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import com.intellij.util.ui.tree.AbstractTreeModel;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftDetailed;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

class DraftTreeModel extends AbstractTreeModel {

    @NotNull
    private final DraftDetailed draft;

    public DraftTreeModel(@NotNull DraftDetailed draft) {

        this.draft = draft;
    }

    @Override
    public Object getRoot() {
        return this.draft;
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
    public int getIndexOfChild(Object parent, Object child) {
        return 0;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object value) {

    }
}
