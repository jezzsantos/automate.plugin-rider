package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntryType;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftDetailed;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftLite;
import jezzsantos.automate.plugin.application.services.interfaces.CliExecutableStatus;
import jezzsantos.automate.plugin.application.services.interfaces.CliVersionCompatibility;
import jezzsantos.automate.plugin.application.services.interfaces.IProjectConfiguration;
import jezzsantos.automate.plugin.common.StringWithDefault;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.common.recording.ReportingContext;
import jezzsantos.automate.plugin.infrastructure.IOsPlatform;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.CliStructuredResult;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.GetInfoStructuredOutput;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.ListAllDefinitionsStructuredOutput;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredError;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;

public class AutomateCliServiceTests {

    @TempDir
    Path tempFile;
    private AutomateCliService service;
    private IAutomateCliRunner cliRunner;
    private ICliResponseCache cache;
    private IRecorder recorder;
    private IProjectConfiguration configuration;
    private IOsPlatform platform;
    private ICliUpgrader upgrader;

    @BeforeEach
    public void setUp() {

        this.recorder = Mockito.mock(IRecorder.class);
        Mockito.when(this.recorder.getReportingContext())
          .thenReturn(new ReportingContext(true, "amachineid", "asessionid"));
        Mockito.when(this.recorder.withOperation(anyString(), any(), anyString(), anyString()))
          .thenAnswer(invocation -> ((Supplier<?>) invocation.getArgument(1)).get());
        this.configuration = Mockito.mock(IProjectConfiguration.class);
        Mockito.when(this.configuration.getExecutablePath())
          .thenReturn(StringWithDefault.fromValue("anexecutablepath"));
        this.cache = Mockito.mock(ICliResponseCache.class);
        Mockito.when(this.cache.isCliInstalled(any()))
          .thenReturn(true);
        this.platform = Mockito.mock(IOsPlatform.class);
        Mockito.when(this.platform.getIsWindowsOs())
          .thenReturn(true);
        Mockito.when(this.platform.getDotNetToolsDirectory())
          .thenReturn("atoolsdirectory");
        this.cliRunner = Mockito.mock(IAutomateCliRunner.class);
        this.upgrader = Mockito.mock(ICliUpgrader.class);
        Mockito.when(this.upgrader.upgrade(any(), anyString(), any(), any()))
          .thenAnswer(invocation -> invocation.getArgument(2));
        this.service = new AutomateCliService(this.recorder, this.configuration, this.cache, this.platform, this.cliRunner, this.upgrader);
    }

    @Test
    public void whenConstructedAndVersionNotInstalled_ThenUpgradesLogsAndCachesResult() {

        Mockito.reset(this.cliRunner, this.cache, this.configuration, this.upgrader);
        var executableName = this.service.getExecutableName();
        var filename = createTemporaryFile(executableName);
        Mockito.when(this.configuration.getExecutablePath())
          .thenReturn(StringWithDefault.fromValue(filename));
        Mockito.when(this.cliRunner.executeStructured(any(), any(), anyList()))
          .thenReturn(new CliStructuredResult<>(new StructuredError("anerror"), null));
        Mockito.when(this.upgrader.upgrade(any(), anyString(), any(), any()))
          .thenReturn(new CliExecutableStatus(executableName, "100.0.0"));

        new AutomateCliService(this.recorder, this.configuration, this.cache, this.platform, this.cliRunner, this.upgrader);

        Mockito.verify(this.configuration).getExecutablePath();
        Mockito.verify(this.cliRunner)
          .executeStructured(any(), argThat(x -> x.getCurrentDirectory().equals("atoolsdirectory") && x.getExecutablePath().equals(StringWithDefault.fromValue(filename))),
                             argThat(x -> x.size() == 1
                               && x.get(0).equals("@info")
                             ));
        Mockito.verify(this.cache).setIsCliInstalled(true);
        Mockito.verify(this.cliRunner).log(argThat(x -> x.Type == CliLogEntryType.NORMAL));
        Mockito.verify(this.upgrader)
          .upgrade(argThat(x -> x.equals(StringWithDefault.fromValue(filename))), argThat(x -> x.equals(executableName)),
                   any(), any());
    }

