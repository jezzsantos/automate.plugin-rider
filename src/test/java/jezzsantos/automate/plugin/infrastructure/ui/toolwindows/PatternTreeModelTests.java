package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import jezzsantos.automate.plugin.application.interfaces.patterns.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.tree.TreePath;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;

public class PatternTreeModelTests {

    private PatternDetailed pattern;
    private PatternTreeModel model;
    private TestModelTreeListener treeModelListener;
    private ITreeSelector treeSelector;

    @BeforeEach
    public void setUp() {

        this.pattern = new PatternDetailed("anid", "aname", new PatternVersion("aversion"), new PatternElement("anid", "aname"));
        this.treeSelector = Mockito.mock(ITreeSelector.class);
        this.model = new PatternTreeModel(this.treeSelector, this.pattern);
        this.treeModelListener = new TestModelTreeListener();
        this.model.addTreeModelListener(this.treeModelListener);
    }

    @AfterEach
    public void tearDown() {

        this.model.removeTreeModelListener(this.treeModelListener);
    }

    @Test
    public void whenGetRoot_ThenReturnsPattern() {

        var result = this.model.getRoot();

        assertEquals(this.pattern.getPattern(), result);
    }

    @Test
    public void whenGetChildAndParentIsCodeTemplate_ThenReturnsNull() {

        var parent = new CodeTemplate("anid", "aname");

        var result = this.model.getChild(parent, 1);

        assertNull(result);
    }

    @Test
    public void whenGetChildAndParentIsAutomation_ThenReturnsNull() {

        var parent = new Automation("anid", "aname");

        var result = this.model.getChild(parent, 1);

        assertNull(result);
    }

    @Test
    public void whenGetChildAndParentIsAttribute_ThenReturnsNull() {

        var parent = new Attribute("anid", "aname");

        var result = this.model.getChild(parent, 1);

        assertNull(result);
    }

    @Test
    public void whenGetChildAndParentIsPatternElementAndIndexIs0_ThenReturnsCodeTemplates() {

        var parent = new PatternElement("anid", "aname");

        var result = this.model.getChild(parent, 0);

        assertEquals(parent.getCodeTemplates(), ((PatternFolderPlaceholderNode) result).getChild());
    }

    @Test
    public void whenGetChildAndParentIsPatternElementAndIndexIs0_ThenReturnsAutomation() {

        var parent = new PatternElement("anid", "aname");

        var result = this.model.getChild(parent, 1);

        assertEquals(parent.getAutomation(), ((PatternFolderPlaceholderNode) result).getChild());
    }

    @Test
    public void whenGetChildAndParentIsPatternElementAndIndexIs0_ThenReturnsAttributes() {

        var parent = new PatternElement("anid", "aname");

        var result = this.model.getChild(parent, 2);

        assertEquals(parent.getAttributes(), ((PatternFolderPlaceholderNode) result).getChild());
    }

    @Test
    public void whenGetChildAndParentIsPatternElementAndIndexIs0_ThenReturnsElements() {

        var parent = new PatternElement("anid", "aname");

        var result = this.model.getChild(parent, 3);

        assertEquals(parent.getElements(), ((PatternFolderPlaceholderNode) result).getChild());
    }

    @Test
    public void whenGetChildAndParentIsPatternElementAndIndexIsOutOfRange_ThenReturnsNull() {

        var parent = new PatternElement("anid", "aname");

        var result = this.model.getChild(parent, 99);

        assertNull(result);
    }

    @Test
    public void whenGetChildAndParentIsCodeTemplatesAndIndexInRange_ThenReturnsCodeTemplate() {

        var codeTemplate = new CodeTemplate("anid", "aname");
        var pattern = new PatternElement("anid", "aname");
        pattern.addCodeTemplate(codeTemplate);
        var parent = new PatternFolderPlaceholderNode(pattern, pattern.getCodeTemplates(), "adisplayname");

        var result = this.model.getChild(parent, 0);

        assertEquals(codeTemplate, result);
    }

    @Test
    public void whenGetChildAndParentIsCodeTemplatesAndIndexOutOfRange_ThenReturnsNull() {

        var codeTemplate = new CodeTemplate("anid", "aname");
        var pattern = new PatternElement("anid", "aname");
        pattern.addCodeTemplate(codeTemplate);
        var parent = new PatternFolderPlaceholderNode(pattern, pattern.getCodeTemplates(), "adisplayname");

        var result = this.model.getChild(parent, 99);

        assertNull(result);
    }

