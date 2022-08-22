package jezzsantos.automate.plugin.instrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.services.interfaces.IConfiguration;
import jezzsantos.automate.plugin.infrastructure.services.cli.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;

public class AutomateCliServiceTests {

    private AutomateCliService service;
    private IAutomateCliRunner runner;
    private IAutomationCache cache;

    @BeforeEach
    public void setUp() {

        var configuration = Mockito.mock(IConfiguration.class);
        Mockito.when(configuration.getExecutablePath())
                .thenReturn("anexecutablepath");
        this.cache = Mockito.mock(IAutomationCache.class);
        var platform = Mockito.mock(IOsPlatform.class);
        Mockito.when(platform.getIsWindowsOs())
                .thenReturn(true);
        Mockito.when(platform.getDotNetInstallationDirectory())
                .thenReturn("aninstallationdirectory");
        this.runner = Mockito.mock(IAutomateCliRunner.class);
        this.service = new AutomateCliService(configuration, cache, platform, runner);
    }

    @Test
    public void whenGetExecutableName_ThenReturnsName() {
        var result = this.service.getExecutableName();

        assertEquals("automate.exe", result);
    }

    @Test
    public void whenGetDefaultInstallLocation_ThenReturnsName() {
        var result = this.service.getDefaultInstallLocation();

        var path = Paths.get("aninstallationdirectory").resolve("automate.exe").toString();
        assertEquals(path, result);
    }

    @Test
    public void whenTryGetExecutableVersionAndInvalidPath_ThenReturnsNull() {
        Mockito.when(this.runner.execute(anyString(), anyList()))
                .thenReturn(new CliTextResult("anerror", ""));

        var result = this.service.tryGetExecutableVersion("anexecutablepath");

        assertNull(result);
    }

    @Test
    public void whenTryGetExecutableVersion_ThenReturnsOutput() {
        Mockito.when(this.runner.execute(anyString(), anyList()))
                .thenReturn(new CliTextResult("", "anoutput"));

        var result = this.service.tryGetExecutableVersion("anexecutablepath");

        assertEquals("anoutput", result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void whenListAllAutomationAndNotCachedAndFails_ThenReturnsEmptyLists() {

        Mockito.when(this.runner.executeStructured(any(), anyString(), anyList()))
                .thenReturn(new CliStructuredResult<>(new StructuredError(), null));
        Mockito.when(this.cache.ListAll(ArgumentMatchers.any(), anyBoolean()))
                .thenAnswer((Answer) invocation -> ((Supplier<AllStateLite>) invocation.getArguments()[0]).get());

        var result = this.service.listAllAutomation(false);

        assertTrue(result.getPatterns().isEmpty());
        assertTrue(result.getToolkits().isEmpty());
        assertTrue(result.getDrafts().isEmpty());
        Mockito.verify(this.runner).executeStructured(
                any(),
                argThat(x -> x.equals("anexecutablepath")),
                argThat(list -> list.equals(Arrays.asList("list", "all"))));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void whenListAllAutomationAndNotCached_ThenReturnsCliResult() {

        Mockito.when(this.runner.executeStructured(any(), anyString(), anyList()))
                .thenReturn(new CliStructuredResult(null, new ListAllDefinitionsStructuredOutput()));
        Mockito.when(this.cache.ListAll(ArgumentMatchers.any(), anyBoolean()))
                .thenAnswer((Answer) invocation -> ((Supplier<AllStateLite>) invocation.getArguments()[0]).get());

        var result = this.service.listAllAutomation(false);

        assertTrue(result.getPatterns().isEmpty());
        assertTrue(result.getToolkits().isEmpty());
        assertTrue(result.getDrafts().isEmpty());
        Mockito.verify(this.runner).executeStructured(
                any(),
                argThat(x -> x.equals("anexecutablepath")),
                argThat(list -> list.equals(Arrays.asList("list", "all"))));
    }

    @Test
    public void whenListAllAutomationAndCached_ThenReturnsCached() {

        Mockito.when(this.runner.executeStructured(any(), anyString(), anyList()))
                .thenReturn(new CliStructuredResult<>(new StructuredError(), null));
        Mockito.when(this.cache.ListAll(ArgumentMatchers.any(), anyBoolean()))
                .thenReturn(new AllStateLite());

        var result = this.service.listAllAutomation(false);

        assertTrue(result.getPatterns().isEmpty());
        assertTrue(result.getToolkits().isEmpty());
        assertTrue(result.getDrafts().isEmpty());
        Mockito.verify(this.runner, never()).executeStructured(any(), anyString(), anyList());
    }
}
