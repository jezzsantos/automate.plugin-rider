//package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;
//
//import javax.swing.event.TreeModelEvent;
//import javax.swing.event.TreeModelListener;
//
//public class TestModelTreeListener implements TreeModelListener {
//
//    private boolean hasRemovedBeenCalled = false;
//    private boolean hasInsertedBeenCalled = false;
//    private boolean hasChangedBeenCalled = false;
//    private int indexRemoved = -1;
//    private Object childRemoved = null;
//    private int indexInserted = -1;
//    private Object childInserted = null;
//    private int indexChanged = -1;
//    private Object childChanged = null;
//
//    public boolean hasInserted(int index, Object node) {
//
//        return (this.indexInserted == index && this.childInserted.equals(node));
//    }
//
//    public boolean hasRemoved(int index, Object node) {
//
//        return (this.indexRemoved == index && this.childRemoved.equals(node));
//    }
//
//    public boolean hasChanged(int index, Object node) {
//
//        return (this.indexChanged == index && this.childChanged.equals(node));
//    }
//
//    public boolean hasRemoveEventBeenRaised() {
//
//        return this.hasRemovedBeenCalled;
//    }
//
//    public boolean hasInsertEventBeenRaised() {
//
//        return this.hasInsertedBeenCalled;
//    }
//
//    public boolean hasChangeEventBeenRaised() {
//
//        return this.hasChangedBeenCalled;
//    }
//
//    @Override
//    public void treeNodesChanged(TreeModelEvent e) {
//
//        this.hasChangedBeenCalled = true;
//        this.indexChanged = e.getChildIndices() == null
//          ? -1
//          : e.getChildIndices()[0];
//        this.childChanged = e.getChildren() == null
//          ? null
//          : e.getChildren()[0];
//    }
//
//    @Override
//    public void treeNodesInserted(TreeModelEvent e) {
//
//        this.hasInsertedBeenCalled = true;
//        this.indexInserted = e.getChildIndices()[0];
//        this.childInserted = e.getChildren()[0];
//    }
//
//    @Override
//    public void treeNodesRemoved(TreeModelEvent e) {
//
//        this.hasRemovedBeenCalled = true;
//        this.indexRemoved = e.getChildIndices()[0];
//        this.childRemoved = e.getChildren()[0];
//    }
//
//    @Override
//    public void treeStructureChanged(TreeModelEvent e) {
//
//    }
//}
