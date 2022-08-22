package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import org.jetbrains.annotations.NotNull;

public class ConfirmDeleteDialogContext {

    public final String Title;
    public final String Message;

    public ConfirmDeleteDialogContext(@NotNull String title, @NotNull String message) {

        this.Title = title;
        this.Message = message;
    }
}