    @Test
    public void whenGetChildAndParentIsAutomationsAndIndexInRange_ThenReturnsAutomation() {

        var automation = new Automation("anid", "aname");
        var pattern = new PatternElement("anid", "aname");
        pattern.addAutomation(automation);
        var parent = new PatternFolderPlaceholderNode(pattern, pattern.getAutomation(), "adisplayname");

        var result = this.model.getChild(parent, 0);

        assertEquals(automation, result);
    }

    @Test
    public void whenGetChildAndParentIsAutomationsAndIndexOutOfRange_ThenReturnsNull() {

        var automation = new Automation("anid", "aname");
        var pattern = new PatternElement("anid", "aname");
        pattern.addAutomation(automation);
        var parent = new PatternFolderPlaceholderNode(pattern, pattern.getAutomation(), "adisplayname");

        var result = this.model.getChild(parent, 99);

        assertNull(result);
    }

    @Test
    public void whenGetChildAndParentIsAttributesAndIndexInRange_ThenReturnsAttribute() {

        var attribute = new Attribute("anid", "aname");
        var pattern = new PatternElement("anid", "aname");
        pattern.addAttribute(attribute);
        var parent = new PatternFolderPlaceholderNode(pattern, pattern.getAttributes(), "adisplayname");

        var result = this.model.getChild(parent, 0);

        assertEquals(attribute, result);
    }

    @Test
    public void whenGetChildAndParentIsAttributesAndIndexOutOfRange_ThenReturnsNull() {

        var attribute = new Attribute("anid", "aname");
        var pattern = new PatternElement("anid", "aname");
        pattern.addAttribute(attribute);
        var parent = new PatternFolderPlaceholderNode(pattern, pattern.getAttributes(), "adisplayname");

        var result = this.model.getChild(parent, 99);

        assertNull(result);
    }

    @Test
    public void whenGetChildAndParentIsElementsAndIndexInRange_ThenReturnsElement() {

        var element = new PatternElement("anid", "aname");
        var pattern = new PatternElement("anid", "aname");
        pattern.addElement(element);
        var parent = new PatternFolderPlaceholderNode(pattern, pattern.getElements(), "adisplayname");

        var result = this.model.getChild(parent, 0);

        assertEquals(element, result);
    }

    @Test
    public void whenGetChildAndParentIsElementsAndIndexOutOfRange_ThenReturnsNull() {

        var element = new PatternElement("anid", "aname");
        var pattern = new PatternElement("anid", "aname");
        pattern.addElement(element);
        var parent = new PatternFolderPlaceholderNode(pattern, pattern.getElements(), "adisplayname");

        var result = this.model.getChild(parent, 99);

        assertNull(result);
    }

    @Test
    public void whenGetChildCountAndParentIsCodeTemplate_ThenReturnsZero() {

        var parent = new CodeTemplate("anid", "aname");

        var result = this.model.getChildCount(parent);

        assertEquals(0, result);
    }

    @Test
    public void whenGetChildCountAndParentIsAutomation_ThenReturnsZero() {

        var parent = new Automation("anid", "aname");

        var result = this.model.getChildCount(parent);

        assertEquals(0, result);
    }

    @Test
    public void whenGetChildCountAndParentIsAttribute_ThenReturnsZero() {

        var parent = new Attribute("anid", "aname");

        var result = this.model.getChildCount(parent);

        assertEquals(0, result);
    }

    @Test
    public void whenGetChildCountAndParentIsPatternElement_ThenReturnsFour() {

        var parent = new PatternElement("anid", "aname");

        var result = this.model.getChildCount(parent);

        assertEquals(4, result);
    }

    @Test
    public void whenGetChildCountAndParentIsCodeTemplates_ThenReturnsSize() {

        var pattern = new PatternElement("anid", "aname");
        pattern.addCodeTemplate(new CodeTemplate("anid", "aname"));
        var parent = new PatternFolderPlaceholderNode(pattern, pattern.getCodeTemplates(), "adisplayname");

        var result = this.model.getChildCount(parent);

        assertEquals(1, result);
    }

    @Test
    public void whenGetChildCountAndParentIsAutomation_ThenReturnsSize() {

        var pattern = new PatternElement("anid", "aname");
        pattern.addAutomation(new Automation("anid", "aname"));
        var parent = new PatternFolderPlaceholderNode(pattern, pattern.getAutomation(), "adisplayname");

        var result = this.model.getChildCount(parent);

        assertEquals(1, result);
    }

