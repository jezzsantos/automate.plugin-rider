package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import com.intellij.util.ui.tree.AbstractTreeModel;
import jezzsantos.automate.plugin.application.interfaces.patterns.*;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreePath;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class PatternTreeModel extends AbstractTreeModel {

    private static final int NO_INDEX = -1;
    private static final int CodeTemplatesIndex = 0;
    private static final int AutomationIndex = 1;
    private static final int AttributesIndex = 2;
    private static final int ElementsIndex = 3;
    @NotNull
    private final PatternDetailed pattern;
    private final ITreeSelector treeSelector;
    @Nullable
    private TreePath selectedPath;

    public PatternTreeModel(@NotNull ITreeSelector treeSelector, @NotNull PatternDetailed pattern) {

        this.treeSelector = treeSelector;
        this.pattern = pattern;
    }

    public void setSelectedPath(@Nullable TreePath path) {

        this.selectedPath = path;
    }

    public void resetSelectedPath() {

        this.selectedPath = null;
    }

    public void insertAttribute(@NotNull Attribute attribute) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();

        if (selectedTreeNode instanceof PatternElement selectedElementTreeNode) {
            var indexOfAttribute = addAttribute(selectedElementTreeNode, attribute);
            var parentFolderTreeNode = (PatternFolderPlaceholderNode) getChild(selectedElementTreeNode, AttributesIndex);
            var parentFolderTreeNodePath = this.selectedPath.pathByAddingChild(parentFolderTreeNode);
            if (indexOfAttribute > NO_INDEX) {
                treeNodesInserted(parentFolderTreeNodePath, new int[]{indexOfAttribute}, new Object[]{attribute});
                selectTreeNode(this.selectedPath, getChild(parentFolderTreeNode, indexOfAttribute));
            }
        }
        else {
            if (selectedTreeNode instanceof PatternFolderPlaceholderNode selectedFolderTreeNode) {
                if (isAttributesPlaceholder(selectedFolderTreeNode)) {
                    var parentTreeNodePath = this.selectedPath;
                    var parentElement = (PatternElement) parentTreeNodePath.getParentPath().getLastPathComponent();
                    var indexOfAttribute = addAttribute(parentElement, attribute);
                    if (indexOfAttribute > NO_INDEX) {
                        treeNodesInserted(parentTreeNodePath, new int[]{indexOfAttribute}, new Object[]{attribute});
                        selectTreeNode(this.selectedPath, getChild(selectedFolderTreeNode, indexOfAttribute));
                    }
                }
            }
        }
    }

    public void updateAttribute(Attribute attribute) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();
        if (selectedTreeNode instanceof Attribute) {
            var parentFolderTreeNodePath = this.selectedPath.getParentPath();
            if (parentFolderTreeNodePath != null) {
                var parentFolderTreeNode = (PatternFolderPlaceholderNode) parentFolderTreeNodePath.getLastPathComponent();
                var patternElementTreeNode = (PatternElement) parentFolderTreeNodePath.getParentPath().getLastPathComponent();
                var indexOfAttribute = updateAttribute(patternElementTreeNode, attribute);
                var attributes = patternElementTreeNode.getAttributes();
                var indexesOfAllAttributes = createArrayOfIndexes(attributes.size());
                treeNodesChanged(parentFolderTreeNodePath, indexesOfAllAttributes, attributes.toArray());
                if (indexOfAttribute > NO_INDEX) {
                    selectTreeNode(parentFolderTreeNodePath, getChild(parentFolderTreeNode, indexOfAttribute));
                }
            }
        }
    }

    public void deleteAttribute(@NotNull Attribute attribute) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();
        if (selectedTreeNode instanceof Attribute) {
            var parentFolderTreeNodePath = this.selectedPath.getParentPath();
            if (parentFolderTreeNodePath != null) {
                var parentFolderTreeNode = (PatternFolderPlaceholderNode) parentFolderTreeNodePath.getLastPathComponent();
                var patternElementTreeNode = (PatternElement) parentFolderTreeNodePath.getParentPath().getLastPathComponent();
                var indexOfAttribute = getIndexOfAttribute(patternElementTreeNode, attribute);
                if (indexOfAttribute > NO_INDEX) {
                    patternElementTreeNode.removeAttribute(attribute);
                    treeNodesRemoved(parentFolderTreeNodePath, new int[]{indexOfAttribute}, new Object[]{attribute});
                    selectNextSiblingOrParent(parentFolderTreeNodePath, parentFolderTreeNode, indexOfAttribute);
                }
            }
        }
    }

    @Override
    public Object getRoot() {

        return this.pattern.getPattern();
    }

    @Override
    public Object getChild(Object parent, int index) {

        if (parent instanceof PatternElement element) {
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

        if (parent instanceof PatternFolderPlaceholderNode placeholder) {
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

        if (parent instanceof PatternFolderPlaceholderNode placeholder) {
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
            if (child instanceof PatternFolderPlaceholderNode placeholder) {
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

        if (parent instanceof PatternFolderPlaceholderNode placeholder) {
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

        return NO_INDEX;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object value) {

    }

    private void selectNextSiblingOrParent(TreePath parentTreeNodePath, PatternFolderPlaceholderNode parentFolderTreeNode, int indexOfDeletedAttribute) {

        var siblingCount = getChildCount(parentFolderTreeNode);
        if (siblingCount == 0) {
            this.treeSelector.selectPath(parentTreeNodePath);
        }
        else {
            if (siblingCount > indexOfDeletedAttribute) {
                var nextSiblingTreeNodePath = parentTreeNodePath.pathByAddingChild(getChild(parentFolderTreeNode, indexOfDeletedAttribute));
                this.treeSelector.selectPath(nextSiblingTreeNodePath);
            }
            else {
                var lastSiblingTreeNode = getChild(parentFolderTreeNode, siblingCount - 1);
                var lastSiblingTreeNodePath = parentTreeNodePath.pathByAddingChild(lastSiblingTreeNode);
                this.treeSelector.selectPath(lastSiblingTreeNodePath);
            }
        }
    }

    private void selectTreeNode(TreePath selectedPath, Object treeNode) {

        var newTreeNodePath = Objects.requireNonNull(selectedPath).pathByAddingChild(treeNode);
        if (newTreeNodePath != null) {
            this.treeSelector.selectPath(newTreeNodePath);
        }
    }

    private int addAttribute(PatternElement element, Attribute attribute) {

        element.addAttribute(attribute);
        return getIndexOfAttribute(element, attribute);
    }

    private int updateAttribute(PatternElement element, Attribute attribute) {

        element.updateAttribute(attribute);
        return element.getAttributes().indexOf(attribute);
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

        return NO_INDEX;
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

    private int[] createArrayOfIndexes(int size) {

        if (size == 0) {
            return new int[0];
        }

        return IntStream.range(0, size).toArray();
    }
}

