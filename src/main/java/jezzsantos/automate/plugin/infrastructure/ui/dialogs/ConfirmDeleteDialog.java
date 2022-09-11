package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ConfirmDeleteDialog extends DialogWrapper {

    private JPanel contents;
    private JLabel message;

    public ConfirmDeleteDialog(@NotNull Project project, @NotNull ConfirmDeleteDialogContext context) {

        super(project);

        this.init();
        this.setTitle(context.Title);
        this.message.setText(context.Message);
    }

    public static boolean confirms(Project project, String title, String message) {

        var dialog = new ConfirmDeleteDialog(project, new ConfirmDeleteDialogContext(title, message));
        return dialog.showAndGet();
    }

    public static class ConfirmDeleteDialogContext {

        public final String Title;
        public final String Message;

        public ConfirmDeleteDialogContext(@NotNull String title, @NotNull String message) {

            this.Title = title;
            this.Message = message;
        }
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {

        return this.contents;
    }
}