    @Test
    public void whenGetChildCountAndParentIsAttributes_ThenReturnsSize() {

        var pattern = new PatternElement("anid", "aname");
        pattern.addAttribute(new Attribute("anid", "aname"));
        var parent = new PatternFolderPlaceholderNode(pattern, pattern.getAttributes(), "adisplayname");

        var result = this.model.getChildCount(parent);

        assertEquals(1, result);
    }

    @Test
    public void whenGetChildCountAndParentIsElements_ThenReturnsSize() {

        var pattern = new PatternElement("anid", "aname");
        pattern.addElement(new PatternElement("anid", "aname"));
        var parent = new PatternFolderPlaceholderNode(pattern, pattern.getElements(), "adisplayname");

        var result = this.model.getChildCount(parent);

        assertEquals(1, result);
    }

    @Test
    public void whenIsLeafAndNodeIsCodeTemplate_ThenReturnsTrue() {

        var node = new CodeTemplate("anid", "aname");

        var result = this.model.isLeaf(node);

        assertTrue(result);
    }

    @Test
    public void whenIsLeafAndNodeIsAutomation_ThenReturnsTrue() {

        var node = new Automation("anid", "aname");

        var result = this.model.isLeaf(node);

        assertTrue(result);
    }

    @Test
    public void whenIsLeafAndNodeIsAttribute_ThenReturnsTrue() {

        var node = new Attribute("anid", "aname");

        var result = this.model.isLeaf(node);

        assertTrue(result);
    }

    @Test
    public void whenIsLeafAndNodeIsElement_ThenReturnsFalse() {

        var node = new PatternElement("anid", "aname");

        var result = this.model.isLeaf(node);

        assertFalse(result);
    }

    @Test
    public void whenIsLeafAndNodeIsCodeTemplates_ThenReturnsFalse() {

        var pattern = new PatternElement("anid", "aname");
        var node = new PatternFolderPlaceholderNode(pattern, pattern.getCodeTemplates(), "adisplayname");

        var result = this.model.isLeaf(node);

        assertFalse(result);
    }

    @Test
    public void whenIsLeafAndNodeIsAutomation_ThenReturnsFalse() {

        var pattern = new PatternElement("anid", "aname");
        var node = new PatternFolderPlaceholderNode(pattern, pattern.getAutomation(), "adisplayname");

        var result = this.model.isLeaf(node);

        assertFalse(result);
    }

    @Test
    public void whenIsLeafAndNodeIsAttributes_ThenReturnsFalse() {

        var pattern = new PatternElement("anid", "aname");
        var node = new PatternFolderPlaceholderNode(pattern, pattern.getAttributes(), "adisplayname");

        var result = this.model.isLeaf(node);

        assertFalse(result);
    }

    @Test
    public void whenIsLeafAndNodeIsElements_ThenReturnsFalse() {

        var pattern = new PatternElement("anid", "aname");
        var node = new PatternFolderPlaceholderNode(pattern, pattern.getElements(), "adisplayname");

        var result = this.model.isLeaf(node);

        assertFalse(result);
    }

    @Test
    public void whenGetIndexOfChildAndParentIsCodeTemplate_ThenReturnsOutOfRange() {

        var parent = new CodeTemplate("anid", "aname");

        var result = this.model.getIndexOfChild(parent, null);

        assertEquals(-1, result);
    }

    @Test
    public void whenGetIndexOfChildAndParentIsAutomation_ThenReturnsOutOfRange() {

        var parent = new Automation("anid", "aname");

        var result = this.model.getIndexOfChild(parent, null);

        assertEquals(-1, result);
    }

    @Test
    public void whenGetIndexOfChildAndParentIsAttribute_ThenReturnsOutOfRange() {

        var parent = new Attribute("anid", "aname");

        var result = this.model.getIndexOfChild(parent, null);

        assertEquals(-1, result);
    }

    @Test
    public void whenGetIndexOfChildAndParentIsElementAndChildIsCodeTemplates_ThenReturnsZero() {

        var parent = new PatternElement("anid", "aname");
        var child = new PatternFolderPlaceholderNode(parent, parent.getCodeTemplates(), "adisplayname");

        var result = this.model.getIndexOfChild(parent, child);

        assertEquals(0, result);
    }

