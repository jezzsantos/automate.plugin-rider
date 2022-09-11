package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

public class TestModelTreeListener implements TreeModelListener {

    private boolean hasRemovedBeenCalled = false;
    private int indexRemoved = -1;
    private Object childRemoved = null;

    @Override
    public void treeNodesChanged(TreeModelEvent e) {

    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {

    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {

        this.hasRemovedBeenCalled = true;
        this.indexRemoved = e.getChildIndices()[0];
        this.childRemoved = e.getChildren()[0];
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {

    }

    public boolean hasRemoved(int index, Object node) {

        return (this.indexRemoved == index && this.childRemoved == node);
    }

    public boolean hasRemoveEventBeenRaised() {

        return this.hasRemovedBeenCalled;
    }
}