    @Test
    public void whenConstructedAndIncompatibleVersionInstalled_ThenUpgradesLogsAndCachesResult() {

        Mockito.reset(this.cliRunner, this.cache, this.configuration, this.upgrader);
        var executableName = this.service.getExecutableName();
        var filename = createTemporaryFile(executableName);
        Mockito.when(this.configuration.getExecutablePath())
          .thenReturn(StringWithDefault.fromValue(filename));
        Mockito.when(this.cliRunner.executeStructured(any(), any(), anyList()))
          .thenReturn(new CliStructuredResult<>(null, new GetInfoStructuredOutput("0.0.0")));
        Mockito.when(this.upgrader.upgrade(any(), anyString(), any(), any()))
          .thenReturn(new CliExecutableStatus(executableName, "100.0.0"));

        new AutomateCliService(this.recorder, this.configuration, this.cache, this.platform, this.cliRunner, this.upgrader);

        Mockito.verify(this.configuration).getExecutablePath();
        Mockito.verify(this.cliRunner)
          .executeStructured(any(), argThat(x -> x.getCurrentDirectory().equals("atoolsdirectory") && x.getExecutablePath().equals(StringWithDefault.fromValue(filename))),
                             argThat(x -> x.size() == 1
                               && x.get(0).equals("@info")
                             ));
        Mockito.verify(this.cache).setIsCliInstalled(true);
        Mockito.verify(this.cliRunner).log(argThat(x -> x.Type == CliLogEntryType.NORMAL));
        Mockito.verify(this.upgrader)
          .upgrade(argThat(x -> x.equals(StringWithDefault.fromValue(filename))), argThat(x -> x.equals(executableName)),
                   any(), any());
    }

    @Test
    public void whenConstructedAndCompatibleVersionInstalled_ThenLogsAndCachesResult() {

        Mockito.reset(this.cliRunner, this.cache, this.configuration, this.upgrader);
        var filename = createTemporaryFile(this.service.getExecutableName());
        Mockito.when(this.configuration.getExecutablePath())
          .thenReturn(StringWithDefault.fromValue(filename));
        Mockito.when(this.cliRunner.executeStructured(any(), any(), anyList()))
          .thenReturn(new CliStructuredResult<>(null, new GetInfoStructuredOutput("100.0.0")));
        Mockito.when(this.upgrader.upgrade(any(), anyString(), any(), any()))
          .thenAnswer(invocation -> invocation.getArgument(2));

        new AutomateCliService(this.recorder, this.configuration, this.cache, this.platform, this.cliRunner, this.upgrader);

        Mockito.verify(this.configuration).getExecutablePath();
        Mockito.verify(this.cliRunner)
          .executeStructured(any(), argThat(x -> x.getCurrentDirectory().equals("atoolsdirectory") && x.getExecutablePath().equals(StringWithDefault.fromValue(filename))),
                             argThat(x -> x.size() == 1
                               && x.get(0).equals("@info")
                             ));
        Mockito.verify(this.cache).setIsCliInstalled(true);
        Mockito.verify(this.cliRunner).log(argThat(x -> x.Type == CliLogEntryType.NORMAL));
        Mockito.verify(this.upgrader, never()).upgrade(any(), anyString(), any(), any());
    }

    @Test
    public void whenGetExecutableName_ThenReturnsName() {

        var result = this.service.getExecutableName();

        assertEquals("automate.exe", result);
    }

    @Test
    public void whenGetDefaultExecutableLocation_ThenReturnsName() {

        var result = this.service.getDefaultExecutableLocation();

        var path = Paths.get("atoolsdirectory").resolve("automate.exe").toString();
        assertEquals(path, result);
    }

    @Test
    public void whenTryGetExecutableStatusAndExecutableNotOnDisk_ThenReturnsStatus() {

        var result = this.service.tryGetExecutableStatus("acurrentdirectory", StringWithDefault.fromValue("notavalidfilepath"));

        assertEquals(CliVersionCompatibility.UNKNOWN, result.getCompatibility());
    }

