package jezzsantos.automate.plugin.infrastructure.ui.components;

import com.intellij.icons.AllIcons;
import com.intellij.ide.DefaultTreeExpander;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.Tree;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.application.interfaces.patterns.Automation;
import jezzsantos.automate.plugin.application.interfaces.patterns.CodeTemplate;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.infrastructure.ui.actions.*;
import jezzsantos.automate.plugin.infrastructure.ui.toolwindows.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;

interface AutomateNotifier {

    void update();
}

public class AutomateTree extends Tree implements AutomateNotifier, DataProvider, Disposable {

    @NotNull
    private final IAutomateApplication application;
    @NotNull
    private final Project project;
    private TreeSelectionListener currentSelectionListener;
    private DefaultTreeExpander automateTreeExpander;

    public AutomateTree(@NotNull Project project) {

        this(project, IAutomateApplication.getInstance(project));
    }

    @TestOnly
    public AutomateTree(@NotNull Project project, @NotNull IAutomateApplication application) {

        this.project = project;
        this.application = application;

        init();
    }

    @Override
    public void update() {

        refreshTree();
    }

    @Override
    public void dispose() {

        this.application.removeConfigurationListener(executablePathChangedListener());
    }

    @Override
    public @Nullable Object getData(@NotNull @NonNls String dataId) {

        if (PlatformCoreDataKeys.SELECTED_ITEM.is(dataId)) {
            return getSelectionPath();
        }

        return null;
    }

    private void refreshTree() {

        this.setModel(new DefaultTreeModel(null));
        this.setExpandsSelectedPaths(true);
        if (this.currentSelectionListener != null) {
            this.removeTreeSelectionListener(this.currentSelectionListener);
        }
        var isInstalled = this.application.isCliInstalled();
        EditingMode editingMode = null;
        if (isInstalled) {
            editingMode = this.application.getEditingMode();
            setGuidance(editingMode == EditingMode.PATTERNS
                          ? AutomateBundle.message("toolWindow.EmptyPatterns.Message")
                          : AutomateBundle.message("toolWindow.EmptyDrafts.Message"));
        }
        else {
            setGuidance(AutomateBundle.message("toolWindow.StartupError.Message", this.application.getExecutableName()));
        }

        if (isInstalled) {
            if (editingMode == EditingMode.PATTERNS) {
                var currentPattern = this.application.getCurrentPatternInfo();
                if (currentPattern != null) {
                    var pattern = Try.andHandle(this.project, this.application::getCurrentPatternDetailed,
                                                AutomateBundle.message("general.AutomateTree.CurrentPattern.Failed.Message"));
                    if (pattern != null) {
                        var model = new PatternTreeModel(new AutomateTreeSelector(this), pattern);
                        this.currentSelectionListener = new PatternModelTreeSelectionListener(model);
                        this.setModel(model);
                        this.addTreeSelectionListener(this.currentSelectionListener);
                    }
                }
            }
            else {
                var currentDraft = this.application.getCurrentDraftInfo();
                if (currentDraft != null) {
                    var draft = Try.andHandle(this.project, this.application::getCurrentDraftDetailed,
                                              AutomateBundle.message("general.AutomateTree.CurrentDraft.Failed.Message"));
                    var toolkit = Try.andHandle(this.project, this.application::getCurrentToolkitDetailed,
                                                AutomateBundle.message("general.AutomateTree.CurrentToolkit.Failed.Message"));
                    if (draft != null && toolkit != null) {
                        var model = new DraftTreeModel(new AutomateTreeSelector(this), draft.getRoot(), toolkit.getPattern());
                        this.currentSelectionListener = new DraftModelTreeSelectionListener(model);
                        this.setModel(model);
                        this.addTreeSelectionListener(this.currentSelectionListener);
                    }
                }
            }

            this.automateTreeExpander.expandAll();
        }
    }

