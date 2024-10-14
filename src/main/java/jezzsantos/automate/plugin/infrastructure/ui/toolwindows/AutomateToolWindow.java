package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.toolbarLayout.ToolbarLayoutStrategy;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.jetbrains.rider.settings.codeCleanup.TreeActionsGroup;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.infrastructure.ui.components.AutomateToolbar;
import jezzsantos.automate.plugin.infrastructure.ui.components.AutomateTree;
import jezzsantos.automate.plugin.infrastructure.ui.components.CliLogsPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class AutomateToolWindow implements Disposable {

    @NotNull
    private final Project project;
    @NotNull
    private final IAutomateApplication application;
    @NotNull
    private final ToolWindow toolwindow;
    private JPanel mainPanel;
    private AutomateToolbar toolbar;
    private AutomateTree tree;
    private CliLogsPane cliLogsPane;
    private JSplitPane splitter;

    public AutomateToolWindow(@NotNull Project project, @NotNull ToolWindow toolwindow) {

        this.project = project;
        this.toolwindow = toolwindow;
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

    private void createUIComponents() {

        this.tree = new AutomateTree(this.project);
        this.toolbar = new AutomateToolbar(this.project, this.tree, ActionPlaces.TOOLWINDOW_CONTENT, true);
        this.toolbar.setTargetComponent(this.mainPanel);
        this.toolbar.setLayoutStrategy(ToolbarLayoutStrategy.NOWRAP_STRATEGY);
        this.cliLogsPane = new CliLogsPane(this.project);
    }

    private void init() {

        this.application.addConfigurationListener(viewCliLogsChangedListener());
        displayCliLogsPane(this.application.getViewCliLog());
        initToolWindow();
    }

    private void initToolWindow() {

        var expandCollapseActions = new ArrayList<>(List.of(new TreeActionsGroup(this.tree).getChildActionsOrStubs()));
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
}

