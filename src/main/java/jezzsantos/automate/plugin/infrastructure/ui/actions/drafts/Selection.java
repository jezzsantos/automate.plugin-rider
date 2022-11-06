package jezzsantos.automate.plugin.infrastructure.ui.actions.drafts;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElement;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.DraftElementPlaceholderNode;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.DraftIncompatiblePlaceholderNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreePath;
import java.util.Map;

public class Selection {

    @Nullable
    public static DraftIncompatiblePlaceholderNode isIncompatibleDraft(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof DraftIncompatiblePlaceholderNode) {
                    return (DraftIncompatiblePlaceholderNode) leaf;
                }
            }
        }

        return null;
    }

    @Nullable
    public static DraftElement isRootElementOrIncompatibleDraft(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof DraftElementPlaceholderNode placeholder) {
                    var element = placeholder.getElement();
                    if (!element.isNotRoot()) {
                        return element;
                    }
                }
                if (leaf instanceof DraftIncompatiblePlaceholderNode placeholder) {
                    return new DraftElement(placeholder.getDraftName(), Map.of(), true);
                }
            }
        }

        return null;
    }

    @Nullable
    public static DraftElement isChildElementAndNotRoot(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof DraftElementPlaceholderNode placeholder) {
                    var element = placeholder.getElement();
                    if (element.isNotRoot()) {
                        return element;
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public static DraftElementPlaceholderNode isChildElementOrRoot(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof DraftElementPlaceholderNode placeholder) {
                    return placeholder;
                }
            }
        }

        return null;
    }
}
