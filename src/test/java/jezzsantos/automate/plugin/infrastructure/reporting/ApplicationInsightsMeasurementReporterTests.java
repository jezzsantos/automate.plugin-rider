package jezzsantos.automate.plugin.infrastructure.reporting;

import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.common.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

public class ApplicationInsightsMeasurementReporterTests {

    private ITelemetryClient telemetryClient;
    private ApplicationInsightsMeasurementReporter reporter;

    @BeforeEach
    public void setUp() {

        this.telemetryClient = Mockito.mock(ITelemetryClient.class);
        Mockito.when(this.telemetryClient.getOperationId())
          .thenReturn("anoperationid");
        this.reporter = new ApplicationInsightsMeasurementReporter(this.telemetryClient);
    }

    @Test
    public void whenMeasureEventAndReportingDisabled_ThenDoesNotReport() {

        this.reporter.measureEvent("aneventname", null);

        Mockito.verify(this.telemetryClient, Mockito.never()).trackEvent(any());
    }

    @Test
    public void whenMeasureEventAndHasNoProperties_ThenReports() {

        this.reporter.enableReporting("amachineid", "asessionid");

        this.reporter.measureEvent("An.Event.Name", null);

        Mockito.verify(this.telemetryClient)
          .trackEvent(argThat(telemetry ->
                                telemetry.getName().equals("an.event.name")
                                  && telemetry.getProperties().size() == 0
                                  && telemetry.getContext().getOperation().getParentId().equals("anoperationid")
          ));
    }

    @Test
    public void whenMeasureEventAndHasSomeProperties_ThenReports() {

        this.reporter.enableReporting("amachineid", "asessionid");

        this.reporter.measureEvent("An.Event.Name", Map.of(
          "aname1", "avalue1",
          "aname2", "avalue2"
        ));

        Mockito.verify(this.telemetryClient)
          .trackEvent(argThat(telemetry ->
                                telemetry.getName().equals("an.event.name")
                                  && telemetry.getProperties().size() == 2
                                  && telemetry.getProperties().get("aname1").equals("avalue1")
                                  && telemetry.getProperties().get("aname2").equals("avalue2")
                                  && telemetry.getContext().getOperation().getParentId().equals("anoperationid")
          ));
    }

    @Test
    public void whenMeasureCliCallAndReportingDisabled_ThenDoesNotReport() {

        this.reporter.measureCliCall(builder -> true, "anactionname", "acommand");

        Mockito.verify(this.telemetryClient, Mockito.never()).trackDependency(any());
    }

    @Test
    public void whenMeasureCliCallAndCallThrows_ThenReports() {

        this.reporter.enableReporting("amachineid", "asessionid");

        assertThrows(Exception.class, () -> this.reporter.measureCliCall(builder -> {
            Try.safely(() -> Thread.sleep(50));
            throw new RuntimeException("amessage");
        }, "anactionname", "acommand"));

        Mockito.verify(this.telemetryClient)
          .trackDependency(argThat(dep ->
                                     dep.getName().equals("anactionname")
                                       && dep.getCommandName().equals("acommand")
                                       && !dep.getId().isEmpty()
                                       && dep.getType().equals("commandline")
                                       && dep.getTimestamp().before(Date.from(Instant.now()))
                                       && dep.getTarget().equals(AutomateConstants.ApplicationInsightsCliRoleName)
                                       && !dep.getSuccess()
                                       && dep.getDuration().getTotalMilliseconds() >= 50
                                       && dep.getContext().getOperation().getParentId().equals("anoperationid")
          ));
    }

    @Test
    public void whenMeasureCliCallAndCallSucceeds_ThenReports() {

        this.reporter.enableReporting("amachineid", "asessionid");

        this.reporter.measureCliCall(builder -> {
            Try.safely(() -> Thread.sleep(50));
            return true;
        }, "anactionname", "acommand");

        Mockito.verify(this.telemetryClient)
          .trackDependency(argThat(dep ->
                                     dep.getName().equals("anactionname")
                                       && dep.getCommandName().equals("acommand")
                                       && !dep.getId().isEmpty()
                                       && dep.getType().equals("commandline")
                                       && dep.getTimestamp().before(Date.from(Instant.now()))
                                       && dep.getTarget().equals(AutomateConstants.ApplicationInsightsCliRoleName)
                                       && dep.getSuccess()
                                       && dep.getDuration().getTotalMilliseconds() >= 50
                                       && dep.getContext().getOperation().getParentId().equals("anoperationid")
          ));
    }

    @Nested
    class GivenACorrelationBuilder {

        private ApplicationInsightsMeasurementReporter.CorrelationIdBuilder builder;

        @BeforeEach
        public void setUp() {

            this.builder = new ApplicationInsightsMeasurementReporter.CorrelationIdBuilder("anoperationid", "adependencyid");
        }

        @Test
        public void whenBuild_ThenReturnsCorrelationId() {

            var result = this.builder.build("asessionid");

            assertEquals("asessionid|anoperationid|adependencyid", result);
        }
    }
}
