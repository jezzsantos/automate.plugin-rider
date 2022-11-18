package jezzsantos.automate.plugin.infrastructure.services.cli;

import groovy.lang.Tuple2;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.CliInstallPolicy;
import jezzsantos.automate.plugin.application.services.interfaces.CliExecutableStatus;
import jezzsantos.automate.plugin.application.services.interfaces.INotifier;
import jezzsantos.automate.plugin.application.services.interfaces.NotificationType;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.IContainer;
import jezzsantos.automate.plugin.common.StringWithDefault;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.common.recording.LogLevel;
import jezzsantos.automate.plugin.infrastructure.ITaskRunner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.lang.module.ModuleDescriptor;
import java.util.Map;
import java.util.Objects;

public class AutomateCliUpgrader implements ICliUpgrader {

    private final IRecorder recorder;
    private final IAutomateCliRunner cliRunner;
    private final INotifier notifier;
    private final ITaskRunner taskRunner;

    public AutomateCliUpgrader(@NotNull IRecorder recorder, @NotNull IAutomateCliRunner cliRunner, @NotNull INotifier notifier) {

        this(recorder, cliRunner, notifier, IContainer.getTaskRunner());
    }

    @TestOnly
    public AutomateCliUpgrader(@NotNull IRecorder recorder, @NotNull IAutomateCliRunner cliRunner, @NotNull INotifier notifier, @NotNull ITaskRunner taskRunner) {

        this.recorder = recorder;
        this.cliRunner = cliRunner;
        this.notifier = notifier;
        this.taskRunner = taskRunner;
    }

