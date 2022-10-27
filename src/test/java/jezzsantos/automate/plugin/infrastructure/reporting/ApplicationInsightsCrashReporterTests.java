package jezzsantos.automate.plugin.infrastructure.reporting;

import com.microsoft.applicationinsights.telemetry.SeverityLevel;
import jezzsantos.automate.plugin.common.recording.CrashLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

public class ApplicationInsightsCrashReporterTests {

    private ITelemetryClient telemetryClient;
    private ApplicationInsightsCrashReporter reporter;

    @BeforeEach
    public void setUp() {

        this.telemetryClient = Mockito.mock(ITelemetryClient.class);
        Mockito.when(this.telemetryClient.getOperationId())
          .thenReturn("anoperationid");
        this.reporter = new ApplicationInsightsCrashReporter(this.telemetryClient);
    }

    @Test
    public void whenCrashAndReportingDisabled_ThenDoesNotReport() {

        this.reporter.crash(CrashLevel.FATAL, new Exception("amessage"), "amessagetemplate");

        Mockito.verify(this.telemetryClient, Mockito.never()).trackException(any());
    }

    @Test
    public void whenCrashAndHasNoProperties_ThenReports() {

        this.reporter.enableReporting("amachineid", "asessionid");
        var exception = new Exception("amessage");

        this.reporter.crash(CrashLevel.FATAL, exception, "amessagetemplate");

        Mockito.verify(this.telemetryClient)
          .trackException(argThat(telemetry ->
                                    telemetry.getException() == exception
                                      && telemetry.getSeverityLevel() == SeverityLevel.Critical
                                      && telemetry.getProperties().size() == 2
                                      && telemetry.getProperties().get("Message_Template").equals("amessagetemplate")
                                      && telemetry.getProperties().get("Message_Arguments").equals("")
                                      && telemetry.getContext().getOperation().getParentId().equals("anoperationid")
          ));
    }

    @Test
    public void whenCrashAndHasSomeArgsAndNoProperties_ThenReports() {

        this.reporter.enableReporting("amachineid", "asessionid");
        var exception = new Exception("amessage");

        this.reporter.crash(CrashLevel.FATAL, exception, "amessagetemplate", "anarg1", "anarg2");

        Mockito.verify(this.telemetryClient)
          .trackException(argThat(telemetry ->
                                    telemetry.getException() == exception
                                      && telemetry.getSeverityLevel() == SeverityLevel.Critical
                                      && telemetry.getProperties().size() == 2
                                      && telemetry.getProperties().get("Message_Template").equals("amessagetemplate")
                                      && telemetry.getProperties().get("Message_Arguments").equals("anarg1, anarg2")
                                      && telemetry.getContext().getOperation().getParentId().equals("anoperationid")
          ));
    }

    @Test
    public void whenCrashAndHasSomeProperties_ThenReports() {

        this.reporter.enableReporting("amachineid", "asessionid");
        var exception = new Exception("amessage");
        var properties = Map.of(
          "aname1", "avalue1",
          "aname2", "avalue2"
        );

        this.reporter.crash(CrashLevel.FATAL, exception, properties, "amessagetemplate", "anarg1", "anarg2");

        Mockito.verify(this.telemetryClient)
          .trackException(argThat(telemetry ->
                                    telemetry.getException() == exception
                                      && telemetry.getSeverityLevel() == SeverityLevel.Critical
                                      && telemetry.getProperties().size() == 4
                                      && telemetry.getProperties().get("aname1").equals("avalue1")
                                      && telemetry.getProperties().get("aname2").equals("avalue2")
                                      && telemetry.getProperties().get("Message_Template").equals("amessagetemplate")
                                      && telemetry.getProperties().get("Message_Arguments").equals("anarg1, anarg2")
                                      && telemetry.getContext().getOperation().getParentId().equals("anoperationid")
          ));
    }
}
