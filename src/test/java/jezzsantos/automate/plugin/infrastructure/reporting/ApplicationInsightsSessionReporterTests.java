package jezzsantos.automate.plugin.infrastructure.reporting;

import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.common.recording.ILogger;
import jezzsantos.automate.plugin.common.recording.LogLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

public class ApplicationInsightsSessionReporterTests {

    private ILogger logger;
    private ITelemetryClient telemetryClient;
    private ApplicationInsightsSessionReporter reporter;

    @BeforeEach
    public void setUp() {

        this.logger = Mockito.mock(ILogger.class);
        this.telemetryClient = Mockito.mock(ITelemetryClient.class);
        Mockito.when(this.telemetryClient.getOperationId())
          .thenReturn("anoperationid");
        this.reporter = new ApplicationInsightsSessionReporter(this.logger, this.telemetryClient);
    }

    @Test
    public void whenEnableReporting_ThenConfiguresClient() {

        this.reporter.enableReporting("amachineid", "asessionid");

        Mockito.verify(this.telemetryClient).setRoleInstance("amachineid");
        Mockito.verify(this.telemetryClient).setDeviceId("amachineid");
        Mockito.verify(this.telemetryClient).setUserId("amachineid");
        Mockito.verify(this.telemetryClient).setSessionId("asessionid");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void whenMeasureStartSession_ThenAddsSessionOperation() {

        this.reporter.measureStartSession();

        var operation = this.reporter.getOperations().getCurrent();
        assertEquals(ApplicationInsightsSessionReporter.SessionName, operation.getName());
        assertNull(operation.getParent());
        Mockito.verify(this.telemetryClient, Mockito.never()).trackRequest(any());
        Mockito.verify(this.telemetryClient).setOperationId(operation.getId());
    }

    @Test
    public void whenMeasureEndSessionAndReportingNotEnabled_ThenDoesNotSendTelemetry() {

        this.reporter.measureStartSession();

        this.reporter.measureEndSession(true);

        var operation = this.reporter.getOperations().getCurrent();
        assertNull(operation);
        Mockito.verify(this.telemetryClient, Mockito.never()).trackRequest(any());
        Mockito.verify(this.telemetryClient).setOperationId(null);
        Mockito.verify(this.logger, Mockito.never()).log(any(), any(), any());
        Mockito.verify(this.telemetryClient, Mockito.never()).sendAllTelemetry();
    }

    @Test
    public void whenMeasureEndSession_ThenSendsTelemetry() {

        this.reporter.enableReporting("amachineid", "asessionid");
        this.reporter.measureStartSession();
        Try.safely(() -> Thread.sleep(50));

        this.reporter.measureEndSession(true);

        var operation = this.reporter.getOperations().getCurrent();
        assertNull(operation);
        Mockito.verify(this.telemetryClient)
          .trackRequest(argThat(req ->
                                  req.getName().equals("jbrd-operation-session")
                                    && !req.getId().isEmpty()
                                    && req.getTimestamp().before(Date.from(Instant.now()))
                                    && req.getContext().getOperation().getId() == null
                                    && req.getContext().getOperation().getParentId() == null
          ));
        Mockito.verify(this.telemetryClient).setOperationId(null);
        Mockito.verify(this.logger).log(LogLevel.DEBUG, null, AutomateBundle.message("trace.ApplicationInsightsMeasurementReporter.Flushing.Message"));
        Mockito.verify(this.telemetryClient).sendAllTelemetry();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void whenMeasureEndSessionAndOpenOperations_ThenClosesOpenOperationsAndSendsTelemetry() {

        this.reporter.enableReporting("amachineid", "asessionid");
        this.reporter.measureStartSession();
        var sessionOperationId = this.reporter.getOperations().getCurrent().getId();
        this.reporter.measureStartOperation("anoperationname1");
        var operationId1 = this.reporter.getOperations().getCurrent().getId();
        this.reporter.measureStartOperation("anoperationname2");
        Try.safely(() -> Thread.sleep(50));

        this.reporter.measureEndSession(true);

        var operation = this.reporter.getOperations().getCurrent();
        assertNull(operation);
        var mockOrder = Mockito.inOrder(this.telemetryClient);
        mockOrder.verify(this.telemetryClient)
          .trackRequest(argThat(req ->
                                  req.getName().equals("jbrd-operation-anoperationname2")
                                    && !req.getId().isEmpty()
                                    && req.getTimestamp().before(Date.from(Instant.now()))
                                    && req.getContext().getOperation().getParentId().equals(operationId1)
          ));
        mockOrder.verify(this.telemetryClient).setOperationId(operationId1);
        mockOrder.verify(this.telemetryClient)
          .trackRequest(argThat(req ->
                                  req.getName().equals("jbrd-operation-anoperationname1")
                                    && !req.getId().isEmpty()
                                    && req.getTimestamp().before(Date.from(Instant.now()))
                                    && req.getContext().getOperation().getParentId().equals(sessionOperationId)
          ));
        mockOrder.verify(this.telemetryClient).setOperationId(sessionOperationId);
        mockOrder.verify(this.telemetryClient)
          .trackRequest(argThat(req ->
                                  req.getName().equals("jbrd-operation-session")
                                    && !req.getId().isEmpty()
                                    && req.getTimestamp().before(Date.from(Instant.now()))
                                    && req.getContext().getOperation().getParentId() == null
          ));
        mockOrder.verify(this.telemetryClient).setOperationId(null);
        Mockito.verify(this.logger).log(LogLevel.DEBUG, null, AutomateBundle.message("trace.ApplicationInsightsMeasurementReporter.Flushing.Message"));
        Mockito.verify(this.telemetryClient).sendAllTelemetry();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void whenMeasureEndOperationAndNotLastOperationName_ThenDoesNothing() {

        this.reporter.enableReporting("amachineid", "asessionid");
        this.reporter.measureStartOperation("anoperationname");
        Try.safely(() -> Thread.sleep(50));

        this.reporter.measureEndOperation("anotheroperationname", true);

        var operation = this.reporter.getOperations().getCurrent();
        assertEquals("anoperationname", operation.getName());
        Mockito.verify(this.telemetryClient, Mockito.never()).trackRequest(any());
    }
}
