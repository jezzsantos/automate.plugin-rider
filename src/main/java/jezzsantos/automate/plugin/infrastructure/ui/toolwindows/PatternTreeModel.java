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
                return new PatternPlaceholderNode(element, element.getCodeTemplates(), AutomateBundle.message("toolWindow.Tree.Element.CodeTemplates.Title"));
            }
            if (index == AutomationIndex) {
                return new PatternPlaceholderNode(element, element.getAutomation(), AutomateBundle.message("toolWindow.Tree.Element.Automation.Title"));
            }
            if (index == AttributesIndex) {
                return new PatternPlaceholderNode(element, element.getAttributes(), AutomateBundle.message("toolWindow.Tree.Element.Attributes.Title"));
            }
            if (index == ElementsIndex) {
                return new PatternPlaceholderNode(element, element.getElements(), AutomateBundle.message("toolWindow.Tree.Element.Elements.Title"));
            }
        }

        if (parent instanceof PatternPlaceholderNode) {
            var placeholder = ((PatternPlaceholderNode) parent);
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

        if (parent instanceof PatternPlaceholderNode) {
            var placeholder = ((PatternPlaceholderNode) parent);
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
          || node instanceof PatternPlaceholderNode) {
            return false;
        }

        return node instanceof CodeTemplate
          || node instanceof Automation
          || node instanceof Attribute;
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {

        if (parent instanceof PatternElement) {
            if (child instanceof PatternPlaceholderNode) {
                var placeholder = ((PatternPlaceholderNode) child);
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

        if (parent instanceof PatternPlaceholderNode) {
            var placeholder = ((PatternPlaceholderNode) parent);
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

        var node = this.selectedPath.getLastPathComponent();

        if (node instanceof PatternElement) {
            var element = (PatternElement) node;
            var newIndex = addAttribute(element, attribute);

            var placeholder = (PatternPlaceholderNode) getChild(element, AttributesIndex);
            var placeholderPath = TreePathUtil.createTreePath(this.selectedPath, placeholder);
            treeNodesInserted(placeholderPath, new int[]{newIndex}, new Object[]{attribute});
        }
        else {
            if (node instanceof PatternPlaceholderNode) {
                var placeholder = (PatternPlaceholderNode) node;
                if (isAttributesPlaceholder(placeholder)) {
                    var placeholderPath = this.selectedPath;
                    var element = (PatternElement) placeholderPath.getParentPath().getLastPathComponent();
                    var newIndex = addAttribute(element, attribute);
                    treeNodesInserted(placeholderPath, new int[]{newIndex}, new Object[]{attribute});
                }
            }
        }
    }

    public void deleteAttribute(@NotNull Attribute attribute) {

        if (this.selectedPath == null) {
            return;
        }

        var node = this.selectedPath.getLastPathComponent();
        if (node instanceof Attribute) {
            var placeholderPath = this.selectedPath.getParentPath();
            var element = (PatternElement) placeholderPath.getParentPath().getLastPathComponent();
            var oldIndex = getIndexOfAttribute(element, attribute);
            element.removeAttribute(attribute);
            treeNodesRemoved(placeholderPath, new int[]{oldIndex}, new Object[]{attribute});
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
    private <T> int getIndexOfChild(PatternPlaceholderNode placeholder, Object child, Class<T> kind) {

        if (kind.isInstance(child)) {
            var instance = (T) child;
            return ((List<T>) placeholder.getChild()).indexOf(instance);
        }

        return -1;
    }

    private boolean isCodeTemplatesPlaceholder(PatternPlaceholderNode placeholder) {

        return placeholder.getChild() == placeholder.getParent().getCodeTemplates();
    }

    private boolean isAutomationPlaceholder(PatternPlaceholderNode placeholder) {

        return placeholder.getChild() == placeholder.getParent().getAutomation();
    }

    private boolean isAttributesPlaceholder(PatternPlaceholderNode placeholder) {

        return placeholder.getChild() == placeholder.getParent().getAttributes();
    }

    private boolean isElementsPlaceholder(PatternPlaceholderNode placeholder) {

        return placeholder.getChild() == placeholder.getParent().getElements();
    }

    private int getIndexOfAttribute(PatternElement element, Attribute attribute) {

        return element.getAttributes().indexOf(attribute);

    }
}

