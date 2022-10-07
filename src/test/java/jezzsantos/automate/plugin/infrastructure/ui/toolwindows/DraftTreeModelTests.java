package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftDetailed;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElement;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElementValue;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftProperty;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.tree.TreePath;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;

public class DraftTreeModelTests {

    private DraftDetailed draft;
    private DraftTreeModel model;
    private TestModelTreeListener treeModelListener;
    private ITreeSelector treeSelector;

    @BeforeEach
    public void setUp() {

        var pattern = new PatternElement("anid", "aname");
        this.draft = new DraftDetailed("anid", "aname", "atoolkitversion", "aruntimeversion", new HashMap<>() {{
            put("Id", "anid");
        }});
        this.treeSelector = Mockito.mock(ITreeSelector.class);
        this.model = new DraftTreeModel(this.treeSelector, this.draft, pattern);
        this.treeModelListener = new TestModelTreeListener();
        this.model.addTreeModelListener(this.treeModelListener);
    }

    @AfterEach
    public void tearDown() {

        this.model.removeTreeModelListener(this.treeModelListener);
    }

    @Test
    public void whenGetRoot_ThenReturnsPlaceholder() {

        var result = (DraftElementPlaceholderNode) this.model.getRoot();

        assertEquals(this.draft.getRoot(), result.getElement());
    }

    @Test
    public void whenGetChildAndParentIsNotPlaceholder_ThenReturnsNull() {

        var result = this.model.getChild(new Object(), 0);

        assertNull(result);
    }

    @Test
    public void whenGetChildAndIsNotPropertyNorElementNorItem_ThenReturnsNull() {

        var parent = new DraftElementPlaceholderNode(new PatternElement("anid", "aname"),
                                                     new DraftElement("aname", Map.of(), false), false);

        var result = this.model.getChild(parent, 0);

        assertNull(result);
    }

    @Test
    public void whenGetChildAndIsProperty_ThenReturnsPropertyPlaceholder() {

        var parent = new DraftElementPlaceholderNode(new PatternElement("anid", "aname"),
                                                     new DraftElement("aname", Map.of(
                                                       "apropertyname1", new DraftElementValue("avalue1"),
                                                       "apropertyname2", new DraftElementValue("avalue2"),
                                                       "apropertyname3", new DraftElementValue("avalue3")
                                                     ), false), false);

        var result = (DraftPropertyPlaceholderNode) this.model.getChild(parent, 0);

        assertEquals("apropertyname1", result.getProperty().getName());
        assertEquals("avalue1", result.getProperty().getValue());
    }

    @Test
    public void whenGetChildAndIsCollectionItem_ThenReturnsElementPlaceholder() {

        var parent = new DraftElementPlaceholderNode(new PatternElement("anid", "aname"),
                                                     new DraftElement("aname", Map.of(
                                                       "anelementname1", new DraftElementValue("anelementname1", Map.of(
                                                         "Id", new DraftElementValue("anelementid"),
                                                         "Items", new DraftElementValue(List.of(
                                                           new DraftElement("acollectionitemname1", Map.of(
                                                             "Id", new DraftElementValue("acollectionitemid1")
                                                           ), false)
                                                         ))
                                                       ))
                                                     ), false), true);

        var result = (DraftElementPlaceholderNode) this.model.getChild(parent, 0);

        assertTrue(result.isCollectionItem());
        assertEquals("acollectionitemname1", result.getElement().getName());
        assertEquals("acollectionitemid1", result.getElement().getId());
    }

    @Test
    public void whenGetChildAndIsElement_ThenReturnsElementPlaceholder() {

        var parent = new DraftElementPlaceholderNode(new PatternElement("anid", "aname"),
                                                     new DraftElement("aname", Map.of(
                                                       "anelementname1", new DraftElementValue("anelementname1", Map.of(
                                                         "Id", new DraftElementValue("anelementid")
                                                       ))
                                                     ), false), false);

        var result = (DraftElementPlaceholderNode) this.model.getChild(parent, 0);

        assertFalse(result.isCollectionItem());
        assertEquals("anelementname1", result.getElement().getName());
        assertEquals("anelementid", result.getElement().getId());
    }

    @Test
    public void whenGetChildCountAndParentIsNotPlaceholder_ThenReturnsZero() {

        var result = this.model.getChildCount(new Object());

        assertEquals(0, result);
    }

