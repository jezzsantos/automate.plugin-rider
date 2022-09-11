package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElement;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElementValue;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftProperty;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.tree.TreePath;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DraftTreeModelTests {

    private DraftElement draft;
    private DraftTreeModel model;
    private TestModelTreeListener treeModelListener;

    @BeforeEach
    public void setUp() {

        var pattern = new PatternElement("anid", "aname");
        this.draft = new DraftElement("anid", Map.of(), true);
        this.model = new DraftTreeModel(this.draft, pattern);
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

        assertEquals(this.draft, result.getElement());
    }

    @Test
    public void whenGetChildAndParentIsNotPlaceholder_ThenReturnsNull() {

        var result = this.model.getChild(new Object(), 0);

        assertNull(result);
    }

    @Test
    public void whenGetChildAndIsNotPropertyNorElementNorItem_ThenReturnsNull() {

        var parent = new DraftElementPlaceholderNode(new PatternElement("anid", "aname"),
                                                     new DraftElement("aname", Map.of(), false), false, "adisplayname");

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
                                                     ), false), false, "adisplayname");

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
                                                     ), false), true, "adisplayname");

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
                                                     ), false), false, "adisplayname");

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
                                                     ), false), true, "adisplayname");

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
                                                     new DraftElement("aname", Map.of(), false), true, "adisplayname");
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
                                                     new DraftElement("aname", Map.of(), false), false, "adisplayname");

        var result = this.model.getIndexOfChild(parent, new Object());

        assertEquals(-1, result);
    }

    @Test
    public void whenGetIndexOfChildAndChildIsUnknownProperty_ThenReturnsOutOfBounds() {

        var parent = new DraftElementPlaceholderNode(new PatternElement("aparentid", "aname"),
                                                     new DraftElement("aname", Map.of(), false), false, "adisplayname");
        var child = new DraftPropertyPlaceholderNode(new DraftProperty("apropertyname", new DraftElementValue("avalue")), "adisplayname");

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
                                                     ), false), false, "adisplayname");
        var child = new DraftPropertyPlaceholderNode(new DraftProperty("apropertyname2", new DraftElementValue("avalue")), "adisplayname");

        var result = this.model.getIndexOfChild(parent, child);

        assertEquals(1, result);
    }

    @Test
    public void whenGetIndexOfChildAndChildIsUnknownElement_ThenReturnsOutOfBounds() {

        var parent = new DraftElementPlaceholderNode(new PatternElement("aparentid", "aname"),
                                                     new DraftElement("aname", Map.of(), false), false, "adisplayname");
        var child = new DraftElementPlaceholderNode(new PatternElement("achildid", "aname"),
                                                    new DraftElement("aname", Map.of(), false), false, "adisplayname");

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
                                                     ), false), false, "adisplayname");
        var child = new DraftElementPlaceholderNode(new PatternElement("achildid", "aname"),
                                                    new DraftElement("anelementname2", Map.of(
                                                      "Id", new DraftElementValue("anelementid2")
                                                    ), false), false, "adisplayname");

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
                                                     ), false), false, "adisplayname");
        var child = new DraftElementPlaceholderNode(new PatternElement("achildid", "aname"),
                                                    new DraftElement("acollectionitemname2", Map.of(
                                                      "Id", new DraftElementValue("acollectionitemid2")
                                                    ), false), true, "adisplayname");

        var result = this.model.getIndexOfChild(parent, child);

        assertEquals(1, result);
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
        var element = new DraftElementPlaceholderNode(new PatternElement("achildid", "aname"),
                                                      draftElement, false, "adisplayname");
        this.model.setSelectedPath(new TreePath(element));

        this.model.deleteDraftElement(draftElement);

        assertFalse(this.treeModelListener.hasRemoveEventBeenRaised());
    }

    @Test
    public void whenDeleteDraftElement_ThenRaisesEvent() {

        var patternElement = new PatternElement("aparentid", "aname");
        var rootElement = new DraftElementPlaceholderNode(patternElement,
                                                          new DraftElement("arootelementname", new HashMap<>(Map.of(
                                                            "Id", new DraftElementValue("arootelementid"),
                                                            "anelementname", new DraftElementValue("anelementname", Map.of(
                                                              "Id", new DraftElementValue("anelementid")
                                                            ))
                                                          )), true), false, "adisplayname");
        var draftElement = new DraftElement("anelementname", Map.of(
          "Id", new DraftElementValue("anelementid")
        ), false);
        var element = new DraftElementPlaceholderNode(patternElement,
                                                      draftElement, false, "adisplayname");
        this.model.setSelectedPath(new TreePath(new Object[]{rootElement, element}));

        this.model.deleteDraftElement(draftElement);

        assertTrue(this.treeModelListener.hasRemoved(0, element));
    }

}

