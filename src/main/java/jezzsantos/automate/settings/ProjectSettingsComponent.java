package jezzsantos.automate.settings;

import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

public class ProjectSettingsComponent {

    private final JPanel minPanel;

    public ProjectSettingsComponent() {
        minPanel = FormBuilder.createFormBuilder()
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return minPanel;
    }
}