    @Test
    public void whenGetChildCountAndParentIsPlaceholder_ThenReturnsTotalCountOfChildrenNodes() {

        var parent = new DraftElementPlaceholderNode(new PatternElement("anid", "aname"),
                                                     new DraftElement("aname", Map.of(
                                                       "apropertyname1", new DraftElementValue("avalue1"),
                                                       "anelementname1", new DraftElementValue("anelementname1", Map.of(
                                                         "Id", new DraftElementValue("anelementid")
                                                       )),
                                                       "anelementname2", new DraftElementValue("anelementname2", Map.of(
                                                         "Id", new DraftElementValue("anelementid"),
                                                         "Items", new DraftElementValue(List.of(
                                                           new DraftElement("acollectionitemname1", Map.of(
                                                             "Id", new DraftElementValue("acollectionitemid1")
                                                           ), false)
                                                         ))
                                                       ))
                                                     ), false), true);

        var result = this.model.getChildCount(parent);

        assertEquals(3, result);
    }

    @Test
    public void whenIsLeafAndNotPlaceholderNode_ThenReturnsTrue() {

        var result = this.model.isLeaf(new Object());

        assertTrue(result);
    }

    @Test
    public void whenIsLeafAndPlaceholderNode_ThenReturnsFalse() {

        var parent = new DraftElementPlaceholderNode(new PatternElement("anid", "aname"),
                                                     new DraftElement("aname", Map.of(), false), true);
        var result = this.model.isLeaf(parent);

        assertFalse(result);
    }

    @Test
    public void whenGetIndexOfChildAndParentIsNotPlaceholderNode_ThenReturnsOutOfBounds() {

        var result = this.model.getIndexOfChild(new Object(), new Object());

        assertEquals(-1, result);
    }

    @Test
    public void whenGetIndexOfChildAndChildIsNotEitherPlaceholderNode_ThenReturnsOutOfBounds() {

        var parent = new DraftElementPlaceholderNode(new PatternElement("aparentid", "aname"),
                                                     new DraftElement("aname", Map.of(), false), false);

        var result = this.model.getIndexOfChild(parent, new Object());

        assertEquals(-1, result);
    }

    @Test
    public void whenGetIndexOfChildAndChildIsUnknownProperty_ThenReturnsOutOfBounds() {

        var parent = new DraftElementPlaceholderNode(new PatternElement("aparentid", "aname"),
                                                     new DraftElement("aname", Map.of(), false), false);
        var child = new DraftPropertyPlaceholderNode(new DraftProperty("apropertyname", new DraftElementValue("avalue")));

        var result = this.model.getIndexOfChild(parent, child);

        assertEquals(-1, result);
    }

    @Test
    public void whenGetIndexOfChildAndChildIsChildProperty_ThenReturnsIndexOfChild() {

        var parent = new DraftElementPlaceholderNode(new PatternElement("aparentid", "aname"),
                                                     new DraftElement("aname", Map.of(
                                                       "apropertyname1", new DraftElementValue("avalue1"),
                                                       "apropertyname2", new DraftElementValue("avalue2"),
                                                       "apropertyname3", new DraftElementValue("avalue3")
                                                     ), false), false);
        var child = new DraftPropertyPlaceholderNode(new DraftProperty("apropertyname2", new DraftElementValue("avalue")));

        var result = this.model.getIndexOfChild(parent, child);

        assertEquals(1, result);
    }

    @Test
    public void whenGetIndexOfChildAndChildIsUnknownElement_ThenReturnsOutOfBounds() {

        var parent = new DraftElementPlaceholderNode(new PatternElement("aparentid", "aname"),
                                                     new DraftElement("aname", Map.of(), false), false);
        var child = new DraftElementPlaceholderNode(new PatternElement("achildid", "aname"),
                                                    new DraftElement("aname", Map.of(), false), false);

        var result = this.model.getIndexOfChild(parent, child);

        assertEquals(-1, result);
    }

    @Test
    public void whenGetIndexOfChildAndChildIsChildElement_ThenReturnsIndexOfChild() {

        var parent = new DraftElementPlaceholderNode(new PatternElement("aparentid", "aname"),
                                                     new DraftElement("aname", Map.of(
                                                       "anelementname1", new DraftElementValue("anelementname1", Map.of(
                                                         "Id", new DraftElementValue("anelementid1")
                                                       )),
                                                       "anelementname2", new DraftElementValue("anelementname2", Map.of(
                                                         "Id", new DraftElementValue("anelementid2")
                                                       )),
                                                       "anelementname3", new DraftElementValue("anelementname3", Map.of(
                                                         "Id", new DraftElementValue("anelementid3")
                                                       ))
                                                     ), false), false);
        var child = new DraftElementPlaceholderNode(new PatternElement("achildid", "aname"),
                                                    new DraftElement("anelementname2", Map.of(
                                                      "Id", new DraftElementValue("anelementid2")
                                                    ), false), false);

        var result = this.model.getIndexOfChild(parent, child);

        assertEquals(1, result);
    }

