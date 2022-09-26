package jezzsantos.automate.plugin.application;

import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.services.interfaces.IApplicationConfiguration;
import jezzsantos.automate.plugin.application.services.interfaces.IAutomateCliService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;

public class AutomateApplicationTests {

    private AutomateApplication application;
    private IAutomateCliService automateService;

    @BeforeEach
    public void setUp() {

        var configuration = Mockito.mock(IApplicationConfiguration.class);
        this.automateService = Mockito.mock(IAutomateCliService.class);
        this.application = new AutomateApplication(configuration, this.automateService, "acurrentdirectory");
    }

    @Test
    public void whenListAllAutomationAndNotForce_ThenListsAllAutomation() {

        Mockito.when(this.automateService.listAllAutomation(anyString(), anyBoolean()))
          .thenReturn(new AllStateLite());

        this.application.listAllAutomation(false);

        Mockito.verify(this.automateService, never()).refreshCliExecutableStatus();
        Mockito.verify(this.automateService, never()).isCliInstalled("acurrentdirectory");
        Mockito.verify(this.automateService).listAllAutomation("acurrentdirectory", false);
    }

    @Test
    public void whenListAllAutomationAndForceAndCliStillNotInstalled_ThenForcesCliInstallationCheckAndLists() {

        Mockito.when(this.automateService.isCliInstalled(anyString()))
          .thenReturn(true);
        var state = new AllStateLite();
        Mockito.when(this.automateService.listAllAutomation(anyString(), anyBoolean()))
          .thenReturn(state);

        var result = this.application.listAllAutomation(true);

        Mockito.verify(this.automateService).refreshCliExecutableStatus();
        Mockito.verify(this.automateService).isCliInstalled("acurrentdirectory");
        Mockito.verify(this.automateService).listAllAutomation("acurrentdirectory", true);
        assertEquals(state, result);
    }

    @Test
    public void whenListAllAutomationAndForceAndCliIsInstalled_ThenForcesCliInstallationCheckAndReturnsEmpty() {

        Mockito.when(this.automateService.isCliInstalled(anyString()))
          .thenReturn(false);

        var result = this.application.listAllAutomation(true);

        Mockito.verify(this.automateService).refreshCliExecutableStatus();
        Mockito.verify(this.automateService).isCliInstalled("acurrentdirectory");
        Mockito.verify(this.automateService, never()).listAllAutomation(anyString(), anyBoolean());
        assertTrue(result.getDrafts().isEmpty());
        assertTrue(result.getPatterns().isEmpty());
        assertTrue(result.getToolkits().isEmpty());
    }
}
