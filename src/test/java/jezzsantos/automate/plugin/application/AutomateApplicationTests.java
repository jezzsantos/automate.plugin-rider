//package jezzsantos.automate.plugin.application;
//
//import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
//import jezzsantos.automate.plugin.application.services.interfaces.IAutomateService;
//import jezzsantos.automate.plugin.application.services.interfaces.IConfiguration;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.anyBoolean;
//import static org.mockito.Mockito.never;
//
//public class AutomateApplicationTests {
//
//    private AutomateApplication application;
//    private IAutomateService automateService;
//
//    @BeforeEach
//    public void setUp() {
//
//        var configuration = Mockito.mock(IConfiguration.class);
//        this.automateService = Mockito.mock(IAutomateService.class);
//        this.application = new AutomateApplication(configuration, this.automateService);
//    }
//
//    @Test
//    public void whenListAllAutomationAndNotForce_ThenListsAllAutomation() {
//
//        Mockito.when(this.automateService.listAllAutomation(anyBoolean()))
//          .thenReturn(new AllStateLite());
//
//        this.application.listAllAutomation(false);
//
//        Mockito.verify(this.automateService, never()).refreshCliExecutableStatus();
//        Mockito.verify(this.automateService, never()).isCliInstalled();
//        Mockito.verify(this.automateService).listAllAutomation(false);
//    }
//
//    @Test
//    public void whenListAllAutomationAndForceAndCliStillNotInstalled_ThenForcesCliInstallationCheckAndLists() {
//
//        Mockito.when(this.automateService.isCliInstalled())
//          .thenReturn(true);
//        var state = new AllStateLite();
//        Mockito.when(this.automateService.listAllAutomation(anyBoolean()))
//          .thenReturn(state);
//
//        var result = this.application.listAllAutomation(true);
//
//        Mockito.verify(this.automateService).refreshCliExecutableStatus();
//        Mockito.verify(this.automateService).isCliInstalled();
//        Mockito.verify(this.automateService).listAllAutomation(true);
//        assertEquals(state, result);
//    }
//
//    @Test
//    public void whenListAllAutomationAndForceAndCliIsInstalled_ThenForcesCliInstallationCheckAndReturnsEmpty() {
//
//        Mockito.when(this.automateService.isCliInstalled())
//          .thenReturn(false);
//
//        var result = this.application.listAllAutomation(true);
//
//        Mockito.verify(this.automateService).refreshCliExecutableStatus();
//        Mockito.verify(this.automateService).isCliInstalled();
//        Mockito.verify(this.automateService, never()).listAllAutomation(anyBoolean());
//        assertTrue(result.getDrafts().isEmpty());
//        assertTrue(result.getPatterns().isEmpty());
//        assertTrue(result.getToolkits().isEmpty());
//    }
//}