    @Test
    public void whenGetIndexOfChildAndChildIsChildCollectionItem_ThenReturnsIndexOfChild() {

        var parent = new DraftElementPlaceholderNode(new PatternElement("aparentid", "aname"),
                                                     new DraftElement("aname", Map.of(
                                                       "anelementname1", new DraftElementValue("anelementname1", Map.of(
                                                         "Id", new DraftElementValue("anelementid1"),
                                                         "Items", new DraftElementValue(List.of(
                                                           new DraftElement("acollectionitemname1", Map.of(
                                                             "Id", new DraftElementValue("acollectionitemid1")
                                                           ), false),
                                                           new DraftElement("acollectionitemname2", Map.of(
                                                             "Id", new DraftElementValue("acollectionitemid2")
                                                           ), false),
                                                           new DraftElement("acollectionitemname3", Map.of(
                                                             "Id", new DraftElementValue("acollectionitemid3")
                                                           ), false)
                                                         ))
                                                       ))
                                                       //                                                       "anelementname2", new DraftElementValue("anelementname2", Map.of(
                                                       //                                                         "Id", new DraftElementValue("anelementid2")
                                                       //                                                       )),
                                                       //                                                       "anelementname3", new DraftElementValue("anelementname3", Map.of(
                                                       //                                                         "Id", new DraftElementValue("anelementid3")
                                                       //                                                       ))
                                                     ), false), false);
        var child = new DraftElementPlaceholderNode(new PatternElement("achildid", "aname"),
                                                    new DraftElement("acollectionitemname2", Map.of(
                                                      "Id", new DraftElementValue("acollectionitemid2")
                                                    ), false), true);

        var result = this.model.getIndexOfChild(parent, child);

        assertEquals(1, result);
    }

    @Test
    public void whenInsertDraftElementAndSelectedPathIsNull_ThenDoesNothing() {

        this.model.resetSelectedPath();

        this.model.insertDraftElement(new DraftElement("aname", Map.of(), false), false);

        assertFalse(this.treeModelListener.hasInsertEventBeenRaised());
    }

    @Test
    public void whenInsertDraftElementAndSelectedPathIsNotAPlaceholder_ThenDoesNothing() {

        this.model.setSelectedPath(new TreePath(new Object()));

        this.model.insertDraftElement(new DraftElement("aname", Map.of(), false), false);

        assertFalse(this.treeModelListener.hasInsertEventBeenRaised());
    }

    @Test
    public void whenInsertDraftElement_ThenRaisesEventAndSelectsNode() {

        var schema = new PatternElement("anid", "aname");
        var parentNode = new DraftElementPlaceholderNode(schema, new DraftElement("aparentelementname", new HashMap<>() {{
            put("Id", new DraftElementValue("aparentelementid"));
        }}, false),
                                                         false);
        var draftElement = new DraftElement("anelementname", Map.of(
          "Id", new DraftElementValue("anelementid")), false);
        this.model.setSelectedPath(new TreePath(new Object[]{parentNode}));

        this.model.insertDraftElement(draftElement, false);

        assertTrue(this.treeModelListener.hasInserted(0, draftElement));
        Mockito.verify(this.treeSelector).selectAndExpandPath(argThat(path ->
                                                                        Objects.requireNonNull(((DraftElementPlaceholderNode) path.getLastPathComponent()).getElement())
                                                                          .equals(draftElement)));
    }

    @Test
    public void whenUpdateDraftElementAndSelectedPathIsNull_ThenDoesNothing() {

        this.model.resetSelectedPath();

        this.model.updateDraftElement(new DraftElement("aname", Map.of(), false));

        assertFalse(this.treeModelListener.hasChangeEventBeenRaised());
    }

    @Test
    public void whenUpdateDraftElementAndSelectedPathIsNotAPlaceholder_ThenDoesNothing() {

        this.model.setSelectedPath(new TreePath(new Object()));

        this.model.updateDraftElement(new DraftElement("aname", Map.of(), false));

        assertFalse(this.treeModelListener.hasChangeEventBeenRaised());
    }

