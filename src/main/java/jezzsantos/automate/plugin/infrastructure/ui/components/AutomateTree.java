package jezzsantos.automate.plugin.infrastructure.ui.components;

import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.ide.DefaultTreeExpander;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.Tree;
import jezzsantos.automate.plugin.AutomateIcons;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.application.interfaces.patterns.Automation;
import jezzsantos.automate.plugin.application.interfaces.patterns.CodeTemplate;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.infrastructure.ui.AutomateColors;
import jezzsantos.automate.plugin.infrastructure.ui.actions.drafts.*;
import jezzsantos.automate.plugin.infrastructure.ui.actions.patterns.*;
import jezzsantos.automate.plugin.infrastructure.ui.actions.toolkits.UpgradeToolkitAction;
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
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Objects;

import static java.awt.event.KeyEvent.VK_ENTER;

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
                        var model = new DraftTreeModel(new AutomateTreeSelector(this), draft, toolkit.getPattern());
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
        var actionGroup = addTreeContextMenu();
        PopupHandler.installPopupMenu(this, actionGroup, "TreePopupMenu");
        DoubleClickHandler.install(this, actionGroup, "TreeDoubleClick");
        this.setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

                var editingMode = AutomateTree.this.application.getEditingMode();
                if (editingMode == EditingMode.PATTERNS) {
                    if (value instanceof PatternFolderPlaceholderNode placeholder) {
                        setIcon(AllIcons.Nodes.Folder);
                        append(placeholder.toString(), SimpleTextAttributes.REGULAR_ITALIC_ATTRIBUTES);
                    }
                    else {
                        if (value instanceof PatternElement element) {
                            setIcon(element.isRoot()
                                      ? AllIcons.General.ProjectStructure
                                      : element.isCollection()
                                        ? AllIcons.Debugger.Frame
                                        : AllIcons.Windows.Maximize);
                            append(element.toString());
                            var description = element.getDescription();
                            if (description.isEmpty()) {
                                setToolTipText(element.isCollection()
                                                 ? AutomateBundle.message("toolWindow.Tree.Pattern.Collection.Tooltip")
                                                 : AutomateBundle.message("toolWindow.Tree.Pattern.Element.Tooltip"));
                            }
                            else {
                                setToolTipText(description);
                            }
                        }
                        else {
                            if (value instanceof Attribute attribute) {
                                setIcon(AllIcons.Gutter.ExtAnnotation);
                                setToolTipText(AutomateBundle.message("toolWindow.Tree.Pattern.Attribute.Tooltip"));
                                append(attribute.toString());
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
                                    append(automation.toString());
                                }
                                else {
                                    if (value instanceof CodeTemplate codeTemplate) {
                                        setIcon(AllIcons.Nodes.Template);
                                        setToolTipText(AutomateBundle.message("toolWindow.Tree.Pattern.CodeTemplate.Tooltip"));
                                        append(codeTemplate.toString());
                                    }
                                }
                            }
                        }
                    }
                }

                if (editingMode == EditingMode.DRAFTS) {
                    if (value instanceof DraftIncompatiblePlaceholderNode placeholder) {
                        if (placeholder.isDraftIncompatible()) {
                            setIcon(AutomateIcons.StatusWarning);
                            setToolTipText(AutomateBundle.message("toolWindow.Tree.Draft.UpgradeDraft.Tooltip"));
                            append(placeholder.toString(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_ITALIC, AutomateColors.getWarningText()));
                        }
                        if (placeholder.isRuntimeIncompatible()) {
                            setIcon(AutomateIcons.StatusError);
                            setToolTipText(AutomateBundle.message("toolWindow.Tree.Draft.UpgradeToolkit.Tooltip"));
                            append(placeholder.toString(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_ITALIC, AutomateColors.getErrorText()));
                        }
                    }
                    else {
                        if (value instanceof DraftElementPlaceholderNode placeholder) {
                            setIcon(placeholder.isCollectionItem()
                                      ? AllIcons.Actions.DynamicUsages
                                      : AllIcons.Debugger.Db_muted_breakpoint);
                            setToolTipText(placeholder.isCollectionItem()
                                             ? AutomateBundle.message("toolWindow.Tree.Draft.CollectionItem.Tooltip")
                                             : AutomateBundle.message("toolWindow.Tree.Draft.Element.Tooltip"));
                            append(placeholder.toString());
                        }
                        else {
                            if (value instanceof DraftPropertyPlaceholderNode placeholder) {
                                setIcon(AllIcons.Gutter.ExtAnnotation);
                                setToolTipText(AutomateBundle.message("toolWindow.Tree.Draft.Property.Tooltip"));
                                if (isNotablePropertyName(placeholder.getProperty().getName())) {
                                    append(placeholder.toString(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD, AutomateColors.getNormalText()));
                                }
                                else {
                                    append(placeholder.toString());
                                }
                            }
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

    private void setGuidance(@NotNull String text) {

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

        var upgradeDraft = new UpgradeDraftAction(this::refreshTree);
        actions.add(upgradeDraft);
        var upgradeToolkit = new UpgradeToolkitAction(this::refreshTree);
        actions.add(upgradeToolkit);

        actions.addSeparator();

        //Add actions
        var addPatternElement = new AddPatternElementAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        addPatternElement.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_INSERT), this);
        actions.add(addPatternElement);
        var addPatternAttribute = new AddPatternAttributeAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        addPatternAttribute.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_INSERT), this);
        actions.add(addPatternAttribute);
        var addPatternCodeTemplate = new AddPatternCodeTemplateAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        addPatternCodeTemplate.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_INSERT), this);
        actions.add(addPatternCodeTemplate);
        var addPatternCodeTemplateCommand = new AddPatternCodeTemplateCommandAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        addPatternCodeTemplateCommand.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_INSERT), this);
        actions.add(addPatternCodeTemplateCommand);
        var addPatternCliCommand = new AddPatternCliCommandAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        addPatternCliCommand.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_INSERT), this);
        actions.add(addPatternCliCommand);
        var addPatternCommandLaunchPoint = new AddPatternCommandLaunchPointAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        addPatternCommandLaunchPoint.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_INSERT), this);
        actions.add(addPatternCommandLaunchPoint);
        var addDraftElement = new ListDraftElementsActionGroup(consumer -> consumer.accept((DraftTreeModel) this.getModel()));
        addDraftElement.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_INSERT), this);
        actions.add(addDraftElement);

        // Edit actions
        var editPatternAttribute = new EditPatternAttributeAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        editPatternAttribute.registerCustomShortcutSet(getKeyboardShortcut(VK_ENTER), this);
        actions.add(editPatternAttribute);
        var editPatternElement = new EditPatternElementAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        editPatternElement.registerCustomShortcutSet(getKeyboardShortcut(VK_ENTER), this);
        actions.add(editPatternElement);
        var editPatternCodeTemplateContent = new EditPatternCodeTemplateContentAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        editPatternCodeTemplateContent.registerCustomShortcutSet(getKeyboardShortcut(VK_ENTER), this);
        actions.add(editPatternCodeTemplateContent);
        var editPatternCodeTemplateCommand = new EditPatternCodeTemplateCommandAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        editPatternCodeTemplateCommand.registerCustomShortcutSet(getKeyboardShortcut(VK_ENTER), this);
        actions.add(editPatternCodeTemplateCommand);
        var editPatternCliCommand = new EditPatternCliCommandAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        editPatternCliCommand.registerCustomShortcutSet(getKeyboardShortcut(VK_ENTER), this);
        actions.add(editPatternCliCommand);
        var editPatternCommandLaunchPoint = new EditPatternCommandLaunchPointAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        editPatternCommandLaunchPoint.registerCustomShortcutSet(getKeyboardShortcut(VK_ENTER), this);
        actions.add(editPatternCommandLaunchPoint);
        var editDraftElement = new EditDraftElementAction(consumer -> consumer.accept((DraftTreeModel) this.getModel()));
        editDraftElement.registerCustomShortcutSet(getKeyboardShortcut(VK_ENTER), this);
        actions.add(editDraftElement);

        actions.addSeparator();

        //Delete actions
        var deletePatternAttribute = new DeletePatternAttributeAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        deletePatternAttribute.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_DELETE), this);
        actions.add(deletePatternAttribute);
        var deletePatternElement = new DeletePatternElementAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        deletePatternElement.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_DELETE), this);
        actions.add(deletePatternElement);
        var deletePatternCodeTemplate = new DeletePatternCodeTemplateAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        deletePatternCodeTemplate.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_DELETE), this);
        actions.add(deletePatternCodeTemplate);
        var deletePatternCodeTemplateCommand = new DeletePatternCodeTemplateCommandAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        deletePatternCodeTemplateCommand.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_DELETE), this);
        actions.add(deletePatternCodeTemplateCommand);
        var deletePatternCliCommand = new DeletePatternCliCommandAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        deletePatternCliCommand.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_DELETE), this);
        actions.add(deletePatternCliCommand);
        var deletePatternCommandLaunchPoint = new DeletePatternCommandLaunchPointAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        deletePatternCommandLaunchPoint.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_DELETE), this);
        actions.add(deletePatternCommandLaunchPoint);
        var deleteDraftElement = new DeleteDraftElementAction(consumer -> consumer.accept((DraftTreeModel) this.getModel()));
        deleteDraftElement.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_DELETE), this);
        actions.add(deleteDraftElement);
        var deleteDraft = new DeleteDraftAction(this::refreshTree);
        deleteDraft.registerCustomShortcutSet(getKeyboardShortcut(KeyEvent.VK_DELETE), this);
        actions.add(deleteDraft);

        actions.addSeparator();

        // Other actions
        var executeDraftLaunchPoints = new ListDraftLaunchPointsActionGroup(consumer -> consumer.accept((DraftTreeModel) this.getModel()));
        actions.add(executeDraftLaunchPoints);
        var publishPattern = new PublishPatternAction(consumer -> consumer.accept((PatternTreeModel) this.getModel()));
        actions.add(publishPattern);

        return actions;
    }

    private ShortcutSet getKeyboardShortcut(int key) {

        return new CustomShortcutSet(KeyStroke.getKeyStroke(key, 0));
    }

    private boolean isNotablePropertyName(@NotNull String name) {

        return Objects.equals(name, "Id") || Objects.equals(name, "Name");
    }

    @SuppressWarnings("ClassCanBeRecord")
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

    @SuppressWarnings("ClassCanBeRecord")
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

    /**
     * Adds a double click {@code MouseListener}, to the component, that executes the first enabled action (of the {@code ActionGroup}) that is registered with the {@code KeyEvent.VK_ENTER} key.
     */
    private static class DoubleClickHandler {

        public static void install(@NotNull Component component, @NotNull ActionGroup actionGroup, @NotNull String place) {

            component.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                    if (!IsDoubleClickEvent(e, MouseEvent.BUTTON1)) {
                        return;
                    }

                    var actionEvent = toActionEvent(e, place, component);
                    var action = getFirstEnabledActionForKeyStroke(actionGroup, actionEvent, KeyEvent.VK_ENTER);
                    if (action != null) {
                        action.actionPerformed(actionEvent);
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    // ignore
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    // ignore
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    // ignore
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    // ignore
                }
            });
        }

        @NotNull
        private static AnActionEvent toActionEvent(@NotNull MouseEvent e, @NotNull String place, @NotNull Component component) {

            return AnActionEvent.createFromInputEvent(e, place, null, DataManager.getInstance().getDataContext(component));
        }

        @SuppressWarnings("SameParameterValue")
        @Nullable
        private static AnAction getFirstEnabledActionForKeyStroke(@NotNull ActionGroup actionGroup, @NotNull AnActionEvent actionEvent, int keyEvent) {

            var allActions = ActionGroupUtil.getActiveActions(actionGroup, actionEvent);
            var actionsWithKeyEvent = allActions.filter(action -> {
                var shortcuts = action.getShortcutSet().getShortcuts();

                return Arrays.stream(shortcuts).anyMatch(shortcut -> {
                    if (!shortcut.isKeyboard()) {
                        return false;
                    }
                    var firstKeyStroke = ((KeyboardShortcut) shortcut).getFirstKeyStroke().getKeyCode();
                    return firstKeyStroke == keyEvent;
                });
            }).collect();
            if (actionsWithKeyEvent.isNotEmpty()) {
                return actionsWithKeyEvent.first();
            }
            else {
                return null;
            }
        }

        @SuppressWarnings("SameParameterValue")
        private static boolean IsDoubleClickEvent(@NotNull MouseEvent e, int mouseEvent) {

            var mouseButton = e.getButton();
            return mouseButton == mouseEvent
              && e.getClickCount() == 2;
        }
    }
}
