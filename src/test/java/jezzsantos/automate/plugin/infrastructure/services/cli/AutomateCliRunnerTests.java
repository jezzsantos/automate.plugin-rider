package jezzsantos.automate.plugin.infrastructure.services.cli;

import com.google.gson.Gson;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntryType;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import static jezzsantos.automate.core.AutomateConstants.OutputStructuredShorthand;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

public class AutomateCliRunnerTests {

    private AutomateCliRunner runner;
    private List<CliLogEntry> logs;
    private IProcessRunner processRunner;

    @BeforeEach
    public void setUp() {

        var platform = Mockito.mock(IOsPlatform.class);
        this.processRunner = Mockito.mock(IProcessRunner.class);
        this.logs = new ArrayList<>();

        this.runner = new AutomateCliRunner(platform, this.processRunner);
        this.runner.addLogListener(this::propertyChange);
    }

    @AfterEach
    public void tearDown() {

        this.runner.removeLogListener(this::propertyChange);
    }

    @Test
    public void whenLog_ThenAddsToLogs() {

        this.runner.log(new CliLogEntry("atext", CliLogEntryType.Normal));

        assertEquals(1, this.logs.size());
        assertEquals("atext", this.logs.get(0).Text);
        assertEquals(CliLogEntryType.Normal, this.logs.get(0).Type);
    }

    @Test
    public void whenGetCliLogsAndNoLogs_ThenReturnsEmpty() {

        var result = this.runner.getLogs();

        assertTrue(result.isEmpty());
    }

    @Test
    public void whenGetCliLogs_ThenReturnsLogs() {

        this.runner.log(new CliLogEntry("atext", CliLogEntryType.Normal));

        var result = this.runner.getLogs();

        assertEquals(1, result.size());
        assertEquals("atext", result.get(0).Text);
        assertEquals(CliLogEntryType.Normal, result.get(0).Type);
    }

    @Test
    public void whenExecuteAndSucceeds_ThenReturnsTextualResultAndLogs() {

        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createSuccess("anoutput"));

        var result = this.runner.execute("anexecutablepath", List.of());

        assertFalse(result.isError());
        assertEquals("anoutput", result.getOutput());
        assertEquals(2, this.logs.size());
        assertEquals(CliLogEntryType.Success, this.logs.get(1).Type);
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.Outcome.Success.Message"), this.logs.get(1).Text);
    }

    @Test
    public void whenExecuteAndFailsToStart_ThenReturnsTextualResultAndLogs() {

        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createFailedToStart());

        var result = this.runner.execute("anexecutablepath", List.of());

        assertTrue(result.isError());
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.Outcome.FailedToStart.Message", "anexecutablepath"), result.getError());
        assertEquals(2, this.logs.size());
        assertEquals(CliLogEntryType.Error, this.logs.get(1).Type);
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.Outcome.FailedToStart.Message", "anexecutablepath"), this.logs.get(1).Text);
    }

    @Test
    public void whenExecuteAndThrowsException_ThenReturnsTextualResultAndLogs() {

        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createFailedWithException(new Exception("amessage")));

        var result = this.runner.execute("anexecutablepath", List.of());

        assertTrue(result.isError());
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.Outcome.ThrewException.Message", "amessage"), result.getError());
        assertEquals(2, this.logs.size());
        assertEquals(CliLogEntryType.Error, this.logs.get(1).Type);
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.Outcome.ThrewException.Message", "amessage"), this.logs.get(1).Text);
    }

    @Test
    public void whenExecuteAndReturnsError_ThenReturnsTextualResultAndLogs() {

        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createFailedWithError("anerror"));

        var result = this.runner.execute("anexecutablepath", List.of());

        assertTrue(result.isError());
        assertEquals("anerror", result.getError());
        assertEquals(2, this.logs.size());
        assertEquals(CliLogEntryType.Error, this.logs.get(1).Type);
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.Outcome.FailedWithError.Message", "anerror"), this.logs.get(1).Text);
    }

    @Test
    public void whenExecuteStructuredAndReturnsError_ThenReturnsTextualResultAndLogs() {

        var errorJson = new Gson().toJson(new StructuredError("anerror"));
        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createFailedWithError(errorJson));

        var result = this.runner.executeStructured(TestStructure.class, "anexecutablepath", List.of());

        assertTrue(result.isError());
        assertEquals("anerror", result.getError().getErrorMessage());
        assertEquals(2, this.logs.size());
        assertEquals(CliLogEntryType.Error, this.logs.get(1).Type);
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.Outcome.FailedWithError.Message", "anerror"), this.logs.get(1).Text);
    }

    @Test
    public void whenExecuteStructuredAndIncludesStructureOutputArguments_ThenDoesNotAppend() {

        var outputJson = new Gson().toJson(new TestStructure("avalue"));
        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createSuccess(outputJson));

        this.runner.executeStructured(TestStructure.class, "anexecutablepath", List.of(OutputStructuredShorthand));

        Mockito.verify(this.processRunner)
          .start(argThat(x -> x.size() == 2 && x.get(0).equals("anexecutablepath") && x.get(1).equals(OutputStructuredShorthand)), any(IOsPlatform.class));

        this.runner.executeStructured(TestStructure.class, "anexecutablepath", List.of("--output-structured"));

        Mockito.verify(this.processRunner)
          .start(argThat(x -> x.size() == 2 && x.get(0).equals("anexecutablepath") && x.get(1).equals("--output-structured")), any(IOsPlatform.class));
    }

    @Test
    public void whenExecuteStructuredAndExcludesStructureOutputArgument_ThenAppendsStructureOutputArgument() {

        var outputJson = new Gson().toJson(new TestStructure("avalue"));
        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createSuccess(outputJson));

        this.runner.executeStructured(TestStructure.class, "anexecutablepath", List.of());

        Mockito.verify(this.processRunner)
          .start(argThat(x -> x.size() == 2 && x.get(0).equals("anexecutablepath") && x.get(1).equals(OutputStructuredShorthand)), any(IOsPlatform.class));
    }

    @Test
    public void whenExecuteStructuredAndSuccess_ThenReturnsTextualResultAndLogs() {

        var outputJson = new Gson().toJson(new TestStructure("avalue"));
        Mockito.when(this.processRunner.start(anyList(), any()))
          .thenReturn(ProcessResult.createSuccess(outputJson));

        var result = this.runner.executeStructured(TestStructure.class, "anexecutablepath", List.of());

        assertFalse(result.isError());
        assertEquals("avalue", result.getOutput().Output.get(0).Values.AValue);
        assertEquals(2, this.logs.size());
        assertEquals(CliLogEntryType.Success, this.logs.get(1).Type);
        assertEquals(AutomateBundle.message("general.AutomateCliRunner.Outcome.Success.Message"), this.logs.get(1).Text);
    }

    @SuppressWarnings("unchecked")
    private void propertyChange(PropertyChangeEvent e) {

        AutomateCliRunnerTests.this.logs.addAll((List<CliLogEntry>) e.getNewValue());
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
