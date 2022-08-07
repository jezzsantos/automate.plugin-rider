package jezzsantos.automate.ui.components;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import jezzsantos.automate.AutomateBundle;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.function.Supplier;

public class OptionsToolbarAction extends AnAction {
    private final Supplier<Component> toolbarComponent;

    public OptionsToolbarAction(Supplier<Component> toolbarComponent) {
        super();
        this.toolbarComponent = toolbarComponent;

        String message = AutomateBundle.message("action.Automate.ShowOptionsMenu");
        this.getTemplatePresentation().setDescription(message);
        this.getTemplatePresentation().setText(message);
        this.getTemplatePresentation().setIcon(AllIcons.General.Gear);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new OpenSettingsToolbarAction());

        ActionManager.getInstance().createActionPopupMenu(ActionPlaces.UNKNOWN, actionGroup)
                .getComponent()
                .show(toolbarComponent.get(), 15, 15);
    }
}
