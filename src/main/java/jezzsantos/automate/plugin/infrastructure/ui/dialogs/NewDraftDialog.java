package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.DraftDefinition;
import jezzsantos.automate.plugin.application.interfaces.ToolkitDefinition;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class NewDraftDialog extends DialogWrapper {

    private final List<DraftDefinition> drafts;
    public String Name;
    public String ToolkitName;
    private JPanel contents;
    private JTextField name;
    private JLabel nameTitle;
    private JLabel toolkitTitle;
    private JComboBox<ToolkitDefinition> toolkits;

    public NewDraftDialog(@Nullable Project project, List<ToolkitDefinition> installedToolkits, List<DraftDefinition> drafts) {
        super(project);
        this.drafts = drafts;

        this.init();
        this.setTitle(AutomateBundle.message("dialog.NewDraft.Title"));
        toolkitTitle.setText(AutomateBundle.message("dialog.NewDraft.Toolkit.Title"));
        nameTitle.setText(AutomateBundle.message("dialog.NewDraft.Name.Title"));
        toolkits.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            var label = new JLabel();
            if (value == null) {
                label.setText(AutomateBundle.message("dialog.NewDraft.NoToolkits.Title"));
            } else {
                label.setText(value.getName());
            }

            return label;
        });
        for (var toolkit : installedToolkits) {
            toolkits.addItem(toolkit);
        }
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contents;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return toolkits;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {
        var selectedToolkit = (ToolkitDefinition) this.toolkits.getSelectedItem();
        if (selectedToolkit == null) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewDraft.ToolkitValidation.None"));
        }

        var text = name.getText();
        if (!text.matches(AutomateConstants.DraftNameRegex)) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewDraft.NameValidation.NotMatch"));
        }
        var existing = this.drafts.stream()
                .anyMatch(draft -> draft.getName().equalsIgnoreCase(text));
        if (existing) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewDraft.NameValidation.Exists"));
        }

        return null;
    }

    @Override
    protected void doOKAction() {
        super.doOKAction();
        Name = this.name.getText();
        var selectedToolkit = (ToolkitDefinition) this.toolkits.getSelectedItem();
        if (selectedToolkit != null) {
            ToolkitName = selectedToolkit.getName();
        }
    }
}
