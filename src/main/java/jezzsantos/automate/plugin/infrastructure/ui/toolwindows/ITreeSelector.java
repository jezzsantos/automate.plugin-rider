package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import javax.swing.tree.TreePath;

public interface ITreeSelector {

    void selectAndExpandPath(TreePath path);

    void selectPath(TreePath path);
}
