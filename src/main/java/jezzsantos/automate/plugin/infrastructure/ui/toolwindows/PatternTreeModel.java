package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import com.intellij.util.ui.tree.AbstractTreeModel;
import jezzsantos.automate.plugin.application.interfaces.patterns.*;
import jezzsantos.automate.plugin.common.AutomateBundle;
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

    public void setSelectedPath(@Nullable TreePath path) {

        this.selectedPath = path;
    }

    public void resetSelectedPath() {

        this.selectedPath = null;
    }

    public void insertCodeTemplate(@NotNull CodeTemplate codeTemplate, @Nullable Automation automation) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();

        if (selectedTreeNode instanceof PatternElement patternElementNode) {
            var patternElementTreePath = this.selectedPath;
            var indexOfCodeTemplate = addCodeTemplate(patternElementNode, codeTemplate);
            insertTreeNode(patternElementNode, patternElementTreePath, CodeTemplatesIndex, codeTemplate, indexOfCodeTemplate, true);

            if (automation != null) {
                var indexOfAutomation = addAutomation(patternElementNode, automation);
                insertTreeNode(patternElementNode, patternElementTreePath, AutomationIndex, automation, indexOfAutomation, false);
            }
        }
        else {
            if (selectedTreeNode instanceof PatternFolderPlaceholderNode placeholderNode) {
                if (isCodeTemplatesPlaceholder(placeholderNode)) {
                    var codeTemplatesFolderTreePath = this.selectedPath;
                    var patternElementTreePath = codeTemplatesFolderTreePath.getParentPath();
                    var patternElementNode = (PatternElement) patternElementTreePath.getLastPathComponent();
                    var indexOfCodeTemplate = addCodeTemplate(patternElementNode, codeTemplate);
                    insertTreeNode(placeholderNode, codeTemplatesFolderTreePath, codeTemplate, indexOfCodeTemplate, true);

                    if (automation != null) {
                        var indexOfAutomation = addAutomation(patternElementNode, automation);
                        insertTreeNode(patternElementNode, patternElementTreePath, AutomationIndex, automation, indexOfAutomation, false);
                    }
                }
            }
        }
    }

    public void deleteCodeTemplate(@NotNull CodeTemplate codeTemplate) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();

        if (selectedTreeNode instanceof CodeTemplate) {
            var codeTemplateFolderTreePath = this.selectedPath.getParentPath();
            if (codeTemplateFolderTreePath != null) {
                var codeTemplatesFolderNode = (PatternFolderPlaceholderNode) codeTemplateFolderTreePath.getLastPathComponent();
                var patternElementNode = (PatternElement) codeTemplateFolderTreePath.getParentPath().getLastPathComponent();
                var indexOfCodeTemplate = getIndexOfCodeTemplate(patternElementNode, codeTemplate);
                if (indexOfCodeTemplate > NO_INDEX) {
                    patternElementNode.removeCodeTemplate(codeTemplate);
                    treeNodesRemoved(codeTemplateFolderTreePath, new int[]{indexOfCodeTemplate}, new Object[]{codeTemplate});
                    selectNextSiblingOrParent(codeTemplateFolderTreePath, codeTemplatesFolderNode, indexOfCodeTemplate);
                }

                //TODO: need to refresh the automation collection (since deleting the codetemplate could have deleted some automations)
                //problem is that in memory we dont know that the data behind the patternElementNode has been changed by the automate CLI (in unknown ways)!

                var patternElementTreePath = codeTemplateFolderTreePath.getParentPath();
                var automationFolderTreePath = patternElementTreePath.pathByAddingChild(getChild(patternElementNode, AutomationIndex));
                var automations = patternElementNode.getAutomation();
                var indexesOfAllAutomations = createArrayOfIndexes(automations.size());
                treeStructureChanged(automationFolderTreePath, indexesOfAllAutomations, automations.toArray());
            }
        }
    }

    public void insertAutomation(@NotNull Automation automation) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();

        if (selectedTreeNode instanceof PatternElement patternElementNode) {
            var patternElementTreePath = this.selectedPath;
            var indexOfAutomation = addAutomation(patternElementNode, automation);
            insertTreeNode(patternElementNode, patternElementTreePath, AutomationIndex, automation, indexOfAutomation, true);
        }
        else {
            if (selectedTreeNode instanceof PatternFolderPlaceholderNode placeholderNode) {
                if (isAutomationPlaceholder(placeholderNode)) {
                    var automationsFolderTreePath = this.selectedPath;
                    var patternElementNode = (PatternElement) automationsFolderTreePath.getParentPath().getLastPathComponent();
                    var indexOfAutomation = addAutomation(patternElementNode, automation);
                    insertTreeNode(placeholderNode, automationsFolderTreePath, automation, indexOfAutomation, true);
                }
            }
        }
    }

    public void deleteAutomation(@NotNull Automation automation) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();

        if (selectedTreeNode instanceof Automation) {
            var automationFolderTreePath = this.selectedPath.getParentPath();
            if (automationFolderTreePath != null) {
                var automationsFolderNode = (PatternFolderPlaceholderNode) automationFolderTreePath.getLastPathComponent();
                var patternElementNode = (PatternElement) automationFolderTreePath.getParentPath().getLastPathComponent();
                var indexOfAutomation = getIndexOfAutomation(patternElementNode, automation);
                if (indexOfAutomation > NO_INDEX) {
                    patternElementNode.removeAutomation(automation);
                    treeNodesRemoved(automationFolderTreePath, new int[]{indexOfAutomation}, new Object[]{automation});
                    selectNextSiblingOrParent(automationFolderTreePath, automationsFolderNode, indexOfAutomation);
                }

                //TODO: need to refresh the automation collection (since deleting an automation could have changed some other automations)
                //TODO: similar to deleteCodeTemplate()
            }
        }
    }

    public void updateAutomation(@NotNull Automation automation) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();

        if (selectedTreeNode instanceof Automation) {
            var automationFolderTreePath = this.selectedPath.getParentPath();
            if (automationFolderTreePath != null) {
                var automationFolderNode = (PatternFolderPlaceholderNode) automationFolderTreePath.getLastPathComponent();
                var patternElementNode = (PatternElement) automationFolderTreePath.getParentPath().getLastPathComponent();
                var indexOfAutomation = updateAutomation(patternElementNode, automation);
                var automations = patternElementNode.getAutomation();
                var indexesOfAllAutomations = createArrayOfIndexes(automations.size());
                treeNodesChanged(automationFolderTreePath, indexesOfAllAutomations, automations.toArray());
                if (indexOfAutomation > NO_INDEX) {
                    selectTreeNode(automationFolderTreePath, getChild(automationFolderNode, indexOfAutomation));
                }
            }
        }
    }

    public void insertAttribute(@NotNull Attribute attribute) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();

        if (selectedTreeNode instanceof PatternElement patternElementNode) {
            var patternElementTreePath = this.selectedPath;
            var indexOfAttribute = addAttribute(patternElementNode, attribute);
            insertTreeNode(patternElementNode, patternElementTreePath, AttributesIndex, attribute, indexOfAttribute, true);
        }
        else {
            if (selectedTreeNode instanceof PatternFolderPlaceholderNode placeholderNode) {
                if (isAttributesPlaceholder(placeholderNode)) {
                    var attributesFolderTreePath = this.selectedPath;
                    var patternElementNode = (PatternElement) attributesFolderTreePath.getParentPath().getLastPathComponent();
                    var indexOfAttribute = addAttribute(patternElementNode, attribute);
                    insertTreeNode(placeholderNode, attributesFolderTreePath, attribute, indexOfAttribute, true);
                }
            }
        }
    }

    public void updateAttribute(@NotNull Attribute attribute) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();

        if (selectedTreeNode instanceof Attribute) {
            var attributesFolderTreePath = this.selectedPath.getParentPath();
            if (attributesFolderTreePath != null) {
                var attributesFolderNode = (PatternFolderPlaceholderNode) attributesFolderTreePath.getLastPathComponent();
                var patternElementNode = (PatternElement) attributesFolderTreePath.getParentPath().getLastPathComponent();
                var indexOfAttribute = updateAttribute(patternElementNode, attribute);
                var attributes = patternElementNode.getAttributes();
                var indexesOfAllAttributes = createArrayOfIndexes(attributes.size());
                treeNodesChanged(attributesFolderTreePath, indexesOfAllAttributes, attributes.toArray());
                if (indexOfAttribute > NO_INDEX) {
                    selectTreeNode(attributesFolderTreePath, getChild(attributesFolderNode, indexOfAttribute));
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
            var attributesFolderTreePath = this.selectedPath.getParentPath();
            if (attributesFolderTreePath != null) {
                var attributesFolderNode = (PatternFolderPlaceholderNode) attributesFolderTreePath.getLastPathComponent();
                var patternElementNode = (PatternElement) attributesFolderTreePath.getParentPath().getLastPathComponent();
                var indexOfAttribute = getIndexOfAttribute(patternElementNode, attribute);
                if (indexOfAttribute > NO_INDEX) {
                    patternElementNode.removeAttribute(attribute);
                    treeNodesRemoved(attributesFolderTreePath, new int[]{indexOfAttribute}, new Object[]{attribute});
                    selectNextSiblingOrParent(attributesFolderTreePath, attributesFolderNode, indexOfAttribute);
                }
            }
        }
    }

    public void insertElement(@NotNull PatternElement element) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();

        if (selectedTreeNode instanceof PatternElement patternElementNode) {
            var patternElementTreePath = this.selectedPath;
            var indexOfElement = addElement(patternElementNode, element);
            insertTreeNode(patternElementNode, patternElementTreePath, ElementsIndex, element, indexOfElement, true);
        }
        else {
            if (selectedTreeNode instanceof PatternFolderPlaceholderNode placeholderNode) {
                if (isElementsPlaceholder(placeholderNode)) {
                    var elementsFolderTreePath = this.selectedPath;
                    var patternElementNode = (PatternElement) elementsFolderTreePath.getParentPath().getLastPathComponent();
                    var indexOfElement = addElement(patternElementNode, element);
                    insertTreeNode(placeholderNode, elementsFolderTreePath, element, indexOfElement, true);
                }
            }
        }
    }

    public void updatePattern(@NotNull PatternElement pattern) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();
        if (selectedTreeNode instanceof PatternElement patternElement) {
            if (patternElement.isRoot()) {
                updateRootElement(pattern);
                treeNodesChanged(this.selectedPath, null, null);
            }
        }
    }

    public void updateElement(@NotNull PatternElement element) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();

        if (selectedTreeNode instanceof PatternElement) {
            var elementsFolderTreePath = this.selectedPath.getParentPath();
            if (elementsFolderTreePath != null) {
                var elementsFolderNode = (PatternFolderPlaceholderNode) elementsFolderTreePath.getLastPathComponent();
                var patternElementNode = (PatternElement) elementsFolderTreePath.getParentPath().getLastPathComponent();
                var indexOfElement = updateElement(patternElementNode, element);
                var elements = patternElementNode.getElements();
                var indexesOfAllElements = createArrayOfIndexes(elements.size());
                treeNodesChanged(elementsFolderTreePath, indexesOfAllElements, elements.toArray());
                if (indexOfElement > NO_INDEX) {
                    selectTreeNode(elementsFolderTreePath, getChild(elementsFolderNode, indexOfElement));
                }
            }
        }
    }

    public void deleteElement(@NotNull PatternElement element) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();

        if (selectedTreeNode instanceof PatternElement) {
            var elementsFolderTreePath = this.selectedPath.getParentPath();
            if (elementsFolderTreePath != null) {
                var elementsFolderNode = (PatternFolderPlaceholderNode) elementsFolderTreePath.getLastPathComponent();
                var patternElementNode = (PatternElement) elementsFolderTreePath.getParentPath().getLastPathComponent();
                var indexOfElement = getIndexOfElement(patternElementNode, element);
                if (indexOfElement > NO_INDEX) {
                    patternElementNode.removeElement(element);
                    treeNodesRemoved(elementsFolderTreePath, new int[]{indexOfElement}, new Object[]{element});
                    selectNextSiblingOrParent(elementsFolderTreePath, elementsFolderNode, indexOfElement);
                }
            }
        }
    }

    private void insertTreeNode(@NotNull PatternElement patternElementNode, @NotNull TreePath patternElementTreePath, int childIndex, @NotNull Object underlyingObject, int indexOfNewObjectInCollection, boolean shouldSelectNode) {

        if (this.selectedPath == null) {
            return;
        }

        var placeholderNode = (PatternFolderPlaceholderNode) getChild(patternElementNode, childIndex);
        var placeholderTreePath = patternElementTreePath.pathByAddingChild(placeholderNode);
        insertTreeNode(placeholderNode, placeholderTreePath, underlyingObject, indexOfNewObjectInCollection, shouldSelectNode);
    }

    private void insertTreeNode(@NotNull PatternFolderPlaceholderNode placeHolderNode, @NotNull TreePath placeholderTreePath, @NotNull Object newObject, int indexOfNewObjectInCollection, boolean shouldSelectNode) {

        if (this.selectedPath == null) {
            return;
        }

        if (indexOfNewObjectInCollection > NO_INDEX) {
            treeNodesInserted(placeholderTreePath, new int[]{indexOfNewObjectInCollection}, new Object[]{newObject});
            if (shouldSelectNode) {
                var treeNode = getChild(placeHolderNode, indexOfNewObjectInCollection);
                selectTreeNode(placeholderTreePath, treeNode);
            }
        }
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

    private void selectTreeNode(@NotNull TreePath parentTreePath, @NotNull Object treeNode) {

        var treeNodePath = Objects.requireNonNull(parentTreePath).pathByAddingChild(treeNode);
        if (treeNodePath != null) {
            this.treeSelector.selectPath(treeNodePath);
        }
    }

    private int addAutomation(@NotNull PatternElement parent, @NotNull Automation automation) {

        parent.addAutomation(automation);
        return getIndexOfAutomation(parent, automation);
    }

    private int updateAutomation(@NotNull PatternElement parent, @NotNull Automation automation) {

        parent.updateAutomation(automation);
        return parent.getAutomation().indexOf(automation);
    }

    private int addCodeTemplate(@NotNull PatternElement parent, @NotNull CodeTemplate codeTemplate) {

        parent.addCodeTemplate(codeTemplate);
        return getIndexOfCodeTemplate(parent, codeTemplate);
    }

    private int addAttribute(@NotNull PatternElement parent, @NotNull Attribute attribute) {

        parent.addAttribute(attribute);
        return getIndexOfAttribute(parent, attribute);
    }

    private int updateAttribute(@NotNull PatternElement parent, @NotNull Attribute attribute) {

        parent.updateAttribute(attribute);
        return parent.getAttributes().indexOf(attribute);
    }

    private int addElement(@NotNull PatternElement parent, @NotNull PatternElement element) {

        parent.addElement(element);
        return getIndexOfElement(parent, element);
    }

    private int updateElement(@NotNull PatternElement parent, @NotNull PatternElement element) {

        parent.updateElement(element);
        return parent.getElements().indexOf(element);
    }

    private void updateRootElement(@NotNull PatternElement pattern) {

        this.pattern.setPattern(pattern);
    }

    private @Nullable Object getItemAtIndex(@NotNull List<?> list, int index) {

        if (list.size() > index) {
            return list.get(index);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> int getIndexOfChild(@NotNull PatternFolderPlaceholderNode placeholder, @NotNull Object child, @NotNull Class<T> kind) {

        if (kind.isInstance(child)) {
            var instance = (T) child;
            return ((List<T>) placeholder.getChild()).indexOf(instance);
        }

        return NO_INDEX;
    }

    private boolean isCodeTemplatesPlaceholder(@NotNull PatternFolderPlaceholderNode placeholder) {

        return placeholder.getChild() == placeholder.getParent().getCodeTemplates();
    }

    private boolean isAutomationPlaceholder(@NotNull PatternFolderPlaceholderNode placeholder) {

        return placeholder.getChild() == placeholder.getParent().getAutomation();
    }

    private boolean isAttributesPlaceholder(@NotNull PatternFolderPlaceholderNode placeholder) {

        return placeholder.getChild() == placeholder.getParent().getAttributes();
    }

    private boolean isElementsPlaceholder(@NotNull PatternFolderPlaceholderNode placeholder) {

        return placeholder.getChild() == placeholder.getParent().getElements();
    }

    private int getIndexOfAttribute(@NotNull PatternElement parent, @NotNull Attribute attribute) {

        return parent.getAttributes().indexOf(attribute);
    }

    private int getIndexOfCodeTemplate(@NotNull PatternElement parent, @NotNull CodeTemplate codeTemplate) {

        return parent.getCodeTemplates().indexOf(codeTemplate);
    }

    private int getIndexOfAutomation(@NotNull PatternElement parent, @NotNull Automation automation) {

        return parent.getAutomation().indexOf(automation);
    }

    private int getIndexOfElement(@NotNull PatternElement parent, @NotNull PatternElement element) {

        return parent.getElements().indexOf(element);
    }

    private int[] createArrayOfIndexes(int size) {

        if (size == 0) {
            return new int[0];
        }

        return IntStream.range(0, size).toArray();
    }
}

