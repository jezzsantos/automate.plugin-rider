package jezzsantos.automate.plugin.infrastructure;

import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.CrashLevel;
import jezzsantos.automate.plugin.common.IRecorder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

public class ApplicationInsightsCrashReportSenderTests {

    private IRecorder recorder;
    private ApplicationInsightsCrashReportSender sender;

    @BeforeEach
    public void setUp() throws IOException {

        this.recorder = Mockito.mock(IRecorder.class);

        this.sender = new ApplicationInsightsCrashReportSender(this.recorder);
    }

    @Test
    public void whenSendAndNoData_ThenRecordsCrash() {

        var report = new ICrashReportSender.ErrorReport();
        report.setVersion(null);
        report.setDeviceId(null);
        report.setReproSteps(null);
        report.setLastActionId(null);

        this.sender.send(report);

        Mockito.verify(this.recorder)
          .crash(argThat(x -> x == CrashLevel.FATAL),
                 argThat(x -> x.getMessage().equals(AutomateBundle.message("general.ApplicationInsightsCrashReportSender.UnknownException.Message"))),
                 argThat(x -> x.size() == 3
                   && x.get("Plugin Version").equals(AutomateBundle.message("general.AICrashReportSender.UnknownEntry.Message"))
                   && x.get("Last ActionId").equals(AutomateBundle.message("general.AICrashReportSender.EmptyEntry.Message"))
                   && x.get("User Comments").equals(AutomateBundle.message("general.AICrashReportSender.EmptyEntry.Message"))
                 ), argThat(x -> x.equals(AutomateBundle.message("general.ApplicationInsightsCrashReportSender.UnknownException.Message"))), any());
    }

    @Test
    public void whenSendAndAllData_ThenRecordsCrash() {

        var exception = new Exception("amessage");
        var report = new ICrashReportSender.ErrorReport();
        report.setVersion("aversion");
        report.setDeviceId("adeviceid");
        report.setReproSteps("areprostep");
        report.setLastActionId("alastactionid");
        report.addException(exception);

        this.sender.send(report);

        Mockito.verify(this.recorder)
          .crash(argThat(x -> x == CrashLevel.FATAL),
                 argThat(x -> x == exception),
                 argThat(x -> x.size() == 3
                   && x.get("Plugin Version").equals("aversion")
                   && x.get("Last ActionId").equals("alastactionid")
                   && x.get("User Comments").equals("areprostep")
                 ), argThat(x -> x.equals("amessage")), any());
    }
}
