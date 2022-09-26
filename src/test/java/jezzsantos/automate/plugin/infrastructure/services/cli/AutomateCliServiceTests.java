package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntryType;
import jezzsantos.automate.plugin.application.services.interfaces.CliVersionCompatibility;
import jezzsantos.automate.plugin.application.services.interfaces.IApplicationConfiguration;
import jezzsantos.automate.plugin.common.Try;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;

public class AutomateCliServiceTests {

    @TempDir
    Path tempFile;
    private AutomateCliService service;
    private IAutomateCliRunner cliRunner;
    private IAutomationCache cache;
    private IApplicationConfiguration configuration;
    private IOsPlatform platform;

    @BeforeEach
    public void setUp() {

        this.configuration = Mockito.mock(IApplicationConfiguration.class);
        Mockito.when(this.configuration.getExecutablePath())
          .thenReturn("anexecutablepath");
        this.cache = Mockito.mock(IAutomationCache.class);
        Mockito.when(this.cache.isCliInstalled(any()))
          .thenReturn(true);
        this.platform = Mockito.mock(IOsPlatform.class);
        Mockito.when(this.platform.getIsWindowsOs())
          .thenReturn(true);
        Mockito.when(this.platform.getDotNetInstallationDirectory())
          .thenReturn("aninstallationdirectory");
        this.cliRunner = Mockito.mock(IAutomateCliRunner.class);
        this.service = new AutomateCliService(this.configuration, this.cache, this.platform, this.cliRunner);
    }

    @Test
    public void whenConstructed_ThenLogsCliInstallationStatus() {

        Mockito.reset(this.cliRunner, this.cache, this.configuration);
        var filename = createTemporaryFile(this.service.getExecutableName());
        Mockito.when(this.configuration.getExecutablePath())
          .thenReturn(filename);
        Mockito.when(this.cliRunner.execute(anyString(), anyString(), anyList()))
          .thenReturn(new CliTextResult("", "100.0.0"));

        new AutomateCliService(this.configuration, this.cache, this.platform, this.cliRunner);

        Mockito.verify(this.configuration).getExecutablePath();
        Mockito.verify(this.cliRunner).execute(argThat(x -> x.equals("aninstallationdirectory")), argThat(x -> x.equals(filename)),
                                               argThat(x -> x.size() == 1 && x.get(0).equals("--version")));
        Mockito.verify(this.cache).setIsCliInstalled(true);
        Mockito.verify(this.cliRunner).log(argThat(x -> x.Type == CliLogEntryType.Normal));
    }

    @Test
    public void whenGetExecutableName_ThenReturnsName() {

        var result = this.service.getExecutableName();

        assertEquals("automate.exe", result);
    }

    @Test
    public void whenGetDefaultExecutableLocation_ThenReturnsName() {

        var result = this.service.getDefaultExecutableLocation();

        var path = Paths.get("aninstallationdirectory").resolve("automate.exe").toString();
        assertEquals(path, result);
    }

    @Test
    public void whenTryGetExecutableStatusAndExecutableNotOnDisk_ThenReturnsStatus() {

        var result = this.service.tryGetExecutableStatus("acurrentdirectory", "notavalidfilepath");

        assertEquals(CliVersionCompatibility.Unknown, result.getCompatibility());
    }

    @Test
    public void whenTryGetExecutableStatusAndExecutableNameNotMatch_ThenReturnsStatus() {

        var filename = createTemporaryFile();

        var result = this.service.tryGetExecutableStatus("acurrentdirectory", filename);

        assertEquals(CliVersionCompatibility.Unknown, result.getCompatibility());
    }

    @Test
    public void whenTryGetExecutableStatusAndInvalidExecutable_ThenReturnsStatus() {

        var filename = createTemporaryFile(this.service.getExecutableName());
        Mockito.when(this.cliRunner.execute(anyString(), anyString(), anyList()))
          .thenReturn(new CliTextResult("anerror", ""));

        var result = this.service.tryGetExecutableStatus("acurrentdirectory", filename);

        assertEquals(CliVersionCompatibility.Unknown, result.getCompatibility());
    }

