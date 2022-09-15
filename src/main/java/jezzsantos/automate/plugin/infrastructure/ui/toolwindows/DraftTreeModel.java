package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import com.intellij.util.ui.tree.AbstractTreeModel;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElement;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DraftTreeModel extends AbstractTreeModel {

    private static final int NO_INDEX = -1;
    @NotNull
    private final DraftElementPlaceholderNode draft;
    @NotNull
    private final PatternElement pattern;
    @Nullable
    private TreePath selectedPath;

    public DraftTreeModel(@NotNull DraftElement draft, @NotNull PatternElement pattern) {

        this.draft = new DraftElementPlaceholderNode(pattern, draft, false);
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

        if (selectedTreeNode instanceof DraftElementPlaceholderNode) {
            var selectedElementTreeNode = (DraftElementPlaceholderNode) selectedTreeNode;
            var indexOfElementOfParent = addElement(selectedElementTreeNode, element, isCollection);
            if (indexOfElementOfParent > NO_INDEX) {
                treeNodesInserted(this.selectedPath, new int[]{indexOfElementOfParent}, new Object[]{element});
            }
        }
    }

    public void updateDraftElement(DraftElement element) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();
        if (selectedTreeNode instanceof DraftElementPlaceholderNode) {
            var selectedElementTreeNode = (DraftElementPlaceholderNode) selectedTreeNode;
            selectedElementTreeNode.updateElement(element);
            var properties = element.getProperties();
            var indexesOfAllProperties = createArrayOfIndexes(properties.size());
            var allProperties = new ArrayList<DraftPropertyPlaceholderNode>();
            properties.forEach(property -> allProperties.add(new DraftPropertyPlaceholderNode(property)));
            treeNodesChanged(this.selectedPath, indexesOfAllProperties, allProperties.toArray());
        }
    }

    public void deleteDraftElement(@NotNull DraftElement element) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedTreeNode = this.selectedPath.getLastPathComponent();
        if (selectedTreeNode instanceof DraftElementPlaceholderNode) {
            var selectedElementTreeNode = (DraftElementPlaceholderNode) selectedTreeNode;
            if (selectedElementTreeNode.getElement().isNotRoot()) {
                var parentTreeNodePath = this.selectedPath.getParentPath();
                if (parentTreeNodePath != null) {
                    var parentElementTreeNode = ((DraftElementPlaceholderNode) parentTreeNodePath.getLastPathComponent());
                    var indexOfElementOfParent = getIndexOfChild(parentElementTreeNode, selectedElementTreeNode);
                    if (indexOfElementOfParent > NO_INDEX) {
                        parentElementTreeNode.getElement().removeElement(element);
                        treeNodesRemoved(parentTreeNodePath, new int[]{indexOfElementOfParent}, new Object[]{selectedElementTreeNode});
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
            if (child instanceof DraftPropertyPlaceholderNode) {
                var placeholder = (DraftPropertyPlaceholderNode) child;
                var placeholderProperty = placeholder.getProperty();

                for (var propertyOfElement : elementProperties) {

                    if (propertyOfElement.equals(placeholderProperty)) {
                        return relativeIndex;
                    }
                    relativeIndex++;
                }

                return NO_INDEX;
            }

            if (child instanceof DraftElementPlaceholderNode) {
                var placeholder = (DraftElementPlaceholderNode) child;
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
                                     .collect(Collectors.toList()));
        }

        return collectionItems;
    }
}