    @Test
    public void whenGetIndexOfChildAndParentIsElementAndChildIsAutomation_ThenReturnsOne() {

        var parent = new PatternElement("anid", "aname");
        var child = new PatternFolderPlaceholderNode(parent, parent.getAutomation(), "adisplayname");

        var result = this.model.getIndexOfChild(parent, child);

        assertEquals(1, result);
    }

    @Test
    public void whenGetIndexOfChildAndParentIsElementAndChildIsAttributes_ThenReturnsTwo() {

        var parent = new PatternElement("anid", "aname");
        var child = new PatternFolderPlaceholderNode(parent, parent.getAttributes(), "adisplayname");

        var result = this.model.getIndexOfChild(parent, child);

        assertEquals(2, result);
    }

    @Test
    public void whenGetIndexOfChildAndParentIsElementAndChildIsElements_ThenReturnsThree() {

        var parent = new PatternElement("anid", "aname");
        var child = new PatternFolderPlaceholderNode(parent, parent.getElements(), "adisplayname");

        var result = this.model.getIndexOfChild(parent, child);

        assertEquals(3, result);
    }

    @Test
    public void whenGetIndexOfChildAndParentIsCodeTemplatesAndChildExists_ThenReturnsIndexOfChild() {

        var codeTemplate1 = new CodeTemplate("anid", "aname");
        var codeTemplate2 = new CodeTemplate("anid", "aname");
        var element = new PatternElement("anid", "aname");
        element.addCodeTemplate(codeTemplate1);
        element.addCodeTemplate(codeTemplate2);
        var parent = new PatternFolderPlaceholderNode(element, element.getCodeTemplates(), "adisplayname");

        var result = this.model.getIndexOfChild(parent, codeTemplate2);

        assertEquals(1, result);
    }

    @Test
    public void whenGetIndexOfChildAndParentIsCodeTemplatesAndChildNotExists_ThenReturnsZero() {

        var codeTemplate1 = new CodeTemplate("anid", "aname");
        var codeTemplate2 = new CodeTemplate("anid", "aname");
        var element = new PatternElement("anid", "aname");
        element.addCodeTemplate(codeTemplate1);
        var parent = new PatternFolderPlaceholderNode(element, element.getCodeTemplates(), "adisplayname");

        var result = this.model.getIndexOfChild(parent, codeTemplate2);

        assertEquals(-1, result);
    }

    @Test
    public void whenGetIndexOfChildAndParentIsAutomationsAndChildExists_ThenReturnsIndexOfChild() {

        var automation1 = new Automation("anid", "aname");
        var automation2 = new Automation("anid", "aname");
        var element = new PatternElement("anid", "aname");
        element.addAutomation(automation1);
        element.addAutomation(automation2);
        var parent = new PatternFolderPlaceholderNode(element, element.getAutomation(), "adisplayname");

        var result = this.model.getIndexOfChild(parent, automation2);

        assertEquals(1, result);
    }

    @Test
    public void whenGetIndexOfChildAndParentIsAutomationsAndChildNotExists_ThenReturnsZero() {

        var automation1 = new Automation("anid", "aname");
        var automation2 = new Automation("anid", "aname");
        var element = new PatternElement("anid", "aname");
        element.addAutomation(automation1);
        var parent = new PatternFolderPlaceholderNode(element, element.getAutomation(), "adisplayname");

        var result = this.model.getIndexOfChild(parent, automation2);

        assertEquals(-1, result);
    }

    @Test
    public void whenGetIndexOfChildAndParentIsAttributesAndChildExists_ThenReturnsIndexOfChild() {

        var attribute1 = new Attribute("anid", "aname");
        var attribute2 = new Attribute("anid", "aname");
        var element = new PatternElement("anid", "aname");
        element.addAttribute(attribute1);
        element.addAttribute(attribute2);
        var parent = new PatternFolderPlaceholderNode(element, element.getAttributes(), "adisplayname");

        var result = this.model.getIndexOfChild(parent, attribute2);

        assertEquals(1, result);
    }

    @Test
    public void whenGetIndexOfChildAndParentIsAttributesAndChildNotExists_ThenReturnsZero() {

        var attribute1 = new Attribute("anid", "aname");
        var attribute2 = new Attribute("anid", "aname");
        var element = new PatternElement("anid", "aname");
        element.addAttribute(attribute1);
        var parent = new PatternFolderPlaceholderNode(element, element.getAttributes(), "adisplayname");

        var result = this.model.getIndexOfChild(parent, attribute2);

        assertEquals(-1, result);
    }

