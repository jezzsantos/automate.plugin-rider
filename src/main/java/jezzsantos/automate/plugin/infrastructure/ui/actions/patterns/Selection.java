package jezzsantos.automate.plugin.infrastructure.ui.actions.patterns;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.PatternFolderPlaceholderNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreePath;

public class Selection {

    @Nullable
    public static PatternElement isPattern(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof PatternElement pattern) {
                    if (pattern.isRoot()) {
                        return pattern;
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public static PatternElement isChildElementOrRootOrPlaceholder(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof PatternElement patternElement) {
                    return patternElement;
                }
                else {
                    if (leaf instanceof PatternFolderPlaceholderNode placeholder) {
                        return (placeholder.getChild() == placeholder.getParent().getElements())
                          ? placeholder.getParent()
                          : null;
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public static SelectedElement isChildElementOrRoot(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof PatternElement patternElement) {
                    if (patternElement.isRoot()) {
                        return new SelectedElement(patternElement, patternElement);
                    }
                    else {
                        var parent = path.getParentPath().getParentPath().getLastPathComponent();
                        if (parent instanceof PatternElement parentElement) {
                            return new SelectedElement(parentElement, patternElement);
                        }
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public static SelectedElement isChildElementAndNotRoot(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof PatternElement patternElement) {
                    if (patternElement.isRoot()) {
                        return null;
                    }
                    else {
                        var parent = path.getParentPath().getParentPath().getLastPathComponent();
                        if (parent instanceof PatternElement parentElement) {
                            return new SelectedElement(parentElement, patternElement);
                        }
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    public static SelectedAttribute isAttribute(@NotNull AnActionEvent e) {

        var selection = e.getData(PlatformCoreDataKeys.SELECTED_ITEM);
        if (selection != null) {
            if (selection instanceof TreePath path) {
                var leaf = path.getLastPathComponent();
                if (leaf instanceof Attribute attribute) {
                    var parent = path.getParentPath().getParentPath().getLastPathComponent();
                    if (parent instanceof PatternElement parentElement) {
                        return new SelectedAttribute(parentElement, attribute);
                    }
                }
            }
        }

        return null;
    }

    static class SelectedAttribute {

        private final Attribute attribute;
        private final PatternElement parent;

        public SelectedAttribute(@NotNull PatternElement parent, @NotNull Attribute attribute) {

            this.parent = parent;
            this.attribute = attribute;
        }

        public PatternElement getParent() {return this.parent;}

        public Attribute getAttribute() {return this.attribute;}
    }

    static class SelectedElement {

        private final PatternElement element;
        private final PatternElement parent;

        public SelectedElement(@NotNull PatternElement parent, @NotNull PatternElement element) {

            this.parent = parent;
            this.element = element;
        }

        public PatternElement getParent() {return this.parent;}

        public PatternElement getElement() {return this.element;}
    }
}
