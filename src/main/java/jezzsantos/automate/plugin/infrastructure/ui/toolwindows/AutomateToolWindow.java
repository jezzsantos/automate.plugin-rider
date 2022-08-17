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
import com.intellij.util.messages.SimpleMessageBusConnection;
import com.intellij.util.messages.Topic;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntryType;
import jezzsantos.automate.plugin.application.services.interfaces.IConfiguration;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import jezzsantos.automate.plugin.infrastructure.ui.actions.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.List;

interface StateChangedListener {
    Topic<StateChangedListener> TOPIC = new Topic<>(StateChangedListener.class, Topic.BroadcastDirection.TO_CHILDREN);

    void settingsChanged();
}

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class AutomateToolWindow implements Disposable {
    @NotNull
    private final Project project;
    private JPanel mainPanel;
    private ActionToolbarImpl toolbar;
    private Tree patternsTree;
    private JTextPane cliLog;
    private JSplitPane windowSplit;

    public AutomateToolWindow(
            @NotNull Project project) {
        this.project = project;

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
        initCliLog();
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
        var configuration = IConfiguration.getInstance(project);
        configuration.addListener(evt -> {
            if (evt.getPropertyName().equals("ViewCliLog")) {
                displayCliLogs();
            }
        });
        displayCliLogs();
    }

    private void displayCliLogs() {
        var configuration = IConfiguration.getInstance(project);
        var isVisible = configuration.getViewCliLog();
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
        return () -> this.project.getMessageBus().syncPublisher(StateChangedListener.TOPIC).settingsChanged();
    }

    private void setupActionNotifications() {
        var messageBus = project.getMessageBus();
        SimpleMessageBusConnection connection = messageBus.connect(this);
        connection.subscribe(StateChangedListener.TOPIC, this::refreshTree);
    }

    private void initTree() {
        patternsTree.getEmptyText()
                .setText(AutomateBundle.message("toolWindow.EmptyPatterns"));

        var root = ((DefaultMutableTreeNode) patternsTree.getModel().getRoot());
        root.setUserObject(new DefaultMutableTreeNode(AutomateBundle.message("toolWindow.RootNode.Title")));
        this.refreshTree();
    }

    @SuppressWarnings("unchecked")
    private void initCliLog() {
        var application = IAutomateApplication.getInstance(project);
        application.addCliLogListener(e -> {
            if (e.getPropertyName().equals("CliLogs")) {
                var entries = (List<CliLogEntry>) e.getNewValue();
                displayLogEntry(entries.get(0));
            }
        });

        var log = application.getCliLog();
        for (var entry : log) {
            displayLogEntry(entry);
        }
        cliLog.setFont(EditorFontCache.getInstance().getFont(EditorFontType.CONSOLE_PLAIN));
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

        var patterns = IAutomateApplication.getInstance(project).getPatterns();
        for (var pattern : patterns) {
            root.add(new DefaultMutableTreeNode(pattern));
        }

        model.reload();
        patternsTree.expandRow(0);
    }

    @Override
    public void dispose() {
    }
}