    @Test
    public void whenGetIndexOfChildAndParentIsElementsAndChildExists_ThenReturnsIndexOfChild() {

        var element1 = new PatternElement("anid", "aname");
        var element2 = new PatternElement("anid", "aname");
        var element = new PatternElement("anid", "aname");
        element.addElement(element1);
        element.addElement(element2);
        var parent = new PatternFolderPlaceholderNode(element, element.getElements(), "adisplayname");

        var result = this.model.getIndexOfChild(parent, element2);

        assertEquals(1, result);
    }

    @Test
    public void whenGetIndexOfChildAndParentIsElementsAndChildNotExists_ThenReturnsZero() {

        var element1 = new PatternElement("anid", "aname");
        var element2 = new PatternElement("anid", "aname");
        var element = new PatternElement("anid", "aname");
        element.addElement(element1);
        var parent = new PatternFolderPlaceholderNode(element, element.getElements(), "adisplayname");

        var result = this.model.getIndexOfChild(parent, element2);

        assertEquals(-1, result);
    }

    @Test
    public void whenInsertAttributeAndSelectedPathIsNull_ThenDoesNothing() {

        this.model.resetSelectedPath();

        this.model.insertAttribute(new Attribute("anid", "aname"));

        assertFalse(this.treeModelListener.hasInsertEventBeenRaised());
    }

    @Test
    public void whenInsertAttributeAndSelectedPathIsNotAPlaceholder_ThenDoesNothing() {

        this.model.setSelectedPath(new TreePath(new Object()));

        this.model.insertAttribute(new Attribute("anid", "aname"));

        assertFalse(this.treeModelListener.hasInsertEventBeenRaised());
    }

    @Test
    public void whenInsertAttributeOnPatternElement_ThenRaisesEventAndSelectsNode() {

        var parentNode = new PatternElement("aparentid", "aname");
        var attributeNode = new Attribute("anid", "aname");
        parentNode.addAttribute(attributeNode);

        this.model.setSelectedPath(new TreePath(new Object[]{parentNode}));

        this.model.insertAttribute(attributeNode);

        assertTrue(this.treeModelListener.hasInserted(0, attributeNode));
        Mockito.verify(this.treeSelector).selectPath(argThat(treePath -> treePath.getLastPathComponent().equals(attributeNode)));
    }

    @Test
    public void whenInsertAttributeOnAttributesPlaceholder_ThenRaisesEventAndSelectsNode() {

        var parentNode = new PatternElement("aparentid", "aname");
        var attributeNode = new Attribute("anid", "aname");
        parentNode.addAttribute(attributeNode);
        var folderNode = new PatternFolderPlaceholderNode(parentNode, parentNode.getAttributes(), "adisplayname");

        this.model.setSelectedPath(new TreePath(new Object[]{parentNode, folderNode}));

        this.model.insertAttribute(attributeNode);

        assertTrue(this.treeModelListener.hasInserted(0, attributeNode));
        Mockito.verify(this.treeSelector).selectPath(argThat(treePath -> treePath.getLastPathComponent().equals(attributeNode)));
    }

    @Test
    public void whenUpdateAttributeAndSelectedPathIsNull_ThenDoesNothing() {

        this.model.resetSelectedPath();

        this.model.updateAttribute(new Attribute("anid", "aname"));

        assertFalse(this.treeModelListener.hasChangeEventBeenRaised());
    }

    @Test
    public void whenUpdateAttributeAndSelectedPathIsNotAnAttribute_ThenDoesNothing() {

        this.model.setSelectedPath(new TreePath(new Object()));

        this.model.updateAttribute(new Attribute("anid", "aname"));

        assertFalse(this.treeModelListener.hasChangeEventBeenRaised());
    }

    @Test
    public void whenUpdateAttribute_ThenRaisesEvent() {

        var parentNode = new PatternElement("aparentid", "aname");
        var attributeNode = new Attribute("anattributeid", "anattributename");
        parentNode.addAttribute(attributeNode);
        var folderNode = new PatternFolderPlaceholderNode(parentNode, parentNode.getAttributes(), "adisplayname");

        this.model.setSelectedPath(new TreePath(new Object[]{parentNode, folderNode, attributeNode}));

        this.model.updateAttribute(attributeNode);

        assertTrue(this.treeModelListener.hasChanged(0, attributeNode));
        Mockito.verify(this.treeSelector).selectPath(argThat(treePath -> treePath.getLastPathComponent().equals(attributeNode)));
    }

