package jezzsantos.automate.plugin.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class RecorderTests {

    private ILogger logger;
    private ICrashReporter crasher;
    private IMeasurementReporter measurer;
    private Recorder recorder;

    @BeforeEach
    public void setUp() {

        this.logger = Mockito.mock(ILogger.class);
        this.crasher = Mockito.mock(ICrashReporter.class);
        this.measurer = Mockito.mock(IMeasurementReporter.class);
        var metadata = Mockito.mock(IPluginMetadata.class);
        Mockito.when(metadata.getInstallationId())
          .thenReturn("amachineid");

        this.recorder = new Recorder(this.logger, this.crasher, this.measurer, metadata);
    }

    @Test
    public void whenConstructed_ThenContextSet() {

        var result = this.recorder.getReportingContext();

        assertFalse(result.getAllowUsage());
        assertEquals("amachineid", result.getMachineId());
        assertNotNull(result.getSessionId());
    }

    @Test
    public void whenStartSessionWithEnabled_ThenEnablesReporting() {

        this.recorder.startSession(true, "amessagetemplate");

        var result = this.recorder.getReportingContext();

        assertTrue(result.getAllowUsage());
        assertEquals("amachineid", result.getMachineId());
        assertNotNull(result.getSessionId());
        Mockito.verify(this.measurer).enableReporting("amachineid", result.getSessionId());
        Mockito.verify(this.crasher).enableReporting("amachineid", result.getSessionId());
    }

    @Test
    public void whenTrace_ThenDoesTrace() {

        this.recorder.trace(LogLevel.INFORMATION, "amessagetemplate");

        Mockito.verify(this.logger).log(LogLevel.INFORMATION, null, "amessagetemplate");
    }

    @Test
    public void whenCount_ThenDoesNotCountButTraces() {

        this.recorder.measureEvent("aneventname", null);

        Mockito.verify(this.logger).log(LogLevel.INFORMATION, null, AutomateBundle.message("trace.Recorder.Measure", "jbrd_aneventname"));
        Mockito.verify(this.measurer, Mockito.never()).measureEvent(any(), any());
    }

    @Test
    public void whenCountAndReportingEnabled_ThenDoesNotCountButTraces() {

        this.recorder.startSession(true, "amessagetemplate");

        this.recorder.measureEvent("aneventname", null);

        Mockito.verify(this.logger).log(LogLevel.INFORMATION, null, AutomateBundle.message("trace.Recorder.Measure", "jbrd_aneventname"));
        Mockito.verify(this.measurer).measureEvent("jbrd_aneventname", null);
    }

    @Test
    public void whenCrash_ThenDoesNotCrashButTraces() {

        var exception = new Exception("amessage");

        this.recorder.crash(CrashLevel.FATAL, exception, "amessagetemplate");

        Mockito.verify(this.logger).log(LogLevel.ERROR, exception, AutomateBundle.message("trace.Recorder.Crash", "amessagetemplate"));
        Mockito.verify(this.crasher, Mockito.never()).crash(any(), any(), any());
    }

    @Test
    public void whenCrashAndReportingEnabled_ThenDoesNotCrashButTraces() {

        var exception = new Exception("amessage");
        this.recorder.startSession(true, "amessagetemplate");

        this.recorder.crash(CrashLevel.FATAL, exception, "amessagetemplate");

        Mockito.verify(this.logger).log(LogLevel.ERROR, exception, AutomateBundle.message("trace.Recorder.Crash", "amessagetemplate"));
        Mockito.verify(this.crasher).crash(CrashLevel.FATAL, exception, (Map<String, String>) null, "amessagetemplate");
    }
}