    @Test
    public void whenTryGetExecutableStatusAndExecutableNameNotMatch_ThenReturnsStatus() {

        var filename = createTemporaryFile();

        var result = this.service.tryGetExecutableStatus("acurrentdirectory", StringWithDefault.fromValue(filename));

        assertEquals(CliVersionCompatibility.UNKNOWN, result.getCompatibility());
    }

    @Test
    public void whenTryGetExecutableStatusAndInvalidExecutable_ThenReturnsStatus() {

        var filename = createTemporaryFile(this.service.getExecutableName());
        Mockito.when(this.cliRunner.executeStructured(any(), any(), anyList()))
          .thenReturn(new CliStructuredResult<>(new StructuredError("anerror"), null));

        var result = this.service.tryGetExecutableStatus("acurrentdirectory", StringWithDefault.fromValue(filename));

        assertEquals(CliVersionCompatibility.UNKNOWN, result.getCompatibility());
    }

    @Test
    public void whenTryGetExecutableStatusAndUnsupportedVersion_ThenReturnsStatus() {

        var filename = createTemporaryFile(this.service.getExecutableName());
        Mockito.when(this.cliRunner.executeStructured(any(), any(), anyList()))
          .thenReturn(new CliStructuredResult<>(null, new GetInfoStructuredOutput("0.0.1")));

        var result = this.service.tryGetExecutableStatus("acurrentdirectory", StringWithDefault.fromValue(filename));

        assertEquals("0.0.1", result.getVersion());
        assertEquals(CliVersionCompatibility.INCOMPATIBLE, result.getCompatibility());
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void whenIsCliInstalledAndUnknownVersion_ThenReturnsFalse() {

        var filename = createTemporaryFile();
        Mockito.when(this.configuration.getExecutablePath())
          .thenReturn(StringWithDefault.fromValue(filename));
        Mockito.when(this.cache.isCliInstalled(any()))
          .thenAnswer(invocation -> ((Supplier<Boolean>) invocation.getArguments()[0]).get());

        var result = this.service.isCliInstalled("acurrentdirectory");

        assertFalse(result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void whenIsCliInstalledAndUnsupportedVersion_ThenReturnsFalse() {

        var filename = createTemporaryFile(this.service.getExecutableName());
        Mockito.when(this.configuration.getExecutablePath())
          .thenReturn(StringWithDefault.fromValue(filename));
        Mockito.when(this.cliRunner.executeStructured(any(), any(), anyList()))
          .thenReturn(new CliStructuredResult(null, new GetInfoStructuredOutput("0.0.1")));
        Mockito.when(this.cache.isCliInstalled(any()))
          .thenAnswer(invocation -> ((Supplier<Boolean>) invocation.getArguments()[0]).get());

        var result = this.service.isCliInstalled("acurrentdirectory");

        assertFalse(result);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void whenIsCliInstalledAndSupportedVersion_ThenReturnsTrue() {

        var filename = createTemporaryFile(this.service.getExecutableName());
        Mockito.when(this.configuration.getExecutablePath())
          .thenReturn(StringWithDefault.fromValue(filename));
        Mockito.when(this.cliRunner.executeStructured(any(), any(), anyList()))
          .thenReturn(new CliStructuredResult(null, new GetInfoStructuredOutput("100.0.0")));
        Mockito.when(this.cache.isCliInstalled(any()))
          .thenAnswer(invocation -> ((Supplier<Boolean>) invocation.getArguments()[0]).get());

        var result = this.service.isCliInstalled("acurrentdirectory");

        assertTrue(result);
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void whenListAllAutomationAndNotCachedAndFails_ThenReturnsEmptyLists() {

        Mockito.when(this.cliRunner.executeStructured(any(), any(), anyList()))
          .thenReturn(new CliStructuredResult<>(new StructuredError(), null));
        Mockito.when(this.cache.listAll(any(), anyBoolean()))
          .thenAnswer(invocation -> ((Supplier<AllStateLite>) invocation.getArguments()[0]).get());

        var result = this.service.listAllAutomation("acurrentdirectory", false);

        assertTrue(result.getPatterns().isEmpty());
        assertTrue(result.getToolkits().isEmpty());
        assertTrue(result.getDrafts().isEmpty());
        Mockito.verify(this.cliRunner)
          .executeStructured(any(),
                             argThat(x -> x.getCurrentDirectory().equals("acurrentdirectory") && x.getExecutablePath().equals(StringWithDefault.fromValue("anexecutablepath"))),
                             argThat(list -> list.equals(Arrays.asList("@list", "@all"))));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    public void whenListAllAutomationAndNotCached_ThenReturnsCliResult() {

        Mockito.when(this.cliRunner.executeStructured(any(), any(), anyList()))
          .thenReturn(new CliStructuredResult(null, new ListAllDefinitionsStructuredOutput()));
        Mockito.when(this.cache.listAll(any(), anyBoolean()))
          .thenAnswer(invocation -> ((Supplier<AllStateLite>) invocation.getArguments()[0]).get());

        var result = this.service.listAllAutomation("acurrentdirectory", false);

        assertTrue(result.getPatterns().isEmpty());
        assertTrue(result.getToolkits().isEmpty());
        assertTrue(result.getDrafts().isEmpty());
        Mockito.verify(this.cliRunner)
          .executeStructured(any(),
                             argThat(x -> x.getCurrentDirectory().equals("acurrentdirectory") && x.getExecutablePath().equals(StringWithDefault.fromValue("anexecutablepath"))),
                             argThat(list -> list.equals(Arrays.asList("@list", "@all"))));
    }

    @Test
    public void whenListAllAutomationAndCached_ThenReturnsCached() {

        Mockito.when(this.cliRunner.executeStructured(any(), any(), anyList()))
          .thenReturn(new CliStructuredResult<>(new StructuredError(), null));
        Mockito.when(this.cache.listAll(any(), anyBoolean()))
          .thenReturn(new AllStateLite());

        var result = this.service.listAllAutomation("acurrentdirectory", false);

        assertTrue(result.getPatterns().isEmpty());
        assertTrue(result.getToolkits().isEmpty());
        assertTrue(result.getDrafts().isEmpty());
        Mockito.verify(this.cliRunner, never()).executeStructured(any(), any(), anyList());
    }

    @SuppressWarnings({"unchecked"})
    @Test
    public void whenGetCurrentDraftDetailedAndIsOutOfDate_ThenReturnsOutOfDateDraft() throws Exception {

        Mockito.when(this.cache.getDraftDetailed(any()))
          .thenAnswer(invocation -> ((Callable<DraftDetailed>) invocation.getArguments()[0]).call());
        Mockito.when(this.cache.getDraftInfo(any()))
          .thenReturn(
            new DraftLite("anid", "aname", "atoolkitid", "atoolkitname", "1.0.0", "2.0.0", AutomateConstants.DraftToolkitVersionCompatibility.DRAFT_AHEADOF_TOOLKIT, true));

        var result = this.service.getCurrentDraftDetailed("acurrentdirectory");

        assertTrue(result.isIncompatible());
    }

    @Test
    public void whenGetCurrentDraftDetailedAndIsNotOutOfDate_ThenReturns() throws Exception {

        Mockito.when(this.cache.getDraftInfo(any()))
          .thenReturn(new DraftLite("anid", "aname", "atoolkitid", "atoolkitname", "anoriginaltoolkitversion", "anoriginaltoolkitversion", true));
        var draft = new DraftDetailed("anid", "aname", "atoolkitid", "atoolkitname", "atoolkitversion", "aruntimeversion", new HashMap<>());
        Mockito.when(this.cache.getDraftDetailed(any()))
          .thenReturn(draft);

        var result = this.service.getCurrentDraftDetailed("acurrentdirectory");

        assertEquals(draft, result);
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