    @Test
    public void whenDeleteAttributeAndSelectedPathIsNull_ThenDoesNothing() {

        this.model.resetSelectedPath();

        this.model.deleteAttribute(new Attribute("anid", "aname"));

        assertFalse(this.treeModelListener.hasRemoveEventBeenRaised());
    }

    @Test
    public void whenDeleteAttributeAndSelectedPathIsNotAPlaceholder_ThenDoesNothing() {

        this.model.setSelectedPath(new TreePath(new Object()));

        this.model.deleteAttribute(new Attribute("anid", "aname"));

        assertFalse(this.treeModelListener.hasRemoveEventBeenRaised());
    }

    @Test
    public void whenDeleteAttributeAndNoParentPath_ThenDoesNothing() {

        var attributeNode = new Attribute("anid", "aname");
        this.model.setSelectedPath(new TreePath(attributeNode));

        this.model.deleteAttribute(attributeNode);

        assertFalse(this.treeModelListener.hasRemoveEventBeenRaised());
    }

    @Test
    public void whenDeleteAttributeWithOnlyAttribute_ThenRaisesEventAndSelectsPlaceholderFolder() {

        var parentNode = new PatternElement("aparentid", "aname");
        var attributeNode = new Attribute("anattributeid", "anattributename");
        parentNode.addAttribute(attributeNode);
        var folderNode = new PatternFolderPlaceholderNode(parentNode, parentNode.getAttributes(), "adisplayname");

        this.model.setSelectedPath(new TreePath(new Object[]{parentNode, folderNode, attributeNode}));

        assertEquals(1, parentNode.getAttributes().size());

        this.model.deleteAttribute(attributeNode);

        assertTrue(this.treeModelListener.hasRemoved(0, attributeNode));
        assertEquals(0, parentNode.getAttributes().size());
        Mockito.verify(this.treeSelector).selectPath(argThat(treePath -> treePath.getLastPathComponent().equals(folderNode)));
    }

    @Test
    public void whenDeleteAttributeWithOtherAttributeAfter_ThenRaisesEventAndSelectsOtherAttribute() {

        var parentNode = new PatternElement("aparentid", "aname");
        var attributeNode1 = new Attribute("anattributeid1", "anattributename1");
        var attributeNode2 = new Attribute("anattributeid2", "anattributename2");
        parentNode.addAttribute(attributeNode1);
        parentNode.addAttribute(attributeNode2);
        var folderNode = new PatternFolderPlaceholderNode(parentNode, parentNode.getAttributes(), "adisplayname");

        this.model.setSelectedPath(new TreePath(new Object[]{parentNode, folderNode, attributeNode1}));

        assertEquals(2, parentNode.getAttributes().size());

        this.model.deleteAttribute(attributeNode1);

        assertTrue(this.treeModelListener.hasRemoved(0, attributeNode1));
        assertEquals(1, parentNode.getAttributes().size());
        Mockito.verify(this.treeSelector).selectPath(argThat(treePath -> treePath.getLastPathComponent().equals(attributeNode2)));
    }

    @Test
    public void whenDeleteAttributeWithOtherAttributeBefore_ThenRaisesEventAndSelectsOtherAttribute() {

        var parentNode = new PatternElement("aparentid", "aname");
        var attributeNode1 = new Attribute("anattributeid1", "anattributename1");
        var attributeNode2 = new Attribute("anattributeid2", "anattributename2");
        parentNode.addAttribute(attributeNode1);
        parentNode.addAttribute(attributeNode2);
        var folderNode = new PatternFolderPlaceholderNode(parentNode, parentNode.getAttributes(), "adisplayname");

        this.model.setSelectedPath(new TreePath(new Object[]{parentNode, folderNode, attributeNode2}));

        assertEquals(2, parentNode.getAttributes().size());

        this.model.deleteAttribute(attributeNode2);

        assertTrue(this.treeModelListener.hasRemoved(1, attributeNode2));
        assertEquals(1, parentNode.getAttributes().size());
        Mockito.verify(this.treeSelector).selectPath(argThat(treePath -> treePath.getLastPathComponent().equals(attributeNode1)));
    }
}
