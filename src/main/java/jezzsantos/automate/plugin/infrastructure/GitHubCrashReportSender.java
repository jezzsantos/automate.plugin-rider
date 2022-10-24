package jezzsantos.automate.plugin.infrastructure;

import jezzsantos.automate.ApplicationSettings;
import jezzsantos.automate.plugin.application.services.interfaces.INotifier;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.kohsuke.github.GHRateLimit;
import org.kohsuke.github.GitHubBuilder;
import org.kohsuke.github.RateLimitChecker;
import org.kohsuke.github.connector.GitHubConnector;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class GitHubCrashReportSender implements ICrashReportSender {

    public static final String GitHubRepoSlug = "jezzsantos/automate.plugin-rider";
    public static final String GitHubIssueLabel = "crash-report";
    private static final String GitHubUserId = "jezzsantos";
    private final GitHubConnector connector;

    public GitHubCrashReportSender() {

        this(null);
    }

    @TestOnly
    public GitHubCrashReportSender(@Nullable GitHubConnector connector) {

        this.connector = connector;
    }

    @Override
    public void send(@NotNull ErrorReport report) throws Exception {

        var gitHubPersonalAccessToken = ApplicationSettings.setting("gitHubApiAccessToken");
        var builder = new GitHubBuilder();
        if (!gitHubPersonalAccessToken.isEmpty()) {
            builder.withOAuthToken(gitHubPersonalAccessToken, GitHubUserId);
        }
        builder.withRateLimitChecker(new RateLimitChecker() {
            @Override
            protected boolean checkRateLimit(GHRateLimit.Record rateLimitRecord, long count) throws InterruptedException {

                var limit = rateLimitRecord.getLimit();
                var remaining = rateLimitRecord.getRemaining();

                if (limit - remaining > 10) {
                    return true;
                }

                return super.checkRateLimit(rateLimitRecord, count);
            }
        });
        builder.withConnector(this.connector);
        var gitHubClient = builder.build();

        gitHubClient.getRepository(GitHubRepoSlug)
          .createIssue(AutomateBundle.message("general.GitHubCrashReportSender.Title.Title"))
          .label(GitHubIssueLabel)
          .body(buildBodyMarkdown(report))
          .create();
    }

    @Nullable
    @Override
    public INotifier.LinkDescriptor getLink() {

        return new INotifier.LinkDescriptor(String.format("https://github.com/%s/issues", GitHubRepoSlug),
                                            AutomateBundle.message("general.GitHubCrashReportSender.MoreInfoLink.Title"));
    }

    @NotNull
    private String buildBodyMarkdown(@NotNull ErrorReport report) {

        var version = toMarkdownParagraph(String.format("Plugin Version: %s", Objects.requireNonNullElse(report.getVersion(), AutomateBundle.message(
          "general.GitHubCrashReportSender.UnknownEntry.Message"))));
        var deviceId = toMarkdownParagraph(String.format("Device ID: %s",
                                                         Objects.requireNonNullElse(report.getDeviceId(), AutomateBundle.message(
                                                           "general.GitHubCrashReportSender.UnknownEntry.Message"))));
        var lastAction = toMarkdownParagraph(String.format("Last ActionId: %s",
                                                           Objects.requireNonNullElse(report.getLastActionId(),
                                                                                      AutomateBundle.message("general.GitHubCrashReportSender.EmptyEntry.Message"))));
        var repro = toMarkdownParagraph(String.format("User Comments: %s",
                                                      Objects.requireNonNullElse(report.getReproSteps(),
                                                                                 AutomateBundle.message("general.GitHubCrashReportSender.EmptyEntry.Message"))));
        var exception = toMarkdownParagraph("Exceptions:");
        var exceptions = toMarkdownParagraph(String.format("%s", report.getExceptions()
          .stream()
          .map(this::toMarkdown)
          .collect(Collectors.joining())));

        return version + deviceId + lastAction + repro + exception + exceptions;
    }

    private String toMarkdown(@NotNull Throwable exception) {

        var cause = exception.getCause();
        var message = toMarkdownParagraph(String.format("Cause: `%s`", cause != null
          ? cause.getMessage()
          : exception.getMessage()));
        var stackTrace = toMarkdownParagraph(toMarkdown(cause != null
                                                          ? cause.getStackTrace()
                                                          : exception.getStackTrace()));

        return String.format("%s\r\n```\n%s```", message, stackTrace);
    }

    private String toMarkdown(StackTraceElement[] stackTrace) {

        return Arrays.stream(stackTrace)
          .map(trace -> toMarkdownParagraph(trace.toString()))
          .collect(Collectors.joining());
    }

    private String toMarkdownParagraph(@NotNull String text) {

        return String.format("%s\r\n", text);
    }
}
