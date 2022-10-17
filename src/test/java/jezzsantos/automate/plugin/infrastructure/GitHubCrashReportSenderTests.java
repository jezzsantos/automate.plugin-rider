package jezzsantos.automate.plugin.infrastructure;

import com.google.gson.Gson;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.Try;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kohsuke.github.connector.GitHubConnector;
import org.kohsuke.github.connector.GitHubConnectorRequest;
import org.kohsuke.github.connector.GitHubConnectorResponse;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;

public class GitHubCrashReportSenderTests {

    private GitHubCrashReportSender sender;
    private GitHubConnector connector;

    @BeforeEach
    public void setUp() throws IOException {

        this.connector = Mockito.mock(GitHubConnector.class);
        Mockito.when(this.connector.send(any()))
          .thenAnswer(invocation -> new GitHubConnectorResponse.ByteArrayResponse(((GitHubConnectorRequest) invocation.getArguments()[0]), 200, Map.of()) {
              @Override
              protected InputStream rawBodyStream() {

                  var url = this.request().url().toString();

                  if (url.contains(String.format("/repos/%s", GitHubCrashReportSender.GitHubRepoSlug))) {
                      return new ByteArrayInputStream(
                        String.format("{\"id\":1234567,\"full_name\":\"%s\"}", GitHubCrashReportSender.GitHubRepoSlug).getBytes(StandardCharsets.UTF_8));
                  }
                  if (url.endsWith("/rate_limit")) {
                      var resetTime = System.currentTimeMillis();
                      var metrics = String.format("{\"limit\":5000,\"remaining\":4999,\"reset\":%s,\"used\":1}", resetTime);
                      return new ByteArrayInputStream(
                        String.format("{\"resources\":{\"core\":%1$s,\"search\":%1$s,\"graphql\":%1$s,\"integration_manifest\":%1$s,\"code_scanning_upload\":%1$s},\"rate\":%1$s}",
                                      metrics)
                          .getBytes(StandardCharsets.UTF_8));
                  }

                  return null;
              }
          });

        this.sender = new GitHubCrashReportSender(this.connector);
    }

    @SuppressWarnings("resource")
    @Test
    public void whenSendAndNoData_ThenCreatesIssue() throws Exception {

        var report = new ICrashReportSender.ErrorReport();
        report.setVersion(null);
        report.setDeviceId(null);
        report.setReproSteps(null);
        report.setLastActionId(null);
        this.sender.send(report);

        var order = Mockito.inOrder(this.connector);
        order.verify(this.connector).send(argThat(req ->
                                                    !req.hasBody()
                                                      && req.method().equals("GET")
                                                      && req.url().toString().endsWith("/rate_limit")
        ));
        order.verify(this.connector).send(argThat(req ->
                                                    !req.hasBody()
                                                      && req.method().equals("GET")
                                                      && req.url().toString().endsWith("/repos/" + GitHubCrashReportSender.GitHubRepoSlug)
        ));
        order.verify(this.connector).send(argThat(req -> {

                                                      var issue = deserializeRequestBody(req);
                                                      return req.hasBody()
                                                        && req.method().equals("POST")
                                                        && req.url().toString().endsWith(String.format("/repos/%s/issues", GitHubCrashReportSender.GitHubRepoSlug))
                                                        && issue.title.equals(AutomateBundle.message("general.GitHubCrashReportSender.Title.Title"))
                                                        && issue.body.equals("Version: unknown\r\nDevice: unknown\r\nLastActionId: none\r\nSteps: none\r\nExceptions:\r\n\r\n")
                                                        && issue.labels.contains(GitHubCrashReportSender.GitHubIssueLabel);
                                                  }

        ));
    }

    @SuppressWarnings("resource")
    @Test
    public void whenSendAndAllData_ThenCreatesIssue() throws Exception {

        var exception = new Exception("amessage");
        var report = new ICrashReportSender.ErrorReport();
        report.setVersion("aversion");
        report.setDeviceId("adeviceid");
        report.setReproSteps("areprostep");
        report.setLastActionId("alastactionid");
        report.addException(exception);
        this.sender.send(report);

        var order = Mockito.inOrder(this.connector);
        order.verify(this.connector).send(argThat(req ->
                                                    !req.hasBody()
                                                      && req.method().equals("GET")
                                                      && req.url().toString().endsWith("/rate_limit")
        ));
        order.verify(this.connector).send(argThat(req ->
                                                    !req.hasBody()
                                                      && req.method().equals("GET")
                                                      && req.url().toString().endsWith("/repos/" + GitHubCrashReportSender.GitHubRepoSlug)
        ));
        order.verify(this.connector).send(argThat(req -> {

                                                      var issue = deserializeRequestBody(req);
                                                      var stackTrace = Arrays.stream(exception.getStackTrace()).map(element -> String.format("%s\r\n", element.toString())).collect(Collectors.joining());
                                                      var body = "Version: aversion\r\nDevice: adeviceid\r\nLastActionId: alastactionid\r\nSteps: areprostep\r\nExceptions:\r\nCause: `amessage`\r\n\r\n```\n" +
                                                        stackTrace + "\r\n```\r\n";
                                                      return req.hasBody()
                                                        && req.method().equals("POST")
                                                        && req.url().toString().endsWith(String.format("/repos/%s/issues", GitHubCrashReportSender.GitHubRepoSlug))
                                                        && issue.title.equals(AutomateBundle.message("general.GitHubCrashReportSender.Title.Title"))
                                                        && issue.body.equals(body)
                                                        && issue.labels.contains(GitHubCrashReportSender.GitHubIssueLabel);
                                                  }

        ));
    }

    @SuppressWarnings({"resource", "ConstantConditions"})
    private Issue deserializeRequestBody(GitHubConnectorRequest req) {

        return new Gson().fromJson(new String(Try.safely(() -> req.body().readAllBytes()), StandardCharsets.UTF_8),
                                   Issue.class);
    }

    @SuppressWarnings("unused")
    static class Issue {

        public String owner;
        public String repo;
        public String title;
        public String body;
        public List<String> assignees;
        public String milestone;
        public List<String> labels;
    }
}
