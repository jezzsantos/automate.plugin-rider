package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.editor.colors.EditorFontCache;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.Colors;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.SimpleMessageBusConnection;
import com.intellij.util.messages.Topic;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntryType;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.AutomateToolWindowFactory;
import jezzsantos.automate.plugin.infrastructure.ui.actions.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
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
    private Tree patternsTree;
    private JTextPane cliLog;
    private JSplitPane windowSplit;

    public AutomateToolWindow(
            @NotNull Project project, AutomateToolWindowFactory factory) {
        this.project = project;
        this.application = IAutomateApplication.getInstance(this.project);
        this.messageBus = project.getMessageBus();

        this.init();
    }

    @NotNull
    public JPanel getContent() {
        return mainPanel;
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
        actions.add(new AdvancedOptionsToolbarActionGroup());
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
        patternsTree.getEmptyText()
                .setText(AutomateBundle.message("toolWindow.EmptyPatterns"));

        var root = ((DefaultMutableTreeNode) patternsTree.getModel().getRoot());
        root.setUserObject(new DefaultMutableTreeNode(AutomateBundle.message("toolWindow.RootNode.Title")));
        this.refreshTree();
    }

    private void displayLogEntry(CliLogEntry entry) {
        var attributes = new SimpleAttributeSet();
        if (entry.Type != CliLogEntryType.Normal) {
            StyleConstants.setForeground(attributes, entry.Type == CliLogEntryType.Error ? Colors.DARK_RED : Colors.DARK_GREEN);
            StyleConstants.setBold(attributes, entry.Type == CliLogEntryType.Error);
        }
        var text = String.format("%s%s", entry.Text, System.lineSeparator());
        var document = cliLog.getDocument();
        Try.safely(() -> document.insertString(document.getLength(), text, attributes));
    }

    private void refreshTree() {
        var model = (DefaultTreeModel) patternsTree.getModel();
        var root = ((DefaultMutableTreeNode) model.getRoot());
        root.removeAllChildren();

        var patterns = this.application.listPatterns();
        for (var pattern : patterns) {
            root.add(new DefaultMutableTreeNode(pattern));
        }

        model.reload();
        patternsTree.expandRow(0);
    }

    @Override
    public void dispose() {
        this.application.removeConfigurationListener(configurationChangedListener());
        this.application.removePropertyListener(cliLogUpdatedListener());
    }
}

