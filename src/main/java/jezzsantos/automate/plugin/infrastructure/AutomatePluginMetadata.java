package jezzsantos.automate.plugin.infrastructure;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.PermanentInstallationID;
import com.intellij.openapi.extensions.PluginId;
import jezzsantos.automate.plugin.common.IPluginMetadata;
import org.jetbrains.annotations.NotNull;

public class AutomatePluginMetadata implements IPluginMetadata {

    private static final String pluginInId = "automate";
    private static String installationId;
    private IdeaPluginDescriptor plugin;

    @NotNull
    @Override
    public String getInstallationId() {

        if (installationId == null) {
            installationId = String.format("jbrdid_%s", PermanentInstallationID.get().replace("-", ""));
        }

        return installationId;
    }

    @NotNull
    @Override
    public String getRuntimeVersion() {

        ensurePlugin();
        return this.plugin.getVersion();
    }

    @NotNull
    @Override
    public String getProductName() {

        ensurePlugin();
        return this.plugin.getName();
    }

    private void ensurePlugin() {

        if (this.plugin == null) {
            this.plugin = PluginManager.getInstance().findEnabledPlugin(PluginId.getId(pluginInId));
        }
    }
}
