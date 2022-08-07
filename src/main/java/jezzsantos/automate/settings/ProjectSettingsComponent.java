package jezzsantos.automate.settings;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;
import jezzsantos.automate.AutomateBundle;

import javax.swing.*;

public class ProjectSettingsComponent {

    private final JPanel minPanel;
    private final JBCheckBox developerMode = new JBCheckBox(AutomateBundle.message("settings.DeveloperMode.Title"));

    public ProjectSettingsComponent() {
        minPanel = FormBuilder.createFormBuilder()
                .addComponent(developerMode, 1)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }


    public JPanel getPanel() {
        return minPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return developerMode;
    }

    public boolean getDeveloperMode() {
        return developerMode.isSelected();
    }

    public void setDeveloperMode(boolean value) {
        developerMode.setSelected(value);
    }
}
