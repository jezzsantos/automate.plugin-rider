package jezzsantos.automate.plugin.infrastructure;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.services.interfaces.INotifier;
import jezzsantos.automate.plugin.application.services.interfaces.NotificationType;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.IPluginMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;

public class AutomateReportSubmitterTests {

    private AutomateReportSubmitter submitter;
    private ICrashReportSender sender;
    private INotifier notifier;

    @BeforeEach
    public void setUp() {

        this.sender = Mockito.mock(ICrashReportSender.class);
        var runner = Mockito.mock(ITaskRunner.class);
        this.notifier = Mockito.mock(INotifier.class);
        var metadata = Mockito.mock(IPluginMetadata.class);
        Mockito.when(metadata.getInstallationId())
          .thenReturn("aninstallationid");

        this.submitter = new AutomateReportSubmitter(this.sender, runner, this.notifier, metadata);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void whenSendReport_ThenSendsAndNotifiesSuccess() throws Exception {

        var project = Mockito.mock(Project.class);
        var plugin = Mockito.mock(IdeaPluginDescriptor.class);
        Mockito.when(plugin.getVersion())
          .thenReturn("aversion");

        this.submitter.sendReport(project, plugin, new IdeaLoggingEvent[0], "additionalinfo", "alastactionid", info -> {});

        Mockito.verify(this.sender).send(argThat(report ->
                                                   report.getDeviceId().equals("aninstallationid")
                                                     && report.getReproSteps().equals("additionalinfo")
                                                     && report.getVersion().equals("aversion")
                                                     && report.getLastActionId().equals("alastactionid")
        ));
        Mockito.verify(this.notifier)
          .alert(argThat(x -> x == NotificationType.INFO), argThat(x -> x == project),
                 argThat(s -> s.equals(AutomateBundle.message("general.AutomateCrashReporter.Complete.Title"))),
                 argThat(s -> s.equals(AutomateBundle.message("general.AutomateCrashReporter.Complete.WithoutLink.Message"))), isNull(), any());
    }

    @Test
    public void whenSendReportAndThrows_ThenNotifiesFailure() throws Exception {

        var project = Mockito.mock(Project.class);
        var plugin = Mockito.mock(IdeaPluginDescriptor.class);
        Mockito.when(plugin.getVersion())
          .thenReturn("aversion");
        doThrow(Exception.class)
          .when(this.sender)
          .send(any());

        this.submitter.sendReport(project, plugin, new IdeaLoggingEvent[0], "additionalinfo", "alastactionid", info -> {});

        Mockito.verify(this.sender).send(any());
        Mockito.verify(this.notifier)
          .alert(argThat(x -> x == NotificationType.WARNING), argThat(x -> x == project),
                 argThat(s -> s.equals(AutomateBundle.message("general.AutomateCrashReporter.Failed.Title"))),
                 argThat(s -> s.equals(AutomateBundle.message("general.AutomateCrashReporter.Failed.Message"))), isNull(), any());
    }
}