    private void init() {

        this.application.addConfigurationListener(executablePathChangedListener());
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.automateTreeExpander = new DefaultTreeExpander(this);
        PopupHandler.installPopupMenu(this, addTreeContextMenu(), "TreePopup");
        this.setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

                var editingMode = AutomateTree.this.application.getEditingMode();
                if (editingMode == EditingMode.PATTERNS) {
                    if (value instanceof PatternFolderPlaceholderNode) {
                        setIcon(AllIcons.Nodes.Folder);
                        append(value.toString(), SimpleTextAttributes.REGULAR_ITALIC_ATTRIBUTES);
                    }
                    else {
                        if (value instanceof PatternElement element) {
                            setIcon(element.isRoot()
                                      ? AllIcons.General.ProjectStructure
                                      : element.isCollection()
                                        ? AllIcons.Debugger.Frame
                                        : AllIcons.Windows.Maximize);
                            setToolTipText(element.isCollection()
                                             ? AutomateBundle.message("toolWindow.Tree.Pattern.Collection.Tooltip")
                                             : AutomateBundle.message("toolWindow.Tree.Pattern.Element.Tooltip"));
                            append(value.toString());
                        }
                        else {
                            if (value instanceof Attribute) {
                                setIcon(AllIcons.Gutter.ExtAnnotation);
                                setToolTipText(AutomateBundle.message("toolWindow.Tree.Pattern.Attribute.Tooltip"));
                                append(value.toString());
                            }
                            else {
                                if (value instanceof Automation automation) {
                                    switch (automation.getType()) {
                                        case CODE_TEMPLATE_COMMAND -> {
                                            setIcon(AllIcons.Actions.GeneratedFolder);
                                            setToolTipText(AutomateBundle.message("toolWindow.Tree.Pattern.CodeTemplateCommand.Tooltip"));
                                        }
                                        case CLI_COMMAND -> {
                                            setIcon(AllIcons.Debugger.Console);
                                            setToolTipText(AutomateBundle.message("toolWindow.Tree.Pattern.CliCommand.Tooltip"));
                                        }
                                        case COMMAND_LAUNCH_POINT -> {
                                            setIcon(AllIcons.Diff.MagicResolve);
                                            setToolTipText(AutomateBundle.message("toolWindow.Tree.Pattern.CommandLaunchPoint.Tooltip"));
                                        }
                                    }
                                    append(value.toString());
                                }
                                else {
                                    if (value instanceof CodeTemplate) {
                                        setIcon(AllIcons.Nodes.Template);
                                        setToolTipText(AutomateBundle.message("toolWindow.Tree.Pattern.CodeTemplate.Tooltip"));
                                        append(value.toString());
                                    }
                                }
                            }
                        }
                    }
                }

                if (editingMode == EditingMode.DRAFTS) {
                    if (value instanceof DraftElementPlaceholderNode placeholder) {
                        setIcon(placeholder.isCollectionItem()
                                  ? AllIcons.Actions.DynamicUsages
                                  : AllIcons.Debugger.Db_muted_breakpoint);
                        setToolTipText(placeholder.isCollectionItem()
                                         ? AutomateBundle.message("toolWindow.Tree.Draft.CollectionItem.Tooltip")
                                         : AutomateBundle.message("toolWindow.Tree.Draft.Element.Tooltip"));
                        append(value.toString());
                    }
                    else {
                        if (value instanceof DraftPropertyPlaceholderNode) {
                            setIcon(AllIcons.Gutter.ExtAnnotation);
                            setToolTipText(AutomateBundle.message("toolWindow.Tree.Draft.Property.Tooltip"));
                            append(value.toString());
                        }
                    }
                }
            }
        });
        this.refreshTree();
    }

    @NotNull
    private PropertyChangeListener executablePathChangedListener() {

        return event -> {
            if (event.getPropertyName().equalsIgnoreCase("ExecutablePath")) {
                this.refreshTree();
            }
        };
    }

    private void setGuidance(String text) {

        var statusText = this.getEmptyText();
        statusText.clear();
        var lines = text.split(System.lineSeparator());
        for (var line : lines) {
            statusText.appendLine(line);
        }
        this.invalidate();
    }

    @NotNull
    private ActionGroup addTreeContextMenu() {

        var actions = new DefaultActionGroup();

        var addPatternAttribute = new AddPatternAttributeAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        addPatternAttribute.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_INSERT), this);
        actions.add(addPatternAttribute);
        var addDraftElement = new ListDraftElementsActionGroup(consumer -> consumer.accept((DraftTreeModel) this.getModel()));
        addDraftElement.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_INSERT), this);
        actions.add(addDraftElement);
        var editPatternAttribute = new EditPatternAttributeAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        editPatternAttribute.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_ENTER), this);
        actions.add(editPatternAttribute);
        var editDraftElement = new EditDraftElementAction(consumer -> consumer.accept((DraftTreeModel) this.getModel()));
        editDraftElement.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_ENTER), this);
        actions.add(editDraftElement);

        actions.addSeparator();

        var deletePatternAttribute = new DeletePatternAttributeAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        deletePatternAttribute.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_DELETE), this);
        actions.add(deletePatternAttribute);
        var deleteDraftElement = new DeleteDraftElementAction(consumer -> consumer.accept((DraftTreeModel) this.getModel()));
        deleteDraftElement.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_DELETE), this);
        actions.add(deleteDraftElement);

        actions.addSeparator();

        var executeDraftLaunchPoints = new ListDraftLaunchPointsActionGroup(consumer -> consumer.accept((DraftTreeModel) this.getModel()));
        actions.add(executeDraftLaunchPoints);

        return actions;
    }

    private ShortcutSet getKeyboardShortcut(int key) {

        return new CustomShortcutSet(KeyStroke.getKeyStroke(key, 0));
    }

    private static class PatternModelTreeSelectionListener implements TreeSelectionListener {

        @NotNull
        private final PatternTreeModel model;

        public PatternModelTreeSelectionListener(@NotNull PatternTreeModel model) {this.model = model;}

        @Override
        public void valueChanged(TreeSelectionEvent e) {

            var path = e.getNewLeadSelectionPath();
            if (path == null) {
                this.model.resetSelectedPath();
                return;
            }
            var selectedPath = e.getPath();
            this.model.setSelectedPath(selectedPath);
        }
    }

    private static class DraftModelTreeSelectionListener implements TreeSelectionListener {

        @NotNull
        private final DraftTreeModel model;

        public DraftModelTreeSelectionListener(@NotNull DraftTreeModel model) {this.model = model;}

        @Override
        public void valueChanged(TreeSelectionEvent e) {

            var path = e.getNewLeadSelectionPath();
            if (path == null) {
                this.model.resetSelectedPath();
                return;
            }
            var selectedItem = e.getPath();
            this.model.setSelectedPath(selectedItem);
        }
    }
}
