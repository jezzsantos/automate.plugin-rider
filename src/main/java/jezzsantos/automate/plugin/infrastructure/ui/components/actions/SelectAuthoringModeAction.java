package jezzsantos.automate.plugin.infrastructure.ui.components.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import icons.RiderIcons;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class SelectAuthoringModeAction extends ToggleAction {

    private final Consumer<Boolean> onSelect;
    private boolean selected;

    public SelectAuthoringModeAction(Consumer<Boolean> onSelect) {
        super();
        this.onSelect = onSelect;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        super.update(e);

        SetPresentation(e);
    }

    @SuppressWarnings("DialogTitleCapitalization")
    private void SetPresentation(@NotNull AnActionEvent e) {
        var message = AutomateBundle.message("action.SelectModePattern.Title");
        var presentation = e.getPresentation();
        presentation.setDescription(message);
        presentation.setText(message);
        presentation.setIcon(this.selected ? RiderIcons.AltEnter.MenuToggleOn : RiderIcons.AltEnter.MenuToggleOff);
        presentation.setEnabledAndVisible(true);
    }


    @Override
    public boolean isSelected(@NotNull AnActionEvent anActionEvent) {
        return this.selected;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent anActionEvent, boolean b) {
        this.selected = !this.selected;
        SetPresentation(anActionEvent);
        this.onSelect.accept(this.selected);
    }
}
