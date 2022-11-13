package jezzsantos.automate.plugin.common;

import jezzsantos.automate.plugin.application.services.interfaces.INotifier;
import jezzsantos.automate.plugin.common.recording.IRecorder;
import jezzsantos.automate.plugin.infrastructure.AutomatePluginMetadata;
import jezzsantos.automate.plugin.infrastructure.IOsPlatform;
import jezzsantos.automate.plugin.infrastructure.ITaskRunner;
import jezzsantos.automate.plugin.infrastructure.OsPlatform;
import jezzsantos.automate.plugin.infrastructure.ui.IntelliJNotifier;
import jezzsantos.automate.plugin.infrastructure.ui.IntelliJTaskRunner;

/**
 * This container is used for any dependencies that are generally used around the codebase, but that are not registered in the IntelliJ Application or Project scope
 */
public interface IContainer {

    static ITaskRunner getTaskRunner() {

        return SingletonContainer.getTaskRunner();
    }

    static INotifier getNotifier() {

        return SingletonContainer.getNotifier();
    }

    static IOsPlatform getOsPlatform() {

        return SingletonContainer.getOsPlatform();
    }

    static IPluginMetadata getPluginMetadata() {

        return SingletonContainer.getPluginMetadata();
    }
}

class SingletonContainer {

    private static ITaskRunner taskRunner;
    private static INotifier notifier;
    private static IOsPlatform osPlatform;
    private static IPluginMetadata metadata;

    public static ITaskRunner getTaskRunner() {

        if (taskRunner == null) {
            taskRunner = new IntelliJTaskRunner();
        }

        return taskRunner;
    }

    public static INotifier getNotifier() {

        if (notifier == null) {
            notifier = new IntelliJNotifier();
        }

        return notifier;
    }

    public static IOsPlatform getOsPlatform() {

        if (osPlatform == null) {
            osPlatform = new OsPlatform(IRecorder.getInstance());
        }

        return osPlatform;
    }

    public static IPluginMetadata getPluginMetadata() {

        if (metadata == null) {
            metadata = new AutomatePluginMetadata();
        }

        return metadata;
    }
}
