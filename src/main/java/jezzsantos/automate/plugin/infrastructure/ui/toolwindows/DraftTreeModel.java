package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import com.intellij.util.ui.tree.AbstractTreeModel;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftDetailed;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElement;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class DraftTreeModel extends AbstractTreeModel {

    private static final int NO_INDEX = -1;
    @NotNull
    private final Object draft;
    @NotNull
    private final PatternElement pattern;
    @NotNull
    private final ITreeSelector treeSelector;
    @Nullable
    private TreePath selectedPath;

    public DraftTreeModel(@NotNull ITreeSelector treeSelector, @NotNull DraftDetailed draft, @NotNull PatternElement pattern) {

        this.treeSelector = treeSelector;
        this.draft = draft.mustBeUpgraded()
          ? new DraftMustBeUpgradedPlaceholderNode(draft.getName(), draft.getUpgradeInfo())
          : new DraftElementPlaceholderNode(pattern, draft.getRoot(), false);
        this.pattern = pattern;
    }

    public void setSelectedPath(@Nullable TreePath path) {

        this.selectedPath = path;
    }

    public void resetSelectedPath() {

        this.selectedPath = null;
    }

    public void insertDraftElement(@NotNull DraftElement element, boolean isCollection) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();

        if (selectedTreeNode instanceof DraftElementPlaceholderNode selectedElementTreeNode) {
            var indexOfElement = addElement(selectedElementTreeNode, element, isCollection);
            if (indexOfElement > NO_INDEX) {
                treeNodesInserted(this.selectedPath, new int[]{indexOfElement}, new Object[]{element});
                selectTreeNode(this.selectedPath, getChild(selectedElementTreeNode, indexOfElement));
            }
        }
    }

    public void updateDraftElement(DraftElement element) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();
        if (selectedTreeNode instanceof DraftElementPlaceholderNode selectedElementTreeNode) {
            selectedElementTreeNode.updateElement(element);
            var properties = element.getProperties();
            var indexesOfAllProperties = createArrayOfIndexes(properties.size());
            var allProperties = new ArrayList<DraftPropertyPlaceholderNode>();
            properties.forEach(property -> allProperties.add(new DraftPropertyPlaceholderNode(property)));
            treeNodesChanged(this.selectedPath, indexesOfAllProperties, allProperties.toArray());
            selectTreeNode(this.selectedPath);
        }
    }

    public void deleteDraftElement(@NotNull DraftElement element) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();
        if (selectedTreeNode instanceof DraftElementPlaceholderNode selectedElementTreeNode) {
            if (selectedElementTreeNode.getElement().isNotRoot()) {
                var parentTreeNodePath = this.selectedPath.getParentPath();
                if (parentTreeNodePath != null) {
                    var parentElementTreeNode = ((DraftElementPlaceholderNode) parentTreeNodePath.getLastPathComponent());
                    var indexOfElement = getIndexOfChild(parentElementTreeNode, selectedElementTreeNode);
                    if (indexOfElement > NO_INDEX) {
                        if (selectedElementTreeNode.isCollectionItem()) {
                            parentElementTreeNode.getElement().deleteDescendantCollectionItem(Objects.requireNonNull(element.getId()));
                        }
                        else {
                            parentElementTreeNode.getElement().deleteElement(Objects.requireNonNull(element.getId()));
                        }
                        treeNodesRemoved(parentTreeNodePath, new int[]{indexOfElement}, new Object[]{selectedElementTreeNode});
                        selectNextSiblingOrParent(parentTreeNodePath, parentElementTreeNode, indexOfElement);
                    }
                }
            }
        }
    }

    @Override
    public Object getRoot() {

        return this.draft;
    }

    @Override
    public Object getChild(Object parent, int index) {

        if (parent instanceof DraftElementPlaceholderNode) {
            var draftElement = ((DraftElementPlaceholderNode) parent).getElement();

            var relativeIndex = index;
            var properties = draftElement.getProperties();
            if (relativeIndex < properties.size()) {
                var property = Objects.requireNonNull(properties.get(relativeIndex));
                return new DraftPropertyPlaceholderNode(property);
            }
            relativeIndex = relativeIndex - properties.size();
            var collectedItems = getCollectedItemsFromAllCollections(draftElement);
            if (relativeIndex < collectedItems.size()) {
                var item = collectedItems.get(relativeIndex);
                return new DraftElementPlaceholderNode(this.pattern, item, true);
            }

            relativeIndex = relativeIndex - collectedItems.size();
            var elements = draftElement.getElements();
            if (relativeIndex < elements.size()) {
                var element = Objects.requireNonNull(elements.get(relativeIndex));
                return new DraftElementPlaceholderNode(this.pattern, element, false);
            }
        }

        return null;
    }

    @Override
    public int getChildCount(Object parent) {

        if (parent instanceof DraftElementPlaceholderNode) {
            var draftElement = ((DraftElementPlaceholderNode) parent).getElement();

            var numberOfProperties = draftElement.getProperties().size();
            var numberOfCollectedItems = getCollectedItemsFromAllCollections(draftElement).size();
            var numberOfElements = draftElement.getElements().size();

            return numberOfProperties + numberOfCollectedItems + numberOfElements;
        }

        return 0;
    }

    @Override
    public boolean isLeaf(Object node) {

        return !(node instanceof DraftElementPlaceholderNode);
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {

        if (parent instanceof DraftElementPlaceholderNode) {
            var parentElement = ((DraftElementPlaceholderNode) parent).getElement();

            var elementProperties = parentElement.getProperties();
            var relativeIndex = 0;
            if (child instanceof DraftPropertyPlaceholderNode placeholder) {
                var placeholderProperty = placeholder.getProperty();

                for (var propertyOfElement : elementProperties) {

                    if (propertyOfElement.equals(placeholderProperty)) {
                        return relativeIndex;
                    }
                    relativeIndex++;
                }

                return NO_INDEX;
            }

            if (child instanceof DraftElementPlaceholderNode placeholder) {
                var childElement = placeholder.getElement();
                relativeIndex = elementProperties.size();
                var collectedItems = getCollectedItemsFromAllCollections(parentElement);
                for (var collectedItem : collectedItems) {
                    if (collectedItem.equals(childElement)) {
                        return relativeIndex;
                    }
                    relativeIndex++;
                }

                var elements = parentElement.getElements();
                for (var element : elements) {
                    if (element.equals(childElement)) {
                        return relativeIndex;
                    }
                    relativeIndex++;
                }

                return NO_INDEX;
            }
        }

        return NO_INDEX;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object value) {

    }

    private void selectNextSiblingOrParent(TreePath parentTreeNodePath, DraftElementPlaceholderNode parentElementTreeNode, int indexOfDeletedElement) {

        var siblingCount = getChildCount(parentElementTreeNode);
        if (siblingCount == 0) {
            selectTreeNode(parentTreeNodePath);
        }
        else {
            if (siblingCount > indexOfDeletedElement) {
                var nextSiblingTreeNodePath = parentTreeNodePath.pathByAddingChild(getChild(parentElementTreeNode, indexOfDeletedElement));
                selectTreeNode(nextSiblingTreeNodePath);
            }
            else {
                var lastSiblingTreeNode = getChild(parentElementTreeNode, siblingCount - 1);
                if (lastSiblingTreeNode instanceof DraftPropertyPlaceholderNode) {
                    selectTreeNode(parentTreeNodePath);
                }
                else {
                    var lastSiblingTreeNodePath = parentTreeNodePath.pathByAddingChild(lastSiblingTreeNode);
                    selectTreeNode(lastSiblingTreeNodePath);
                }
            }
        }
    }

    private void selectTreeNode(TreePath selectedPath) {

        this.treeSelector.selectPath(selectedPath);
    }

    private void selectTreeNode(TreePath selectedPath, Object newTreeNode) {

        var newTreeNodePath = Objects.requireNonNull(selectedPath).pathByAddingChild(newTreeNode);
        if (newTreeNodePath != null) {
            this.treeSelector.selectAndExpandPath(newTreeNodePath);
        }
    }

    private int[] createArrayOfIndexes(int size) {

        if (size == 0) {
            return new int[0];
        }

        return IntStream.range(0, size).toArray();
    }

    private int addElement(DraftElementPlaceholderNode parentElement, DraftElement element, boolean isCollection) {

        parentElement.getElement().addElement(element);
        return getIndexOfChild(parentElement, new DraftElementPlaceholderNode(this.pattern, element, isCollection));
    }

    private List<DraftElement> getCollectedItemsFromAllCollections(DraftElement element) {

        var collections = element.getCollections();
        var collectionItems = new ArrayList<DraftElement>();
        for (var collection : collections.entrySet()) {
            collectionItems.addAll(collection.getValue().getCollectionItems().stream()
                                     .sorted(Comparator.comparing(item -> Objects.requireNonNullElse(item.getId(), "")))
                                     .toList());
        }

        return collectionItems;
    }
}
