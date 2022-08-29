package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import com.intellij.ui.tree.TreePathUtil;
import com.intellij.util.ui.tree.AbstractTreeModel;
import jezzsantos.automate.plugin.application.interfaces.patterns.*;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreePath;
import java.util.List;

public class PatternTreeModel extends AbstractTreeModel {

    private static final int CodeTemplatesIndex = 0;
    private static final int AutomationIndex = 1;
    private static final int AttributesIndex = 2;
    private static final int ElementsIndex = 3;
    @NotNull
    private final PatternDetailed pattern;
    @Nullable
    private TreePath selectedPath;

    public PatternTreeModel(@NotNull PatternDetailed pattern) {

        this.pattern = pattern;
    }

    @Override
    public Object getRoot() {

        return this.pattern.getPattern();
    }

    @Override
    public Object getChild(Object parent, int index) {

        if (parent instanceof PatternElement) {
            var element = ((PatternElement) parent);
            if (index == CodeTemplatesIndex) {
                return new PatternFolderPlaceholderNode(element, element.getCodeTemplates(), AutomateBundle.message("toolWindow.Tree.Element.CodeTemplates.Title"));
            }
            if (index == AutomationIndex) {
                return new PatternFolderPlaceholderNode(element, element.getAutomation(), AutomateBundle.message("toolWindow.Tree.Element.Automation.Title"));
            }
            if (index == AttributesIndex) {
                return new PatternFolderPlaceholderNode(element, element.getAttributes(), AutomateBundle.message("toolWindow.Tree.Element.Attributes.Title"));
            }
            if (index == ElementsIndex) {
                return new PatternFolderPlaceholderNode(element, element.getElements(), AutomateBundle.message("toolWindow.Tree.Element.Elements.Title"));
            }
        }

        if (parent instanceof PatternFolderPlaceholderNode) {
            var placeholder = ((PatternFolderPlaceholderNode) parent);
            var element = placeholder.getParent();
            if (isCodeTemplatesPlaceholder(placeholder)) {
                return getItemAtIndex(element.getCodeTemplates(), index);
            }
            if (isAutomationPlaceholder(placeholder)) {
                return getItemAtIndex(element.getAutomation(), index);
            }
            if (isAttributesPlaceholder(placeholder)) {
                return getItemAtIndex(element.getAttributes(), index);
            }
            if (isElementsPlaceholder(placeholder)) {
                return getItemAtIndex(element.getElements(), index);
            }
        }

        return null;
    }

    @Override
    public int getChildCount(Object parent) {

        if (parent instanceof PatternElement) {
            return 4;
        }

        if (parent instanceof PatternFolderPlaceholderNode) {
            var placeholder = ((PatternFolderPlaceholderNode) parent);
            var element = placeholder.getParent();
            if (isCodeTemplatesPlaceholder(placeholder)) {
                return element.getCodeTemplates().size();
            }
            if (isAutomationPlaceholder(placeholder)) {
                return element.getAutomation().size();
            }
            if (isAttributesPlaceholder(placeholder)) {
                return element.getAttributes().size();
            }
            if (isElementsPlaceholder(placeholder)) {
                return element.getElements().size();
            }
        }

        return 0;
    }