    @Test
    public void whenTryGetExecutableStatusAndUnsupportedVersion_ThenReturnsStatus() {

        var filename = createTemporaryFile(this.service.getExecutableName());
        Mockito.when(this.cliRunner.execute(anyString(), anyString(), anyList()))
          .thenReturn(new CliTextResult("", "0.0.1"));

        var result = this.service.tryGetExecutableStatus("acurrentdirectory", filename);

        assertEquals("0.0.1", result.getVersion());
        assertEquals(CliVersionCompatibility.UnSupported, result.getCompatibility());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void whenIsCliInstalledAndUnknownVersion_ThenReturnsFalse() {

        var filename = createTemporaryFile();
        Mockito.when(this.configuration.getExecutablePath())
          .thenReturn(filename);
        Mockito.when(this.cache.isCliInstalled(any()))
          .thenAnswer((Answer) invocation -> ((Supplier<Boolean>) invocation.getArguments()[0]).get());

        var result = this.service.isCliInstalled("acurrentdirectory");

        assertFalse(result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void whenIsCliInstalledAndUnsupportedVersion_ThenReturnsFalse() {

        var filename = createTemporaryFile(this.service.getExecutableName());
        Mockito.when(this.configuration.getExecutablePath())
          .thenReturn(filename);
        Mockito.when(this.cliRunner.execute(anyString(), anyString(), anyList()))
          .thenReturn(new CliTextResult("", "0.0.1"));
        Mockito.when(this.cache.isCliInstalled(any()))
          .thenAnswer((Answer) invocation -> ((Supplier<Boolean>) invocation.getArguments()[0]).get());

        var result = this.service.isCliInstalled("acurrentdirectory");

        assertFalse(result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void whenIsCliInstalledAndSupportedVersion_ThenReturnsTrue() {

        var filename = createTemporaryFile(this.service.getExecutableName());
        Mockito.when(this.configuration.getExecutablePath())
          .thenReturn(filename);
        Mockito.when(this.cliRunner.execute(anyString(), anyString(), anyList()))
          .thenReturn(new CliTextResult("", "100.0.0"));
        Mockito.when(this.cache.isCliInstalled(any()))
          .thenAnswer((Answer) invocation -> ((Supplier<Boolean>) invocation.getArguments()[0]).get());

        var result = this.service.isCliInstalled("acurrentdirectory");

        assertTrue(result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void whenListAllAutomationAndNotCachedAndFails_ThenReturnsEmptyLists() {

        Mockito.when(this.cliRunner.executeStructured(any(), anyString(), anyString(), anyList()))
          .thenReturn(new CliStructuredResult<>(new StructuredError(), null));
        Mockito.when(this.cache.ListAll(any(), anyBoolean()))
          .thenAnswer((Answer) invocation -> ((Supplier<AllStateLite>) invocation.getArguments()[0]).get());

        var result = this.service.listAllAutomation("acurrentdirectory", false);

        assertTrue(result.getPatterns().isEmpty());
        assertTrue(result.getToolkits().isEmpty());
        assertTrue(result.getDrafts().isEmpty());
        Mockito.verify(this.cliRunner).executeStructured(
          any(), argThat(x -> x.equals("acurrentdirectory")),
          argThat(x -> x.equals("anexecutablepath")),
          argThat(list -> list.equals(Arrays.asList("list", "all"))));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void whenListAllAutomationAndNotCached_ThenReturnsCliResult() {

        Mockito.when(this.cliRunner.executeStructured(any(), anyString(), anyString(), anyList()))
          .thenReturn(new CliStructuredResult(null, new ListAllDefinitionsStructuredOutput()));
        Mockito.when(this.cache.ListAll(any(), anyBoolean()))
          .thenAnswer((Answer) invocation -> ((Supplier<AllStateLite>) invocation.getArguments()[0]).get());

        var result = this.service.listAllAutomation("acurrentdirectory", false);

        assertTrue(result.getPatterns().isEmpty());
        assertTrue(result.getToolkits().isEmpty());
        assertTrue(result.getDrafts().isEmpty());
        Mockito.verify(this.cliRunner).executeStructured(
          any(), argThat(x -> x.equals("acurrentdirectory")),
          argThat(x -> x.equals("anexecutablepath")),
          argThat(list -> list.equals(Arrays.asList("list", "all"))));
    }

    @Test
    public void whenListAllAutomationAndCached_ThenReturnsCached() {

        Mockito.when(this.cliRunner.executeStructured(any(), anyString(), anyString(), anyList()))
          .thenReturn(new CliStructuredResult<>(new StructuredError(), null));
        Mockito.when(this.cache.ListAll(any(), anyBoolean()))
          .thenReturn(new AllStateLite());

        var result = this.service.listAllAutomation("acurrentdirectory", false);

        assertTrue(result.getPatterns().isEmpty());
        assertTrue(result.getToolkits().isEmpty());
        assertTrue(result.getDrafts().isEmpty());
        Mockito.verify(this.cliRunner, never()).executeStructured(any(), anyString(), anyString(), anyList());
    }

    private String createTemporaryFile(String name) {

        var file = this.tempFile.resolve(name)
          .toFile();
        Try.safely(file::createNewFile);
        return file.getAbsolutePath();
    }

    private String createTemporaryFile() {

        return createTemporaryFile(RandomStringUtils.randomAlphanumeric(8));
    }
}
