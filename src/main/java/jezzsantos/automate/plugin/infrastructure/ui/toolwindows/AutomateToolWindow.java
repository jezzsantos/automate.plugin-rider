package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.editor.colors.EditorFontCache;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.*;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.SimpleMessageBusConnection;
import com.intellij.util.messages.Topic;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntryType;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftDetailed;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.AutomateToolWindowFactory;
import jezzsantos.automate.plugin.infrastructure.ui.actions.*;
import jezzsantos.automate.plugin.infrastructure.ui.components.AutomateTree;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.beans.PropertyChangeListener;
import java.util.List;

interface StateChangedListener {

    Topic<StateChangedListener> TOPIC = new Topic<>(StateChangedListener.class, Topic.BroadcastDirection.TO_CHILDREN);

    void settingsChanged();
}

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class AutomateToolWindow implements Disposable {

    @NotNull
    private final Project project;
    @NotNull
    private final IAutomateApplication application;
    @NotNull
    private final MessageBus messageBus;
    private JPanel mainPanel;
    private ActionToolbarImpl toolbar;
    private AutomateTree patternsTree;
    private JTextPane cliLog;
    private JSplitPane windowSplit;
    private TreeSelectionListener currentSelectionListener;

    public AutomateToolWindow(@NotNull Project project, AutomateToolWindowFactory factory) {
        this.project = project;
        this.application = IAutomateApplication.getInstance(this.project);
        this.messageBus = project.getMessageBus();

        this.init();
    }

    @NotNull
    public JPanel getContent() {
        return mainPanel;
    }

    @Override
    public void dispose() {
        this.application.removeConfigurationListener(configurationChangedListener());
        this.application.removePropertyListener(cliLogUpdatedListener());
    }

    private void createUIComponents() {

        toolbar = createToolbar();
    }

    private void init() {
        initTree();
        setupActionNotifications();
        setupCliLogs();
    }

    @NotNull
    private ActionToolbarImpl createToolbar() {
        final Runnable notify = notifyUpdated();

        final var actions = new DefaultActionGroup();
        actions.add(new TogglePatternEditingModeAction(notify));
        actions.add(new ToggleDraftEditingModeAction(notify));
        actions.addSeparator();
        actions.add(new PatternsListToolbarAction(notify));
        actions.add(new AddPatternAction(notify));
        actions.add(new InstallToolkitToolbarAction(notify));
        actions.add(new DraftsListToolbarAction(notify));
        actions.add(new AddDraftAction(notify));
        actions.add(new RefreshPatternsAction(notify));
        actions.addSeparator();
        actions.add(new ShowSettingsToolbarAction());
        actions.add(new AdvancedOptionsToolbarActionGroup(notify));
        actions.addSeparator();
        actions.add(new ToggleAuthoringModeToolbarAction(notify));

        var actionToolbar = new ActionToolbarImpl(ActionPlaces.TOOLWINDOW_CONTENT, actions, true);
        actionToolbar.setTargetComponent(mainPanel);
        actionToolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);

        return actionToolbar;
    }

    private void setupCliLogs() {
        this.application.addConfigurationListener(configurationChangedListener());
        this.application.addPropertyListener(cliLogUpdatedListener());
        displayCliLogPane(this.application.getViewCliLog());

        var entries = this.application.getCliLogEntries();
        for (var entry : entries) {
            displayLogEntry(entry);
        }
        cliLog.setFont(EditorFontCache.getInstance().getFont(EditorFontType.CONSOLE_PLAIN));
    }

    @NotNull
    private PropertyChangeListener configurationChangedListener() {
        return event -> {
            if (event.getPropertyName().equalsIgnoreCase("ViewCliLog")) {
                displayCliLogPane((boolean) event.getNewValue());
            }
        };
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private PropertyChangeListener cliLogUpdatedListener() {
        return event -> {
            if (event.getPropertyName().equalsIgnoreCase("CliLogs")) {
                var latestEntries = ((List<CliLogEntry>) event.getNewValue());
                for (var entry : latestEntries) {
                    displayLogEntry(entry);
                }
            }
        };
    }

    private void displayCliLogPane(boolean isVisible) {
        windowSplit.getBottomComponent().setVisible(isVisible);
        windowSplit.setEnabled(isVisible);
        var splitter = (BasicSplitPaneUI) windowSplit.getUI();
        splitter.getDivider().setVisible(isVisible);
        if (isVisible) {
            windowSplit.setDividerLocation(0.5d);
        }
    }

    @NotNull
    private Runnable notifyUpdated() {
        return () -> this.messageBus.syncPublisher(StateChangedListener.TOPIC).settingsChanged();
    }

    private void setupActionNotifications() {
        SimpleMessageBusConnection connection = this.messageBus.connect(this);
        connection.subscribe(StateChangedListener.TOPIC, this::refreshTree);
    }

    private void initTree() {

        PopupHandler.installPopupMenu(patternsTree, addTreeContextMenu(), "TreePopup");
        patternsTree.setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                if (value.getClass() == TreePlaceholder.class) {
                    setIcon(AllIcons.Nodes.Folder);
                    append(value.toString(), SimpleTextAttributes.REGULAR_ITALIC_ATTRIBUTES);
                }
                else {
                    if (value.getClass() == PatternElement.class) {
                        setIcon(AllIcons.General.ProjectStructure);
                        append(value.toString());
                    }
                    else {
                        if (value.getClass() == DraftDetailed.class) {
                            setIcon(AllIcons.Actions.GeneratedFolder);
                            append(value.toString());
                        }
                        else {
                            append(value.toString());
                        }
                    }
                }
            }
        });
        this.refreshTree();
    }

    private void displayLogEntry(CliLogEntry entry) {
        var attributes = new SimpleAttributeSet();
        if (entry.Type != CliLogEntryType.Normal) {
            StyleConstants.setForeground(attributes, entry.Type == CliLogEntryType.Error
                    ? DarculaColors.RED
                    : Colors.DARK_GREEN);
            StyleConstants.setBold(attributes, entry.Type == CliLogEntryType.Error);
        }
        var text = String.format("%s%s", entry.Text, System.lineSeparator());
        var document = cliLog.getDocument();
        Try.safely(() -> document.insertString(document.getLength(), text, attributes));
    }

    private void refreshTree() {
        patternsTree.setModel(null);
        if (this.currentSelectionListener != null) {
            patternsTree.removeTreeSelectionListener(this.currentSelectionListener);
        }
        var editingMode = application.getEditingMode();
        patternsTree.getEmptyText().setText(editingMode == EditingMode.Patterns
                                                    ? AutomateBundle.message("toolWindow.EmptyPatterns.Message")
                                                    : AutomateBundle.message("toolWindow.EmptyDrafts.Message"));
        patternsTree.invalidate();

        if (editingMode == EditingMode.Patterns) {
            var currentPattern = application.getCurrentPatternInfo();
            if (currentPattern != null) {
                var pattern = Try.safely(application::getCurrentPatternDetailed);
                if (pattern != null) {
                    var model = new PatternTreeModel(pattern);
                    this.currentSelectionListener = new PatternModelTreeSelectionListener(model);
                    patternsTree.setModel(model);
                    patternsTree.addTreeSelectionListener(this.currentSelectionListener);
                }
            }
        }
        else {
            var currentDraft = application.getCurrentDraftInfo();
            if (currentDraft != null) {
                var draft = Try.safely(application::getCurrentDraftDetailed);
                if (draft != null) {
                    var model = new DraftTreeModel(draft);
                    this.currentSelectionListener = new DraftModelTreeSelectionListener(model);
                    patternsTree.setModel(model);
                    patternsTree.addTreeSelectionListener(this.currentSelectionListener);

                }
            }
        }
    }

    @NotNull
    private ActionGroup addTreeContextMenu() {
        final Runnable notify = notifyUpdated();

        var actions = new DefaultActionGroup();

        actions.add(new AddAttributeAction(consumer -> consumer.accept((PatternTreeModel) patternsTree.getModel())));
        actions.addSeparator();
        actions.add(new DeleteAttributeAction(consumer -> consumer.accept((PatternTreeModel) patternsTree.getModel())));

        return actions;
    }

    private static class PatternModelTreeSelectionListener implements TreeSelectionListener {

        @NotNull
        private final PatternTreeModel model;

        public PatternModelTreeSelectionListener(@NotNull PatternTreeModel model) {this.model = model;}

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            var path = e.getNewLeadSelectionPath();
            if (path == null) {
                model.resetSelectedPath();
                return;
            }
            var selectedPath = e.getPath();
            model.setSelectedPath(selectedPath);
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
                return;
            }
            var selectedItem = e.getPath().getLastPathComponent();
            //model.setSelection(selectedItem);
        }
    }
}