    @Override
    public boolean isLeaf(Object node) {

        if (node instanceof PatternElement
          || node instanceof PatternFolderPlaceholderNode) {
            return false;
        }

        return node instanceof CodeTemplate
          || node instanceof Automation
          || node instanceof Attribute;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {

        if (parent instanceof PatternElement) {
            if (child instanceof PatternFolderPlaceholderNode) {
                var placeholder = ((PatternFolderPlaceholderNode) child);
                if (isCodeTemplatesPlaceholder(placeholder)) {
                    return CodeTemplatesIndex;
                }
                if (isAutomationPlaceholder(placeholder)) {
                    return AutomationIndex;
                }
                if (isAttributesPlaceholder(placeholder)) {
                    return AttributesIndex;
                }
                if (isElementsPlaceholder(placeholder)) {
                    return ElementsIndex;
                }
            }
        }

        if (parent instanceof PatternFolderPlaceholderNode) {
            var placeholder = ((PatternFolderPlaceholderNode) parent);
            if (isCodeTemplatesPlaceholder(placeholder)) {
                return getIndexOfChild(placeholder, child, CodeTemplate.class);
            }
            if (isAutomationPlaceholder(placeholder)) {
                return getIndexOfChild(placeholder, child, Automation.class);
            }
            if (isAttributesPlaceholder(placeholder)) {
                return getIndexOfChild(placeholder, child, Attribute.class);
            }
            if (isElementsPlaceholder(placeholder)) {
                return getIndexOfChild(placeholder, child, PatternElement.class);
            }
        }

        return -1;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object value) {

    }

    public void insertAttribute(@NotNull Attribute attribute) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedNode = this.selectedPath.getLastPathComponent();

        if (selectedNode instanceof PatternElement) {
            var parentElement = (PatternElement) selectedNode;
            var newIndexInModel = addAttribute(parentElement, attribute);

            var folderNode = (PatternFolderPlaceholderNode) getChild(parentElement, AttributesIndex);
            var folderPath = TreePathUtil.createTreePath(this.selectedPath, folderNode);
            treeNodesInserted(folderPath, new int[]{newIndexInModel}, new Object[]{attribute});
        }
        else {
            if (selectedNode instanceof PatternFolderPlaceholderNode) {
                var folder = (PatternFolderPlaceholderNode) selectedNode;
                if (isAttributesPlaceholder(folder)) {
                    var folderPath = this.selectedPath;
                    var parentElement = (PatternElement) folderPath.getParentPath().getLastPathComponent();
                    var newIndexInModel = addAttribute(parentElement, attribute);
                    treeNodesInserted(folderPath, new int[]{newIndexInModel}, new Object[]{attribute});
                }
            }
        }
    }

    public void deleteAttribute(@NotNull Attribute attribute) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedNode = this.selectedPath.getLastPathComponent();
        if (selectedNode instanceof Attribute) {
            var folderPath = this.selectedPath.getParentPath();
            var parentElement = (PatternElement) folderPath.getParentPath().getLastPathComponent();
            var existingIndexInTree = getIndexOfAttribute(parentElement, attribute);
            parentElement.removeAttribute(attribute);
            treeNodesRemoved(folderPath, new int[]{existingIndexInTree}, new Object[]{attribute});
        }
    }

    public void setSelectedPath(@Nullable TreePath path) {

        this.selectedPath = path;
    }

    public void resetSelectedPath() {

        this.selectedPath = null;
    }

    private int addAttribute(PatternElement element, Attribute attribute) {

        element.addAttribute(attribute);
        return getIndexOfAttribute(element, attribute);
    }

    private Object getItemAtIndex(List<?> list, int index) {

        if (list.size() > index) {
            return list.get(index);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> int getIndexOfChild(PatternFolderPlaceholderNode placeholder, Object child, Class<T> kind) {

        if (kind.isInstance(child)) {
            var instance = (T) child;
            return ((List<T>) placeholder.getChild()).indexOf(instance);
        }

        return -1;
    }

    private boolean isCodeTemplatesPlaceholder(PatternFolderPlaceholderNode placeholder) {

        return placeholder.getChild() == placeholder.getParent().getCodeTemplates();
    }

    private boolean isAutomationPlaceholder(PatternFolderPlaceholderNode placeholder) {

        return placeholder.getChild() == placeholder.getParent().getAutomation();
    }

    private boolean isAttributesPlaceholder(PatternFolderPlaceholderNode placeholder) {

        return placeholder.getChild() == placeholder.getParent().getAttributes();
    }

    private boolean isElementsPlaceholder(PatternFolderPlaceholderNode placeholder) {

        return placeholder.getChild() == placeholder.getParent().getElements();
    }

    private int getIndexOfAttribute(PatternElement element, Attribute attribute) {

        return element.getAttributes().indexOf(attribute);

    }
}

