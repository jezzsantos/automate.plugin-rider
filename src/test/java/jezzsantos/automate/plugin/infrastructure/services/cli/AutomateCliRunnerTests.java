package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.google.gson.Gson;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntryType;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.StringWithDefault;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.IOsPlatform;
import jezzsantos.automate.plugin.infrastructure.reporting.ICorrelationIdBuilder;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredError;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutput;
import jezzsantos.automate.plugin.infrastructure.services.cli.responses.StructuredOutputOutput;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.beans.PropertyChangeEvent;
import java.lang.module.ModuleDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static jezzsantos.automate.core.AutomateConstants.OutputStructuredOptionShorthand;
import static jezzsantos.automate.core.AutomateConstants.UsageCorrelationOption;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class AutomateCliRunnerTests {

    private AutomateCliRunner runner;
    private List<CliLogEntry> logs;
    private IProcessRunner processRunner;
    private IRecorder recorder;

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setUp() {

        this.processRunner = Mockito.mock(IProcessRunner.class);
        this.logs = new ArrayList<>();
        this.recorder = Mockito.mock(IRecorder.class);
        var correlationIdBuilder = Mockito.mock(ICorrelationIdBuilder.class);
        Mockito.when(correlationIdBuilder.build(anyString()))
          .thenReturn("acorrelationid");
        Mockito.when(this.recorder.measureCliCall(any(), any(), any()))
          .thenAnswer(invocation -> ((Function<ICorrelationIdBuilder, ?>) invocation.getArguments()[0]).apply(correlationIdBuilder));
        var platform = Mockito.mock(IOsPlatform.class);

        this.runner = new AutomateCliRunner(this.recorder, this.processRunner, platform);
        this.runner.addLogListener(this::propertyChange);
    }

    @AfterEach
    public void tearDown() {

        this.runner.removeLogListener(this::propertyChange);
    }

    @Test
    public void whenLog_ThenAddsToLogs() {

        this.runner.log(new CliLogEntry("atext", CliLogEntryType.NORMAL));

        assertEquals(1, this.logs.size());
        assertEquals("atext", this.logs.get(0).Text);
        assertEquals(CliLogEntryType.NORMAL, this.logs.get(0).Type);
    }

    @Test
    public void whenGetCliLogsAndNoLogs_ThenReturnsEmpty() {

        var result = this.runner.getLogs();

        assertTrue(result.isEmpty());
    }

    @Test
    public void whenGetCliLogs_ThenReturnsLogs() {

        this.runner.log(new CliLogEntry("atext", CliLogEntryType.NORMAL));

        var result = this.runner.getLogs();

        assertEquals(1, result.size());
        assertEquals("atext", result.get(0).Text);
        assertEquals(CliLogEntryType.NORMAL, result.get(0).Type);
    }

    @Test
    public void whenExecuteAndAllowUsage_ThenReturnsTextualResultAndLogs() {

        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createSuccess("anoutput"));

        var result = this.runner.execute(createContext(), List.of());

        assertFalse(result.isError());
        assertEquals("anoutput", result.getOutput());
        assertEquals(2, this.logs.size());
        assertEquals(CliLogEntryType.SUCCESS, this.logs.get(1).Type);
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.Success.Message"), this.logs.get(1).Text);
        Mockito.verify(this.processRunner).start(argThat(args -> args.size() == 3
          && args.get(0).equals("anexecutablepath")
          && args.get(1).equals(UsageCorrelationOption)
          && !args.get(2).isEmpty()
        ), argThat(x -> x.equals("acurrentdirectory")));
    }

    @Test
    public void whenExecuteAndSucceeds_ThenReturnsTextualResultAndLogs() {

        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createSuccess("anoutput"));

        var result = this.runner.execute(createContextForbidsUsage(), List.of("@anarg1", "@anarg2"));

        assertFalse(result.isError());
        assertEquals("anoutput", result.getOutput());
        assertEquals(2, this.logs.size());
        assertEquals(CliLogEntryType.SUCCESS, this.logs.get(1).Type);
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.Success.Message"), this.logs.get(1).Text);
        Mockito.verify(this.processRunner).start(argThat(args -> args.size() == 4
          && args.get(0).equals("anexecutablepath")
          && args.get(1).equals("anarg1")
          && args.get(2).equals("anarg2")
          && args.get(3).equals(String.format("%s:false", AutomateConstants.UsageAllowedOption))
        ), argThat(x -> x.equals("acurrentdirectory")));
        Mockito.verify(this.recorder).measureCliCall(any(), argThat(x -> x.equals("instruct-cli")), argThat(x -> x.equals("anarg1.anarg2")));
    }

    @Test
    public void whenExecuteAndFailsToStart_ThenReturnsTextualResultAndLogs() {

        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createFailedToStart());

        var result = this.runner.execute(createContextForbidsUsage(), List.of());

        assertTrue(result.isError());
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.FailedToStart.Message", "anexecutablepath"), result.getError());
        assertEquals(2, this.logs.size());
        assertEquals(CliLogEntryType.ERROR, this.logs.get(1).Type);
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.FailedToStart.Message", "anexecutablepath"), this.logs.get(1).Text);
    }

    @Test
    public void whenExecuteAndThrowsException_ThenReturnsTextualResultAndLogs() {

        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createFailedWithException(new Exception("amessage")));

        var result = this.runner.execute(createContextForbidsUsage(), List.of());

        assertTrue(result.isError());
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.Outcome.ThrewException.Message", "amessage"), result.getError());
        assertEquals(2, this.logs.size());
        assertEquals(CliLogEntryType.ERROR, this.logs.get(1).Type);
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.Outcome.ThrewException.Message", "amessage"), this.logs.get(1).Text);
    }

    @Test
    public void whenExecuteAndReturnsEmptyError_ThenReturnsTextualResultAndLogs() {

        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createFailedWithError("", 1));

        var result = this.runner.execute(createContextForbidsUsage(), List.of());

        assertTrue(result.isError());
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.FailedWithEmptyError.Message"), result.getError());
        assertEquals(2, this.logs.size());
        assertEquals(CliLogEntryType.ERROR, this.logs.get(1).Type);
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.FailedWithError.Message", 1,
                                            AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.FailedWithEmptyError.Message")), this.logs.get(1).Text);
    }

    @Test
    public void whenExecuteAndReturnsError_ThenReturnsTextualResultAndLogs() {

        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createFailedWithError("anerror", 1));

        var result = this.runner.execute(createContextForbidsUsage(), List.of());

        assertTrue(result.isError());
        assertEquals("anerror", result.getError());
        assertEquals(2, this.logs.size());
        assertEquals(CliLogEntryType.ERROR, this.logs.get(1).Type);
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.FailedWithError.Message", 1, "anerror"), this.logs.get(1).Text);
    }

    @Test
    public void whenExecuteStructuredAndReturnsEmptyError_ThenReturnsTextualResultAndLogs() {

        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createFailedWithError("", 1));

        var result = this.runner.executeStructured(TestStructure.class, createContextForbidsUsage(), List.of());

        assertTrue(result.isError());
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.FailedWithEmptyError.Message"), result.getError().getErrorMessage());
        assertEquals(2, this.logs.size());
        assertEquals(CliLogEntryType.ERROR, this.logs.get(1).Type);
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.FailedWithError.Message", 1,
                                            AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.FailedWithEmptyError.Message")), this.logs.get(1).Text);
    }

    @Test
    public void whenExecuteStructuredAndReturnsError_ThenReturnsTextualResultAndLogs() {

        var errorJson = new Gson().toJson(new StructuredError("anerror"));
        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createFailedWithError(errorJson, 1));

        var result = this.runner.executeStructured(TestStructure.class, createContextForbidsUsage(), List.of());

        assertTrue(result.isError());
        assertEquals("anerror", result.getError().getErrorMessage());
        assertEquals(2, this.logs.size());
        assertEquals(CliLogEntryType.ERROR, this.logs.get(1).Type);
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.FailedWithError.Message", 1, "anerror"), this.logs.get(1).Text);
    }

    @Test
    public void whenExecuteStructuredAndReturnsException_ThenReturnsTextualResultAndLogs() {

        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createFailedWithException(new Exception("amessage")));

        var result = this.runner.executeStructured(TestStructure.class, createContextForbidsUsage(), List.of());

        assertTrue(result.isError());
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.Outcome.ThrewException.Message", "amessage"), result.getError().getErrorMessage());
        assertEquals(2, this.logs.size());
        assertEquals(CliLogEntryType.ERROR, this.logs.get(1).Type);
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.Outcome.ThrewException.Message", "amessage"), this.logs.get(1).Text);
    }

    @Test
    public void whenExecuteStructuredAndIncludesStructureOutputArguments_ThenDoesNotAppend() {

        var outputJson = new Gson().toJson(new TestStructure("avalue"));
        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createSuccess(outputJson));

        this.runner.executeStructured(TestStructure.class, createContextForbidsUsage(), List.of(OutputStructuredOptionShorthand));

        Mockito.verify(this.processRunner)
          .start(argThat(args -> args.size() == 3
            && args.get(0).equals("anexecutablepath")
            && args.get(1).equals(OutputStructuredOptionShorthand)
            && args.get(2).equals(String.format("%s:false", AutomateConstants.UsageAllowedOption))
          ), anyString());

        this.runner.executeStructured(TestStructure.class, createContextForbidsUsage(), List.of("--output-structured"));

        Mockito.verify(this.processRunner)
          .start(argThat(args -> args.size() == 3
            && args.get(0).equals("anexecutablepath")
            && args.get(1).equals("--output-structured")
            && args.get(2).equals(String.format("%s:false", AutomateConstants.UsageAllowedOption))
          ), anyString());
    }

    @Test
    public void whenExecuteStructuredAndExcludesStructureOutputArgument_ThenAppendsStructureOutputArgument() {

        var outputJson = new Gson().toJson(new TestStructure("avalue"));
        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createSuccess(outputJson));

        this.runner.executeStructured(TestStructure.class, createContextForbidsUsage(), List.of());

        Mockito.verify(this.processRunner)
          .start(argThat(args -> args.size() == 3
            && args.get(0).equals("anexecutablepath")
            && args.get(1).equals(OutputStructuredOptionShorthand)
            && args.get(2).equals(String.format("%s:false", AutomateConstants.UsageAllowedOption))
          ), anyString());
    }

    @Test
    public void whenExecuteStructuredAndAllowUsage_ThenReturnsTextualResultAndLogs() {

        var outputJson = new Gson().toJson(new TestStructure("avalue"));
        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createSuccess(outputJson));

        var result = this.runner.executeStructured(TestStructure.class, createContext(), List.of());

        assertFalse(result.isError());
        assertEquals("avalue", result.getOutput().Output.get(0).Values.AValue);
        assertEquals(2, this.logs.size());
        assertEquals(CliLogEntryType.SUCCESS, this.logs.get(1).Type);
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.Success.Message"), this.logs.get(1).Text);
        Mockito.verify(this.processRunner).start(argThat(args -> args.size() == 4
          && args.get(0).equals("anexecutablepath")
          && args.get(1).equals(AutomateConstants.OutputStructuredOptionShorthand)
          && args.get(2).equals(UsageCorrelationOption)
          && !args.get(3).isEmpty()
        ), argThat(x -> x.equals("acurrentdirectory")));
    }

    @Test
    public void whenExecuteStructuredAndSuccess_ThenReturnsTextualResultAndLogs() {

        var outputJson = new Gson().toJson(new TestStructure("avalue"));
        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createSuccess(outputJson));

        var result = this.runner.executeStructured(TestStructure.class, createContextForbidsUsage(), List.of());

        assertFalse(result.isError());
        assertEquals("avalue", result.getOutput().Output.get(0).Values.AValue);
        assertEquals(2, this.logs.size());
        assertEquals(CliLogEntryType.SUCCESS, this.logs.get(1).Type);
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.CliCommand.Outcome.Success.Message"), this.logs.get(1).Text);
        Mockito.verify(this.processRunner).start(argThat(args -> args.size() == 3
          && args.get(0).equals("anexecutablepath")
          && args.get(1).equals(AutomateConstants.OutputStructuredOptionShorthand)
          && args.get(2).equals(String.format("%s:false", AutomateConstants.UsageAllowedOption))
        ), argThat(x -> x.equals("acurrentdirectory")));
    }

    @Test
    public void whenInstallLatestWithUninstallAndFails_ThenReturnsNull() {

        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createFailedWithError("anerror", 1));

        var result = this.runner.installLatest(true);

        assertEquals(AutomateBundle.message("general.AutomateCliRunner.UninstallCommand.Outcome.FailedWithError.Message", 1, "anerror"), this.logs.get(1).Text);
        assertNull(result);
    }

    @Test
    public void whenInstallLatestWithInstallAndFails_ThenReturnsNull() {

        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createFailedWithError("anerror", 1));

        var result = this.runner.installLatest(false);

        assertEquals(AutomateBundle.message("general.AutomateCliRunner.InstallCommand.Outcome.FailedWithError.Message", 1, "anerror"), this.logs.get(1).Text);
        assertNull(result);
    }

    @Test
    public void whenInstallLatestWithInstallAndSucceedsButNoVersion_ThenThrows() {

        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createSuccess("outputcontainsnoversionnumber"));

        assertThrows(RuntimeException.class, () ->
                       this.runner.installLatest(false),
                     AutomateBundle.message("general.AutomateCliRunner.InstallCommand.ParseVersion.Message"));

        assertEquals(AutomateBundle.message("general.AutomateCliRunner.InstallCommand.ParseVersion.Message"), this.logs.get(1).Text);
    }

    @Test
    public void whenInstallLatestWithInstallAndSucceeds_ThenReturnsVersion() {

        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createSuccess("(version '100.0.0')"));

        var result = this.runner.installLatest(false);

        assertEquals(AutomateBundle.message("general.AutomateCliRunner.InstallCommand.Outcome.Success.Message", "100.0.0"), this.logs.get(1).Text);
        assertEquals(ModuleDescriptor.Version.parse("100.0.0"), result);
    }

    @Test
    public void whenGetCommandFromArgsAndNull_ThenReturnsNull() {

        var result = this.runner.getCommandDescriptorFromArgs(null);

        assertNull(result);
    }

    @Test
    public void whenGetCommandFromArgsAndEmpty_ThenReturnsNull() {

        var result = this.runner.getCommandDescriptorFromArgs(List.of());

        assertNull(result);
    }

    @Test
    public void whenGetCommandFromArgsAndOnlyCommandName_ThenReturnsNull() {

        var result = this.runner.getCommandDescriptorFromArgs(List.of("acommandname"));

        assertNull(result);
    }

    @Test
    public void whenGetCommandFromArgsAndOnlyOneParams_ThenReturnsOne() {

        var result = this.runner.getCommandDescriptorFromArgs(List.of("acommandname", "@param1"));

        assertEquals("param1", result);
    }

    @Test
    public void whenGetCommandFromArgsAndOnlyTwoParams_ThenReturnsTwo() {

        var result = this.runner.getCommandDescriptorFromArgs(List.of("acommandname", "@param1", "@param2"));

        assertEquals("param1.param2", result);
    }

    @Test
    public void whenGetCommandFromArgsAndOnlyThreeParams_ThenReturnsThree() {

        var result = this.runner.getCommandDescriptorFromArgs(List.of("acommandname", "@param1", "@param2", "@param3"));

        assertEquals("param1.param2.param3", result);
    }

    @Test
    public void whenGetCommandFromArgsAndMoreThanThreeParams_ThenReturnsThree() {

        var result = this.runner.getCommandDescriptorFromArgs(List.of("acommandname", "@param1", "@param2", "@param3", "@param4", "@param5"));

        assertEquals("param1.param2.param3", result);
    }

    @Test
    public void whenGetCommandFromArgsAndDataInFirstPlace_ThenReturnsNull() {

        var result = this.runner.getCommandDescriptorFromArgs(List.of("acommandname", "data1", "data2", "data3", "data4", "data5"));

        assertNull(result);
    }

    @Test
    public void whenGetCommandFromArgsAndDataInSecondPlace_ThenReturnsOne() {

        var result = this.runner.getCommandDescriptorFromArgs(List.of("acommandname", "@param1", "data1", "data2", "data3", "data4"));

        assertEquals("param1", result);
    }

    @Test
    public void whenGetCommandFromArgsAndDataInThirdPlace_ThenReturnsTwo() {

        var result = this.runner.getCommandDescriptorFromArgs(List.of("acommandname", "@param1", "@param2", "data1", "data2", "data3"));

        assertEquals("param1.param2", result);
    }

    @Test
    public void whenGetCommandFromArgsAndDataInFourthPlace_ThenReturnsThree() {

        var result = this.runner.getCommandDescriptorFromArgs(List.of("acommandname", "@param1", "@param2", "@param3", "data1", "data2"));

        assertEquals("param1.param2.param3", result);
    }

    @SuppressWarnings("unchecked")
    private void propertyChange(@NotNull PropertyChangeEvent e) {

        AutomateCliRunnerTests.this.logs.addAll((List<CliLogEntry>) e.getNewValue());
    }

    private ExecutionContext createContext() {

        return new ExecutionContext("acurrentdirectory", StringWithDefault.fromValue("anexecutablepath"), true, "asessionid");
    }

    private ExecutionContext createContextForbidsUsage() {

        return new ExecutionContext("acurrentdirectory", StringWithDefault.fromValue("anexecutablepath"), false, "asessionid");
    }
}

class TestResponse {

    public String AValue;

    public TestResponse(String value) {

        this.AValue = value;
    }
}

class TestStructure extends StructuredOutput<TestResponse> {

    public TestStructure(String value) {

        this.Output.add(new StructuredOutputOutput<>(new TestResponse(value)));
    }
}