    @Override
    public @NotNull CliExecutableStatus upgrade(@NotNull StringWithDefault executablePath, @NotNull String executableName, @NotNull CliExecutableStatus executableStatus, @NotNull CliInstallPolicy installPolicy) {

        var compatibility = executableStatus.getCompatibility();
        switch (compatibility) {
            case UNKNOWN -> {
                var neededVersion = executableStatus.getMinCompatibleVersion();
                if (installPolicy == CliInstallPolicy.NONE) {
                    this.recorder.trace(LogLevel.INFORMATION,
                                        AutomateBundle.message("trace.AutomateCliUpgrader.Upgrade.CompatibilityUnknown.Disabled", executableName, neededVersion));
                    this.recorder.measureEvent("autoupgrader.cli-missing.upgrade-disabled", Map.of(
                      "Current Version", ""
                    ));
                    alertInstallerError(
                      AutomateBundle.message("general.AutomateCliUpgrader.Upgrade.CompatibilityUnknown.Disabled.Message", executableName), true);
                }
                else {
                    if (installPolicy == CliInstallPolicy.AUTO_UPGRADE) {
                        if (!executablePath.isCustomized()) {
                            var installResult = tryInstallLatestCli(false);
                            var latestVersion = installResult.getV2();
                            if (latestVersion == null) {
                                var exception = Objects.requireNonNullElse(installResult.getV1(), "").toString();
                                this.recorder.trace(LogLevel.ERROR,
                                                    AutomateBundle.message("trace.AutomateCliUpgrader.Upgrade.CompatibilityUnknown.AutoUpgrade.Failed", executableName, exception,
                                                                           neededVersion));
                                this.recorder.measureEvent("autoupgrader.cli-missing.upgrade-failed", Map.of(
                                  "Current Version", "",
                                  "Exception", exception
                                ));
                                alertInstallerError(AutomateBundle.message("general.AutomateCliUpgrader.Upgrade.CompatibilityUnknown.AutoUpgrade.Failed", executableName),
                                                    true);
                            }
                            else {
                                executableStatus = new CliExecutableStatus(executableName, latestVersion.toString());
                                this.recorder.trace(LogLevel.INFORMATION,
                                                    AutomateBundle.message("trace.AutomateCliUpgrader.Upgrade.CompatibilityUnknown.AutoUpgrade.Success", executableName,
                                                                           neededVersion));
                                this.recorder.measureEvent("autoupgrader.cli-missing.upgrade-success", Map.of(
                                  "Current Version", "",
                                  "Required Version", latestVersion.toString()));
                                alertInstallerSuccess(
                                  AutomateBundle.message("general.AutomateCliUpgrader.Upgrade.CompatibilityUnknown.AutoUpgrade.Success.Message", executableName,
                                                         latestVersion.toString()));
                            }
                        }
                        else {
                            this.recorder.trace(LogLevel.WARNING,
                                                AutomateBundle.message("trace.AutomateCliUpgrader.Upgrade.CompatibilityUnknown.AutoUpgrade.UnSupported", executableName));
                            this.recorder.measureEvent("autoupgrader.cli-missing.upgrade-forbidden", Map.of(
                              "Current Version", ""
                            ));
                            alertInstallerError(AutomateBundle.message("general.AutomateCliUpgrader.Upgrade.CompatibilityUnknown.AutoUpgrade.UnSupported.Message", executableName,
                                                                       executablePath.getValueOrDefault()), true);
                        }
                    }
                }
            }
            case INCOMPATIBLE -> {
                var currentVersion = executableStatus.getVersion();
                var neededVersion = executableStatus.getMinCompatibleVersion();
                if (installPolicy == CliInstallPolicy.NONE) {
                    this.recorder.trace(LogLevel.INFORMATION,
                                        AutomateBundle.message("trace.AutomateCliUpgrader.Upgrade.InCompatible.AutoUpgrade.Disabled", executableName, currentVersion,
                                                               neededVersion));
                    this.recorder.measureEvent("autoupgrader.cli-expired.upgrade-disabled", Map.of(
                      "Current Version", currentVersion,
                      "Required Version", neededVersion));
                    alertInstallerError(AutomateBundle.message("general.AutomateCliUpgrader.Upgrade.InCompatible.AutoUpgrade.Disabled.Message", executableName, currentVersion,
                                                               neededVersion), true);
                }
                else {
                    if (installPolicy == CliInstallPolicy.AUTO_UPGRADE) {
                        if (!executablePath.isCustomized()) {
                            var installResult = tryInstallLatestCli(true);
                            var latestVersion = installResult.getV2();
                            if (latestVersion == null) {
                                var exception = Objects.requireNonNullElse(installResult.getV1(), "").toString();
                                this.recorder.trace(LogLevel.ERROR,
                                                    AutomateBundle.message("trace.AutomateCliUpgrader.Upgrade.InCompatible.AutoUpgrade.Failed.Message", executableName, exception,
                                                                           currentVersion, neededVersion));
                                this.recorder.measureEvent("autoupgrader.cli-expired.upgrade-failed", Map.of(
                                  "Current Version", currentVersion,
                                  "Required Version", neededVersion,
                                  "Exception", exception
                                ));
                                alertInstallerError(
                                  AutomateBundle.message("general.AutomateCliUpgrader.Upgrade.InCompatible.AutoUpgrade.Failed.Message", executableName, currentVersion,
                                                         neededVersion), true);
                            }
                            else {
                                executableStatus = new CliExecutableStatus(executableName, latestVersion.toString());
                                this.recorder.trace(LogLevel.INFORMATION,
                                                    AutomateBundle.message("trace.AutomateCliUpgrader.Upgrade.InCompatible.AutoUpgrade.Success", executableName, currentVersion,
                                                                           neededVersion));
                                this.recorder.measureEvent("autoupgrader.cli-expired.upgrade-succeed", Map.of(
                                  "Current Version", currentVersion,
                                  "Required Version", latestVersion.toString()));
                                alertInstallerSuccess(
                                  AutomateBundle.message("general.AutomateCliUpgrader.Upgrade.InCompatible.AutoUpgrade.Success.Message", executableName, currentVersion,
                                                         latestVersion)
                                );
                            }
                        }
                        else {
                            this.recorder.measureEvent("autoupgrader.cli-expired.upgrade-forbidden", null);
                            this.recorder.trace(LogLevel.WARNING,
                                                AutomateBundle.message("trace.AutomateCliUpgrader.Upgrade.InCompatible.AutoUpgrade.UnSupported", executableName, currentVersion,
                                                                       neededVersion));
                            alertInstallerError(AutomateBundle.message("general.AutomateCliUpgrader.Upgrade.InCompatible.AutoUpgrade.UnSupported.Message", executableName,
                                                                       executablePath.getValueOrDefault()), true);
                        }
                    }
                }
            }
            case COMPATIBLE ->
              this.recorder.trace(LogLevel.INFORMATION, AutomateBundle.message("trace.AutomateCliUpgrader.Upgrade.Compatible", executableName, executableStatus.getVersion()));
        }

        return executableStatus;
    }

    private Tuple2<Throwable, ModuleDescriptor.Version> tryInstallLatestCli(boolean uninstall) {

        try {
            return new Tuple2<>(null, this.taskRunner.runModal(AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.Task.Title",
                                                                                      AutomateConstants.ExecutableName),
                                                               () -> AutomateCliUpgrader.this.cliRunner.installLatest(uninstall)));
        } catch (Exception ex) {
            alertInstallerError(AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.Failed.Message", AutomateConstants.ExecutableName, ex.getMessage()), false);
            return new Tuple2<>(ex, null);
        }
    }

    private void alertInstallerError(@NotNull String message, boolean includeHelpLink) {

        alertInternal(NotificationType.ERROR, message, includeHelpLink);
    }

    private void alertInstallerSuccess(@NotNull String message) {

        alertInternal(NotificationType.INFO, message, false);
    }

    private void alertInternal(@NotNull NotificationType type, @NotNull String message, boolean includeHelpLink) {

        this.notifier.alert(type, AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.Alert.Title", AutomateConstants.ExecutableName), message,
                            includeHelpLink
                              ? new INotifier.LinkDescriptor(AutomateConstants.InstallationInstructionsUrl,
                                                             AutomateBundle.message("general.AutomateCliUpgrader.MoreInfoLink.Title"))
                              : null);
    }
}
