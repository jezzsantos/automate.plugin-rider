package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.jetbrains.rider.test.base.BaseTestWithSolution;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ShowSettingsMenuActionTests extends BaseTestWithSolution {

    @Test
    public void whenConstructed_ThenIsEnabledAndVisible() {

        var action = new ShowSettingsMenuAction();
        var dataContext = Mockito.mock(DataContext.class);
        var event = AnActionEvent.createFromDataContext("aplace", null, dataContext);

        action.update(event);
        assertTrue(event.getPresentation().isEnabledAndVisible());
    }

    @NotNull
    @Override
    protected String getSolutionDirectoryName() {

        return "TestSolution";
    }
}
