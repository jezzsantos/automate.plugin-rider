package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.CliInstallPolicy;
import jezzsantos.automate.plugin.application.services.interfaces.CliExecutableStatus;
import jezzsantos.automate.plugin.application.services.interfaces.INotifier;
import jezzsantos.automate.plugin.application.services.interfaces.NotificationType;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.StringWithDefault;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.infrastructure.ITaskRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.module.ModuleDescriptor;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;

public class AutomateCliUpgraderTests {

    private IAutomateCliRunner cliRunner;
    private INotifier notifier;
    private AutomateCliUpgrader upgrader;
    private ITaskRunner taskRunner;

    @SuppressWarnings("unchecked")
    @BeforeEach
    public void setUp() {

        this.cliRunner = Mockito.mock(IAutomateCliRunner.class);
        this.notifier = Mockito.mock(INotifier.class);
        this.taskRunner = Mockito.mock(ITaskRunner.class);
        Try.safely(() -> Mockito.when(this.taskRunner.runToCompletion(anyString(), any()))
          .thenAnswer(x -> ((Callable<ModuleDescriptor.Version>) x.getArgument(1)).call()));
        this.upgrader = new AutomateCliUpgrader(this.cliRunner, this.notifier, this.taskRunner);
    }

    @Test
    public void whenUpgradeAndNotInstalledAndAutoInstalls_ThenLogsAndNotifySuccess() {

        Mockito.when(this.cliRunner.installLatest(anyString(), anyBoolean()))
          .thenReturn(ModuleDescriptor.Version.parse("99.0.0"));
        var status = new CliExecutableStatus("anexecutablename");

        var result = this.upgrader.upgrade("acurrentdirectory", StringWithDefault.fromValue("anexecutablepath"), "anexecutablename", status, CliInstallPolicy.AUTO_UPGRADE);

        Mockito.verify(this.cliRunner).installLatest("acurrentdirectory", false);
        Mockito.verify(this.notifier).alert(argThat(x -> x == NotificationType.INFO), anyString(),
                                            argThat(x -> x.equals(AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.AutoInstallSucceeds.Message",
                                                                                         "anexecutablename", "99.0.0"))), any());
        assertEquals("99.0.0", result.getVersion());
    }

    @Test
    public void whenUpgradeAndNotInstalledAndInstallFails_ThenLogsAndNotifyError() {

        Mockito.when(this.cliRunner.installLatest(anyString(), anyBoolean()))
          .thenReturn(null);
        var status = new CliExecutableStatus("anexecutablename");

        var result = this.upgrader.upgrade("acurrentdirectory", StringWithDefault.fromValue("anexecutablepath"), "anexecutablename", status, CliInstallPolicy.AUTO_UPGRADE);

        Mockito.verify(this.cliRunner).installLatest("acurrentdirectory", false);
        Mockito.verify(this.notifier).alert(argThat(x -> x == NotificationType.ERROR), anyString(),
                                            argThat(x -> x.equals(AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.InstallFailed.Message",
                                                                                         "anexecutablename"))), any());
        assertEquals(status, result);
    }

    @Test
    public void whenUpgradeAndNotInstalledAndCustomExecutablePath_ThenLogsAndNotifyError() {

        var status = new CliExecutableStatus("anexecutablename");
        var executablePath = StringWithDefault.fromValue("anexecutablepath");
        executablePath.setValue("acustomexecutablepath");

        var result = this.upgrader.upgrade("acurrentdirectory", executablePath, "anexecutablename", status, CliInstallPolicy.AUTO_UPGRADE);

        Mockito.verify(this.cliRunner, never()).installLatest(anyString(), anyBoolean());
        Mockito.verify(this.notifier).alert(argThat(x -> x == NotificationType.ERROR), anyString(),
                                            argThat(x -> x.equals(AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.InstallForbidden.Message",
                                                                                         "anexecutablename", "acustomexecutablepath"))), any());
        assertEquals(status, result);
    }

    @Test
    public void whenUpgradeAndNotInstalledAndCannotAutoInstall_ThenLogsAndNotifyError() {

        var status = new CliExecutableStatus("anexecutablename");

        var result = this.upgrader.upgrade("acurrentdirectory", StringWithDefault.fromValue("anexecutablepath"), "anexecutablename", status, CliInstallPolicy.NONE);

        Mockito.verify(this.cliRunner, never()).installLatest(anyString(), anyBoolean());
        Mockito.verify(this.notifier).alert(argThat(x -> x == NotificationType.ERROR), anyString(),
                                            argThat(x -> x.equals(AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.NotInstalled.Message",
                                                                                         "anexecutablename"))), any());
        assertEquals(status, result);
    }

    @Test
    public void whenUpgradeAndIncompatibleVersionInstalledAndAutoUpgrades_ThenLogsAndNotifySuccess() {

        Mockito.when(this.cliRunner.installLatest(anyString(), anyBoolean()))
          .thenReturn(ModuleDescriptor.Version.parse("99.0.0"));
        var status = new CliExecutableStatus("anexecutablename", "0.0.0");

        var result = this.upgrader.upgrade("acurrentdirectory", StringWithDefault.fromValue("anexecutablepath"), "anexecutablename", status, CliInstallPolicy.AUTO_UPGRADE);

        Mockito.verify(this.cliRunner).installLatest("acurrentdirectory", true);
        Mockito.verify(this.notifier).alert(argThat(x -> x == NotificationType.INFO), anyString(),
                                            argThat(x -> x.equals(AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.AutoUpgradedSucceeds.Message",
                                                                                         "anexecutablename", "0.0.0", "99.0.0"))), any());
        assertEquals("99.0.0", result.getVersion());
    }

    @Test
    public void whenUpgradeAndIncompatibleVersionInstalledAndFailsUpgrade_ThenLogsAndNotifyError() {

        Mockito.when(this.cliRunner.installLatest(anyString(), anyBoolean()))
          .thenReturn(null);
        var status = new CliExecutableStatus("anexecutablename", "0.0.0");

        var result = this.upgrader.upgrade("acurrentdirectory", StringWithDefault.fromValue("anexecutablepath"), "anexecutablename", status, CliInstallPolicy.AUTO_UPGRADE);

        Mockito.verify(this.cliRunner).installLatest("acurrentdirectory", true);
        Mockito.verify(this.notifier).alert(argThat(x -> x == NotificationType.ERROR), anyString(),
                                            argThat(x -> x.equals(AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.UpgradeFailed.Message",
                                                                                         "anexecutablename", "0.0.0", AutomateConstants.MinimumSupportedVersion))), any());
        assertEquals(status, result);
    }

    @Test
    public void whenUpgradeAndIncompatibleVersionInstalledAndCustomExecutablePath_ThenLogsAndNotifyError() {

        var status = new CliExecutableStatus("anexecutablename", "0.0.0");
        var executablePath = StringWithDefault.fromValue("anexecutablepath");
        executablePath.setValue("acustomexecutablepath");

        var result = this.upgrader.upgrade("acurrentdirectory", executablePath, "anexecutablename", status, CliInstallPolicy.AUTO_UPGRADE);

        Mockito.verify(this.cliRunner, never()).installLatest(anyString(), anyBoolean());
        Mockito.verify(this.notifier).alert(argThat(x -> x == NotificationType.ERROR), anyString(),
                                            argThat(x -> x.equals(AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.UpgradeForbidden.Message",
                                                                                         "anexecutablename", "acustomexecutablepath"))), any());
        assertEquals(status, result);
    }

    @Test
    public void whenUpgradeAndIncompatibleVersionInstalledAndCannotAutoUpgrade_ThenLogsAndNotifyError() {

        var status = new CliExecutableStatus("anexecutablename", "0.0.0");

        var result = this.upgrader.upgrade("acurrentdirectory", StringWithDefault.fromValue("anexecutablepath"), "anexecutablename", status, CliInstallPolicy.NONE);

        Mockito.verify(this.cliRunner, never()).installLatest(anyString(), anyBoolean());
        Mockito.verify(this.notifier).alert(argThat(x -> x == NotificationType.ERROR), anyString(),
                                            argThat(x -> x.equals(AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.IncompatibleVersion.Message",
                                                                                         "anexecutablename", "0.0.0", AutomateConstants.MinimumSupportedVersion))), any());
        assertEquals(status, result);
    }

    @Test
    public void whenUpgradeAndCompatibleVersionInstalled_ThenDoesNothing() {

        var status = new CliExecutableStatus("anexecutablename", "100.0.0");

        var result = this.upgrader.upgrade("acurrentdirectory", StringWithDefault.fromValue("anexecutablepath"), "anexecutablename", status, CliInstallPolicy.AUTO_UPGRADE);

        Mockito.verify(this.cliRunner, never()).installLatest(anyString(), anyBoolean());
        Mockito.verify(this.notifier, never()).alert(any(), anyString(), anyString(), any());
        assertEquals(status, result);
    }
}
