package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.Topic;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.infrastructure.ui.actions.*;
import jezzsantos.automate.plugin.infrastructure.ui.actions.drafts.AddDraftAction;
import jezzsantos.automate.plugin.infrastructure.ui.actions.drafts.DraftsListToolbarAction;
import jezzsantos.automate.plugin.infrastructure.ui.actions.patterns.AddPatternAction;
import jezzsantos.automate.plugin.infrastructure.ui.actions.patterns.PatternsListToolbarAction;
import jezzsantos.automate.plugin.infrastructure.ui.actions.toolkits.InstallToolkitToolbarAction;
import jezzsantos.automate.plugin.infrastructure.ui.components.AutomateTree;
import jezzsantos.automate.plugin.infrastructure.ui.components.CliLogsPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

interface StateChangedListener {

    Topic<StateChangedListener> TOPIC = new Topic<>(
      StateChangedListener.class, Topic.BroadcastDirection.TO_CHILDREN);

    void settingsChanged();
}

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class AutomateToolWindow implements Disposable {

    @NotNull
    private final Project project;
    @NotNull
    private final IAutomateApplication application;
    @NotNull
    private final ToolWindow toolwindow;
    @NotNull
    private final MessageBus messageBus;
    private JPanel mainPanel;
    private SimpleToolWindowPanel toolbar;
    private AutomateTree tree;
    private CliLogsPane cliLogsPane;
    private JSplitPane splitter;

    public AutomateToolWindow(@NotNull Project project, @NotNull ToolWindow toolwindow) {

        this.project = project;
        this.toolwindow = toolwindow;
        this.messageBus = project.getMessageBus();
        this.application = IAutomateApplication.getInstance(this.project);

        this.init();
    }

    @Override
    public void dispose() {

        this.application.removeConfigurationListener(viewCliLogsChangedListener());
        if (this.cliLogsPane != null) {
            this.cliLogsPane.dispose();
        }
        if (this.tree != null) {
            this.tree.dispose();
        }
    }

    @NotNull
    public JPanel getContent() {

        return this.mainPanel;
    }

    @NotNull
    private static DefaultActionGroup createActions(MessageBus messageBus, AutomateTree tree) {

        final Runnable update = notifyUpdated(messageBus, tree);

        final var actions = new DefaultActionGroup();

        actions.add(new TogglePatternEditingModeAction(update));
        actions.add(new ToggleDraftEditingModeAction(update));

        actions.addSeparator();

        actions.add(new PatternsListToolbarAction(update));
        actions.add(new AddPatternAction(update));
        actions.add(new InstallToolkitToolbarAction(update));
        actions.add(new DraftsListToolbarAction(update));
        actions.add(new AddDraftAction(update));
        actions.add(new RefreshAllAction(update));

        actions.addSeparator();

        actions.add(new ShowSettingsToolbarAction());
        actions.add(new AdvancedOptionsToolbarActionGroup(update));

        actions.addSeparator();

        actions.add(new ToggleAuthoringModeToolbarAction(update));

        return actions;
    }

    @NotNull
    private static Runnable notifyUpdated(MessageBus messageBus, AutomateTree tree) {

        return () -> {
            messageBus.syncPublisher(StateChangedListener.TOPIC).settingsChanged();
            tree.update();
        };
    }

    private void createUIComponents() {

        this.toolbar = new SimpleToolWindowPanel(true);
        this.tree = new AutomateTree(this.project);
        this.cliLogsPane = new CliLogsPane(this.project);
    }

    private void init() {

        this.application.addConfigurationListener(viewCliLogsChangedListener());
        displayCliLogsPane(this.application.getViewCliLog());
        initToolWindow();
    }

    private void initToolWindow() {

        var actionManager = ActionManager.getInstance();
        var actionToolbar = actionManager.createActionToolbar(ActionPlaces.TOOLWINDOW_CONTENT, createActions(this.messageBus, this.tree), true);
        this.toolbar.setToolbar(actionToolbar.getComponent());
        actionToolbar.setTargetComponent(this.mainPanel);

        var expandCollapseActions = new ArrayList<>(List.of(actionManager.getAction(IdeActions.ACTION_COLLAPSE_ALL), actionManager.getAction(IdeActions.ACTION_EXPAND_ALL)));
        Collections.reverse(expandCollapseActions);
        this.toolwindow.setTitleActions(expandCollapseActions);
    }

    @NotNull
    private PropertyChangeListener viewCliLogsChangedListener() {

        return event -> {
            if (event.getPropertyName().equalsIgnoreCase("ViewCliLog")) {
                displayCliLogsPane((boolean) event.getNewValue());
            }
        };
    }

    private void displayCliLogsPane(boolean isVisible) {

        this.splitter.getBottomComponent().setVisible(isVisible);
        this.splitter.setEnabled(isVisible);
        var splitter = (BasicSplitPaneUI) this.splitter.getUI();
        splitter.getDivider().setVisible(isVisible);
        if (isVisible) {
            this.splitter.setDividerLocation(0.5d);
        }
    }

    private void setupActionNotifications() {

        var connection = this.messageBus.connect();
        connection.subscribe(StateChangedListener.TOPIC, (StateChangedListener) this.tree::update);
    }
}

