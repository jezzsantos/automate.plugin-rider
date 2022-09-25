package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.util.function.Function;

public class EditValueDialog extends DialogWrapper {

    private final EditValueDialogContext context;
    private JPanel contents;
    private JTextField value;
    private JLabel valueTitle;

    public EditValueDialog(@NotNull Project project, @NotNull EditValueDialog.EditValueDialogContext context) {

        super(project);
        this.context = context;

        this.init();
        this.setTitle(context.getTitle() != null
                        ? context.getTitle()
                        : AutomateBundle.message("dialog.EditValue.Title"));
        this.valueTitle.setText(context.getName());
        this.value.setText(context.getValue());
    }

    @TestOnly
    public static @Nullable ValidationInfo doValidate(EditValueDialogContext context, String value) {

        if (context.isRequired()) {
            if (value.isEmpty()) {
                return new ValidationInfo(AutomateBundle.message("dialog.EditValue.Validation.ValueMissing.Message"));
            }
        }
        var validator = context.getValidator();
        if (validator != null) {
            var result = validator.apply(value);
            //noinspection RedundantIfStatement
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    @NotNull
    public EditValueDialogContext getContext() {return this.context;}

    @Override
    protected @Nullable JComponent createCenterPanel() {

        return this.contents;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {

        return this.value;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {

        var value = this.value.getText();
        return doValidate(this.context, value);
    }

    @Override
    protected void doOKAction() {

        super.doOKAction();
        this.context.Value = this.value.getText();
    }

    public static class EditValueDialogContext {

        private final String name;
        private final String value;
        private final boolean isRequired;
        private final Function<String, ValidationInfo> validator;
        private final String title;
        public String Value;

        @SuppressWarnings("unused")
        public EditValueDialogContext(@NotNull String name, @Nullable String value, boolean isRequired, @Nullable String title, @Nullable Function<String, ValidationInfo> validator) {

            this.name = name;
            this.value = value == null
              ? ""
              : value;
            this.isRequired = isRequired;
            this.title = title;
            this.validator = validator;
        }

        public EditValueDialogContext(boolean isRequired, @Nullable String title, @Nullable Function<String, ValidationInfo> validator) {

            this.name = AutomateBundle.message("dialog.EditValue.DefaultValue.Title");
            this.value = "";
            this.isRequired = isRequired;
            this.title = title;
            this.validator = validator;
        }

        @Nullable
        public String getTitle() {return this.title;}

        @NotNull
        public String getName() {return this.name;}

        @NotNull
        public String getValue() {return this.value;}

        public boolean isRequired() {return this.isRequired;}

        @Nullable
        public Function<String, ValidationInfo> getValidator() {return this.validator;}
    }
}
