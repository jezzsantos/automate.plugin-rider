package jezzsantos.automate.plugin.infrastructure.services.cli;

import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.CliInstallPolicy;
import jezzsantos.automate.plugin.application.services.interfaces.CliExecutableStatus;
import jezzsantos.automate.plugin.application.services.interfaces.INotifier;
import jezzsantos.automate.plugin.application.services.interfaces.NotificationType;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.StringWithDefault;
import jezzsantos.automate.plugin.infrastructure.ITaskRunner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.lang.module.ModuleDescriptor;

public class AutomateCliUpgrader implements ICliUpgrader {

    private final IAutomateCliRunner cliRunner;
    private final INotifier notifier;
    private final ITaskRunner taskRunner;

    public AutomateCliUpgrader(@NotNull IAutomateCliRunner cliRunner, @NotNull INotifier notifier) {

        this(cliRunner, notifier, ITaskRunner.getInstance());
    }

    @TestOnly
    public AutomateCliUpgrader(@NotNull IAutomateCliRunner cliRunner, @NotNull INotifier notifier, @NotNull ITaskRunner taskRunner) {

        this.cliRunner = cliRunner;
        this.notifier = notifier;
        this.taskRunner = taskRunner;
    }

    @Override
    public @NotNull CliExecutableStatus upgrade(@NotNull String currentDirectory, @NotNull StringWithDefault executablePath, @NotNull String executableName, @NotNull CliExecutableStatus executableStatus, @NotNull CliInstallPolicy installPolicy) {

        switch (executableStatus.getCompatibility()) {
            case UNKNOWN -> {
                if (installPolicy == CliInstallPolicy.NONE) {
                    alertInstallerError(
                      AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.NotInstalled.Message", executableName), true);
                }
                else {
                    if (installPolicy == CliInstallPolicy.AUTO_UPGRADE) {
                        if (!executablePath.isCustomized()) {
                            var latestVersion = tryInstallLatestCli(currentDirectory, false);
                            if (latestVersion == null) {
                                alertInstallerError(AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.InstallFailed.Message", executableName), true);
                            }
                            else {
                                executableStatus = new CliExecutableStatus(executableName, latestVersion.toString());
                                alertInstallerSuccess(
                                  AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.AutoInstallSucceeds.Message", executableName, latestVersion));
                            }
                        }
                        else {
                            alertInstallerError(AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.InstallForbidden.Message", executableName,
                                                                       executablePath.getValueOrDefault()), true);
                        }
                    }
                }
            }
            case INCOMPATIBLE -> {
                var currentVersion = executableStatus.getVersion();
                var neededVersion = executableStatus.getMinCompatibleVersion();
                if (installPolicy == CliInstallPolicy.NONE) {
                    alertInstallerError(AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.IncompatibleVersion.Message", executableName, currentVersion,
                                                               neededVersion), true);
                }
                else {
                    if (installPolicy == CliInstallPolicy.AUTO_UPGRADE) {
                        if (!executablePath.isCustomized()) {
                            var latestVersion = tryInstallLatestCli(currentDirectory, true);
                            if (latestVersion == null) {
                                alertInstallerError(
                                  AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.UpgradeFailed.Message", executableName, currentVersion, neededVersion), true);
                            }
                            else {
                                executableStatus = new CliExecutableStatus(executableName, latestVersion.toString());
                                alertInstallerSuccess(
                                  AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.AutoUpgradedSucceeds.Message", executableName, currentVersion, latestVersion)
                                );
                            }
                        }
                        else {
                            alertInstallerError(AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.UpgradeForbidden.Message", executableName,
                                                                       executablePath.getValueOrDefault()), true);
                        }
                    }
                }
            }
            case COMPATIBLE -> {
            }
        }

        return executableStatus;
    }

    @Nullable
    private ModuleDescriptor.Version tryInstallLatestCli(@NotNull String currentDirectory, boolean uninstall) {

        try {
            return this.taskRunner.runModal(AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.Task.Title",
                                                                   AutomateConstants.ExecutableName),
                                            () -> AutomateCliUpgrader.this.cliRunner.installLatest(currentDirectory, uninstall));
        } catch (Exception ex) {
            alertInstallerError(AutomateBundle.message("general.AutomateCliUpgrader.CliInstall.Failed.Message", AutomateConstants.ExecutableName, ex.getMessage()), false);
            return null;
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
