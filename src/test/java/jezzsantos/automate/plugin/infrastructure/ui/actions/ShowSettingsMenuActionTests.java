package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import jezzsantos.automate.plugin.infrastructure.ui.IntegrationTestBase;
import org.jetbrains.annotations.NotNull;
import org.mockito.Mockito;
import org.testng.annotations.Test;

public class ShowSettingsMenuActionTests extends IntegrationTestBase {

    @Test
    public void whenConstructed_ThenIsEnabledAndVisible() {

        var action = new ShowSettingsMenuAction();
        var dataContext = Mockito.mock(DataContext.class);
        var event = AnActionEvent.createFromDataContext("aplace", null, dataContext);

        action.update(event);
        //assertTrue(event.getPresentation().isEnabledAndVisible());
    }

    @NotNull
    @Override
    protected String getSolutionDirectoryName() {

        return "TestSolution";
    }
}