    @Test
    public void whenUpdateDraftElement_ThenRaisesEvent() {

        var parentNode = new DraftElementPlaceholderNode(new PatternElement("aparentid", "aname"),
                                                         new DraftElement("aparentelementname", Map.of(
                                                           "Id", new DraftElementValue("aparentelementid"),
                                                           "achildelementname", new DraftElementValue("achildelementname", Map.of(
                                                             "Id", new DraftElementValue("achildelementid")
                                                           ))
                                                         ), false), false);
        var childNode = new DraftElementPlaceholderNode(new PatternElement("achildid", "aname"),
                                                        new DraftElement("achildelementname", new HashMap<>() {{
                                                            put("Id", new DraftElementValue("achildelementid"));
                                                        }}, false),
                                                        false);
        var draftElement = new DraftElement("anelementname", new HashMap<>() {{
            put("Id", new DraftElementValue("achildelementid"));
        }}, false);
        var draftProperty = new DraftProperty("apropertyname1", new DraftElementValue("avalue"));
        draftElement.addProperty(draftProperty);
        this.model.setSelectedPath(new TreePath(new Object[]{parentNode, childNode}));

        this.model.updateDraftElement(draftElement);

        assertTrue(this.treeModelListener.hasChanged(0, new DraftPropertyPlaceholderNode(draftProperty)));
        Mockito.verify(this.treeSelector).selectPath(argThat(treePath -> treePath.getLastPathComponent().equals(childNode)));
    }

    @Test
    public void whenDeleteDraftElementAndSelectedPathIsNull_ThenDoesNothing() {

        this.model.resetSelectedPath();

        this.model.deleteDraftElement(new DraftElement("aname", Map.of(), false));

        assertFalse(this.treeModelListener.hasRemoveEventBeenRaised());
    }

    @Test
    public void whenDeleteDraftElementAndSelectedPathIsNotAPlaceholder_ThenDoesNothing() {

        this.model.setSelectedPath(new TreePath(new Object()));

        this.model.deleteDraftElement(new DraftElement("aname", Map.of(), false));

        assertFalse(this.treeModelListener.hasRemoveEventBeenRaised());
    }

    @Test
    public void whenDeleteDraftElementAndNoParentPath_ThenDoesNothing() {

        var draftElement = new DraftElement("anelementname", Map.of(
          "Id", new DraftElementValue("anelementid")
        ), false);
        var parentNode = new DraftElementPlaceholderNode(new PatternElement("achildid", "aname"),
                                                         draftElement, false);
        this.model.setSelectedPath(new TreePath(parentNode));

        this.model.deleteDraftElement(draftElement);

        assertFalse(this.treeModelListener.hasRemoveEventBeenRaised());
    }

    @Test
    public void whenDeleteDraftElementWithOnlyElement_ThenRaisesEventAndSelectsParentNode() {

        var patternElement = new PatternElement("aparentid", "aname");
        var rootNode = new DraftElementPlaceholderNode(patternElement,
                                                       new DraftElement("arootelementname", new HashMap<>() {{
                                                           put("Id", new DraftElementValue("arootelementid"));
                                                           put("anelementname", new DraftElementValue("anelementname", Map.of(
                                                             "Id", new DraftElementValue("anelementid")
                                                           )));
                                                       }}, true), false);
        var draftElement = new DraftElement("anelementname", Map.of(
          "Id", new DraftElementValue("anelementid")
        ), false);
        var childNode = new DraftElementPlaceholderNode(patternElement, draftElement, false);
        this.model.setSelectedPath(new TreePath(new Object[]{rootNode, childNode}));

        assertEquals(1, rootNode.getElement().getElements().size());

        this.model.deleteDraftElement(draftElement);

        assertTrue(this.treeModelListener.hasRemoved(0, childNode));
        assertEquals(0, rootNode.getElement().getElements().size());
        Mockito.verify(this.treeSelector).selectPath(argThat(path ->
                                                               Objects.requireNonNull(((DraftElementPlaceholderNode) path.getLastPathComponent()).getElement().getId())
                                                                 .equals("arootelementid")));
    }

