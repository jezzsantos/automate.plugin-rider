package jezzsantos.automate.plugin.infrastructure;

import com.intellij.diagnostic.IdeaReportingEvent;
import com.intellij.ide.DataManager;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.idea.IdeaLogger;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.ErrorReportSubmitter;
import com.intellij.openapi.diagnostic.IdeaLoggingEvent;
import com.intellij.openapi.diagnostic.SubmittedReportInfo;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsActions;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.serviceContainer.NonInjectable;
import com.intellij.util.Consumer;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.plugin.application.services.interfaces.INotifier;
import jezzsantos.automate.plugin.application.services.interfaces.NotificationType;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.IContainer;
import jezzsantos.automate.plugin.common.IPluginMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.awt.*;

public class AutomateReportSubmitter extends ErrorReportSubmitter {

    private final ICrashReportSender sender;
    private final ITaskRunner runner;

    private final INotifier notifier;
    private final IPluginMetadata metadata;

    @UsedImplicitly
    public AutomateReportSubmitter() {

        this(new GitHubCrashReportSender(), IContainer.getTaskRunner(), IContainer.getNotifier(), IContainer.getPluginMetadata());
    }

    @TestOnly
    @NonInjectable
    public AutomateReportSubmitter(@NotNull ICrashReportSender sender, @NotNull ITaskRunner runner, @NotNull INotifier notifier, @NotNull IPluginMetadata metadata) {

        this.sender = sender;
        this.runner = runner;
        this.notifier = notifier;
        this.metadata = metadata;
    }

    @TestOnly
    public void sendReport(@Nullable Project project, @Nullable IdeaPluginDescriptor plugin, IdeaLoggingEvent @NotNull [] events, @Nullable String additionalInfo, @Nullable String lastActionId, @NotNull Consumer<? super SubmittedReportInfo> consumer) {

        var report = new ICrashReportSender.ErrorReport();
        report.setDeviceId(this.metadata.getInstallationId());
        report.setReproSteps(additionalInfo);
        report.setVersion(plugin != null
                            ? plugin.getVersion()
                            : "");
        for (var event : events) {
            if (event instanceof IdeaReportingEvent reportingEvent) {
                var message = reportingEvent.getData();
                var exception = message.getThrowable();
                report.addException(exception);
            }
            else {
                var exception = event.getThrowable();
                report.addException(exception);
            }
        }
        report.setLastActionId(lastActionId);

        try {
            AutomateReportSubmitter.this.sender.send(report);
        } catch (Exception ex) {
            this.notifier.alert(NotificationType.WARNING, project, AutomateBundle.message(
              "general.AutomateCrashReporter.Failed.Title"), AutomateBundle.message("general.AutomateCrashReporter.Failed.Message"), null, () -> {
                var info = new SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE);
                consumer.consume(info);
            });
            return;
        }

        var link = AutomateReportSubmitter.this.sender.getLink();
        this.notifier.alert(NotificationType.INFO, project, AutomateBundle.message(
          "general.AutomateCrashReporter.Complete.Title"), link != null
                              ? AutomateBundle.message("general.AutomateCrashReporter.Complete.WithLink.Message")
                              : AutomateBundle.message("general.AutomateCrashReporter.Complete.WithoutLink.Message"), link, () -> {
            var info = new SubmittedReportInfo(SubmittedReportInfo.SubmissionStatus.NEW_ISSUE);
            consumer.consume(info);
        });
    }

    @NlsActions.ActionText
    @NotNull
    @Override
    public String getReportActionText() {

        return AutomateBundle.message("general.AutomateCrashReporter.Action.Title");
    }

    @SuppressWarnings("UnstableApiUsage")
    @NlsContexts.DetailedDescription
    @Nullable
    @Override
    public String getPrivacyNoticeText() {

        //TODO: Perhaps add/update a privacy policy here? https://jezzsantos.github.io/automate/privacy/
        return super.getPrivacyNoticeText();
    }

    @Override
    public boolean submit(IdeaLoggingEvent @NotNull [] events, @Nullable String additionalInfo, @NotNull Component parentComponent, @NotNull Consumer<? super SubmittedReportInfo> consumer) {

        var mgr = DataManager.getInstance();
        var context = mgr.getDataContext(parentComponent);
        var project = CommonDataKeys.PROJECT.getData(context);

        var descriptor = getPluginDescriptor();
        var plugin = descriptor instanceof IdeaPluginDescriptor
          ? (IdeaPluginDescriptor) descriptor
          : null;

        this.runner.runModeless(project, AutomateBundle.message("general.AutomateCrashReporter.Progress.Message"),
                                () -> sendReport(project, plugin, events, additionalInfo, IdeaLogger.ourLastActionId, consumer));
        return true;
    }
}
