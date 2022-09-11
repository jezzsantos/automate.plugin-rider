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

public class DraftTreeModel extends AbstractTreeModel {

    @NotNull
    private final DraftElementPlaceholderNode draft;
    @NotNull
    private final PatternElement pattern;
    private TreePath selectedPath;

    public DraftTreeModel(@NotNull DraftElement draft, @NotNull PatternElement pattern) {

        this.draft = new DraftElementPlaceholderNode(pattern, draft, false, draft.getName());
        this.pattern = pattern;
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
                return new DraftPropertyPlaceholderNode(property, String.format("%s: %s", property.getName(), property.getValue()));
            }
            relativeIndex = relativeIndex - properties.size();
            var collectedItems = getCollectedItemsFromAllCollections(draftElement);
            if (relativeIndex < collectedItems.size()) {
                var item = collectedItems.get(relativeIndex);
                return new DraftElementPlaceholderNode(this.pattern, item.collectionItem, true, String.format("%s (%s)", item.collection.getName(), item.collectionItem.getId()));
            }

            relativeIndex = relativeIndex - collectedItems.size();
            var elements = draftElement.getElements();
            if (relativeIndex < elements.size()) {
                var element = Objects.requireNonNull(elements.get(relativeIndex));
                return new DraftElementPlaceholderNode(this.pattern, element, false, String.format("%s (%s)", element.getName(), element.getId()));
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

                    if (Objects.equals(propertyOfElement, placeholderProperty)) {
                        return relativeIndex;
                    }
                    relativeIndex++;
                }

                return -1;
            }

            if (child instanceof DraftElementPlaceholderNode) {
                var placeholder = (DraftElementPlaceholderNode) child;
                var childElement = placeholder.getElement();
                relativeIndex = elementProperties.size();
                var collectedItems = getCollectedItemsFromAllCollections(parentElement);
                for (var collectedItem : collectedItems) {
                    if (Objects.equals(collectedItem.collectionItem, childElement)) {
                        return relativeIndex;
                    }
                    relativeIndex++;
                }

                var elements = parentElement.getElements();
                for (var element : elements) {
                    if (Objects.equals(element, childElement)) {
                        return relativeIndex;
                    }
                    relativeIndex++;
                }

                return -1;
            }
        }

        return -1;
    }

    @Override
    public void valueForPathChanged(TreePath path, Object value) {

    }

    public void deleteDraftElement(@NotNull DraftElement element) {

        if (this.selectedPath == null) {
            return;
        }

        var selectedNode = this.selectedPath.getLastPathComponent();
        if (selectedNode instanceof DraftElementPlaceholderNode) {
            var elementNode = (DraftElementPlaceholderNode) selectedNode;
            if (elementNode.getElement().isNotRoot()) {
                var parentPath = this.selectedPath.getParentPath();
                if (parentPath != null) {
                    var parentElementNode = ((DraftElementPlaceholderNode) parentPath.getLastPathComponent());
                    var existingIndexInTree = getIndexOfChild(parentElementNode, elementNode);
                    if (existingIndexInTree >= 0) {
                        parentElementNode.getElement().removeElement(element);
                        treeNodesRemoved(parentPath, new int[]{existingIndexInTree}, new Object[]{elementNode});
                    }
                }
            }
        }
    }

    public void setSelectedPath(@Nullable TreePath path) {

        this.selectedPath = path;
    }

    public void resetSelectedPath() {

        this.selectedPath = null;
    }

    private List<CollectedItemDescriptor> getCollectedItemsFromAllCollections(DraftElement item) {

        var collections = item.getCollections();
        var items = new ArrayList<CollectedItemDescriptor>();
        for (var collection : collections.entrySet()) {
            items.addAll(collection.getValue().getCollectionItems().stream()
                           .map(element -> new CollectedItemDescriptor(collection.getValue(), element))
                           .sorted(Comparator.comparing(descriptor -> Objects.requireNonNullElse(descriptor.collectionItem.getId(), "")))
                           .collect(Collectors.toList()));
        }

        return items;
    }

    private static class CollectedItemDescriptor {

        @NotNull
        private final DraftElement collection;
        @NotNull
        private final DraftElement collectionItem;

        public CollectedItemDescriptor(@NotNull DraftElement collection, @NotNull DraftElement collectionItem) {

            this.collection = collection;
            this.collectionItem = collectionItem;
        }

    }
}
