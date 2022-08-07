package jezzsantos.automate.ui;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import jezzsantos.automate.ui.components.OptionsToolbarAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Calendar;

public class AutomateToolWindow {
    @NotNull
    private final Project project;
    @NotNull
    private final ToolWindow toolWindow;
    private JPanel mainPanel;
    private ActionToolbarImpl toolbar;
    private JButton refreshButton;
    private JLabel currentTime;

    public AutomateToolWindow(
            @NotNull Project project, ToolWindow toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;

        refreshButton.addActionListener(e -> refreshContents());

        this.refreshContents();
    }

    @NotNull
    public JPanel getContent() {
        return mainPanel;
    }

    private void createUIComponents() {

        toolbar = createToolbar();
    }

    @NotNull
    private ActionToolbarImpl createToolbar() {
        final DefaultActionGroup actionGroup = new DefaultActionGroup();

        actionGroup.add(new OptionsToolbarAction(() -> toolbar));
        actionGroup.addSeparator();

        return new ActionToolbarImpl("automate", actionGroup, false);
    }

    public void refreshContents() {
        Calendar instance = Calendar.getInstance();
        int min = instance.get(Calendar.MINUTE);
        String strMin = min < 10 ? "0" + min : String.valueOf(min);
        currentTime.setText(instance.get(Calendar.HOUR_OF_DAY) + ":" + strMin);
        //currentTime.setIcon(new ImageIcon(getClass().getResource("/toolWindow/Time-icon.png")));
    }

}

