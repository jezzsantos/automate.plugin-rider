package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.util.Objects;

public class NewDraftDialog extends DialogWrapper {

    private final NewDraftDialogContext context;
    private JPanel contents;
    private JTextField name;
    private JLabel nameTitle;
    private JLabel toolkitTitle;
    private JComboBox<ToolkitLite> toolkits;

    public NewDraftDialog(@Nullable Project project, @NotNull NewDraftDialogContext context) {

        super(project);
        this.context = context;

        this.init();
        this.setTitle(AutomateBundle.message("dialog.NewDraft.Title"));
        this.toolkitTitle.setText(AutomateBundle.message("dialog.NewDraft.Toolkit.Title"));
        this.nameTitle.setText(AutomateBundle.message("dialog.NewDraft.Name.Title"));
        this.name.setText(this.context.Name);
        this.toolkits.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            var label = new JLabel();
            label.setText(Objects.requireNonNullElseGet(value.getName(), () -> AutomateBundle.message("dialog.NewDraft.NoToolkits.Message")));
            return label;
        });
        for (var toolkit : this.context.InstalledToolkits) {
            this.toolkits.addItem(toolkit);
        }
    }

    @TestOnly
    public static @Nullable ValidationInfo doValidate(NewDraftDialogContext context, ToolkitLite selectedToolkit, String name) {

        if (selectedToolkit == null) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewDraft.ToolkitValidation.None.Message"));
        }
        if (!context.InstalledToolkits.contains(selectedToolkit)) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewDraft.ToolkitValidation.None.Message"));
        }
        if (!name.matches(AutomateConstants.DraftNameRegex)) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewDraft.NameValidation.NotMatch.Message"));
        }
        var draftExists = context.Drafts.stream()
          .anyMatch(draft -> draft.getName().equalsIgnoreCase(name));
        if (draftExists) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewDraft.NameValidation.Exists.Message"));
        }

        return null;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {

        return this.toolkits;
    }

    public NewDraftDialogContext getContext() {

        return this.context;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {

        return this.contents;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {

        var selectedToolkit = (ToolkitLite) this.toolkits.getSelectedItem();
        var name = this.name.getText();
        return doValidate(this.context, selectedToolkit, name);
    }

    @Override
    protected void doOKAction() {

        super.doOKAction();
        this.context.Name = this.name.getText();
        var selectedToolkit = (ToolkitLite) this.toolkits.getSelectedItem();
        if (selectedToolkit != null) {
            this.context.ToolkitName = selectedToolkit.getName();
        }
    }

}