    @Test
    public void whenDeleteDraftElementWithOtherElementAfter_ThenRaisesEventAndSelectsOtherElement() {

        var patternElement = new PatternElement("aparentid", "aname");
        var rootNode = new DraftElementPlaceholderNode(patternElement,
                                                       new DraftElement("arootelementname", new HashMap<>() {{
                                                           put("Id", new DraftElementValue("arootelementid"));
                                                           put("anelementname1", new DraftElementValue("anelementname1", Map.of(
                                                             "Id", new DraftElementValue("anelementid1")
                                                           )));
                                                           put("anelementname2", new DraftElementValue("anelementname2", Map.of(
                                                             "Id", new DraftElementValue("anelementid2")
                                                           )));
                                                       }}, true), false);
        var draftElement = new DraftElement("anelementname1", Map.of(
          "Id", new DraftElementValue("anelementid1")
        ), false);
        var childNode = new DraftElementPlaceholderNode(patternElement, draftElement, false);
        this.model.setSelectedPath(new TreePath(new Object[]{rootNode, childNode}));

        assertEquals(2, rootNode.getElement().getElements().size());

        this.model.deleteDraftElement(draftElement);

        assertTrue(this.treeModelListener.hasRemoved(0, childNode));
        assertEquals(1, rootNode.getElement().getElements().size());
        Mockito.verify(this.treeSelector).selectPath(argThat(path ->
                                                               Objects.requireNonNull(((DraftElementPlaceholderNode) path.getLastPathComponent()).getElement().getId())
                                                                 .equals("anelementid2")));
    }

    @Test
    public void whenDeleteDraftElementWithOtherElementBefore_ThenRaisesEventAndSelectsOtherElement() {

        var patternElement = new PatternElement("aparentid", "aname");
        var rootNode = new DraftElementPlaceholderNode(patternElement,
                                                       new DraftElement("arootelementname", new HashMap<>() {{
                                                           put("Id", new DraftElementValue("arootelementid"));
                                                           put("anelementname1", new DraftElementValue("anelementname1", Map.of(
                                                             "Id", new DraftElementValue("anelementid1")
                                                           )));
                                                           put("anelementname2", new DraftElementValue("anelementname2", Map.of(
                                                             "Id", new DraftElementValue("anelementid2")
                                                           )));
                                                       }}, true), false);
        var draftElement = new DraftElement("anelementname2", Map.of(
          "Id", new DraftElementValue("anelementid2")
        ), false);
        var childNode = new DraftElementPlaceholderNode(patternElement, draftElement, false);
        this.model.setSelectedPath(new TreePath(new Object[]{rootNode, childNode}));

        assertEquals(2, rootNode.getElement().getElements().size());

        this.model.deleteDraftElement(draftElement);

        assertTrue(this.treeModelListener.hasRemoved(1, childNode));
        assertEquals(1, rootNode.getElement().getElements().size());
        Mockito.verify(this.treeSelector).selectPath(argThat(path ->
                                                               Objects.requireNonNull(((DraftElementPlaceholderNode) path.getLastPathComponent()).getElement().getId())
                                                                 .equals("anelementid1")));
    }

    @Test
    public void whenDeleteDraftElementWithCollectionItem_ThenRaisesEventAndSelectsParentNode() {

        var patternElement = new PatternElement("aparentid", "aname");
        var rootNode = new DraftElementPlaceholderNode(patternElement,
                                                       new DraftElement("arootelementname", new HashMap<>() {{
                                                           put("Id", new DraftElementValue("arootelementid"));
                                                           put("apropertyname1", new DraftElementValue("avalue"));
                                                           put("acollectionname", new DraftElementValue("acollectionname", Map.of(
                                                             "Items", new DraftElementValue(new ArrayList<>() {{
                                                                 add(new DraftElement("anelementname", Map.of(
                                                                   "Id", new DraftElementValue("acollectionitemid")
                                                                 ), false));
                                                             }})
                                                           )));
                                                       }}, true), false);
        var draftElement = new DraftElement("anelementname", Map.of(
          "Id", new DraftElementValue("acollectionitemid")
        ), false);
        var childCollectionItemNode = new DraftElementPlaceholderNode(patternElement, draftElement, true);
        this.model.setSelectedPath(new TreePath(new Object[]{rootNode, childCollectionItemNode}));

        assertEquals(1, rootNode.getElement().getCollections().size());
        assertEquals(1, Objects.requireNonNull(rootNode.getElement().getCollections().get(0)).getCollectionItems().size());

        this.model.deleteDraftElement(draftElement);

        assertTrue(this.treeModelListener.hasRemoved(1, childCollectionItemNode));
        assertEquals(1, rootNode.getElement().getCollections().size());
        assertEquals(0, Objects.requireNonNull(rootNode.getElement().getCollections().get(0)).getCollectionItems().size());
        Mockito.verify(this.treeSelector).selectPath(argThat(path ->
                                                               Objects.requireNonNull(((DraftElementPlaceholderNode) path.getLastPathComponent()).getElement().getId())
                                                                 .equals("arootelementid")));
    }
}

