package jezzsantos.automate.plugin.infrastructure.ui.components;

import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutomateTree extends Tree implements DataProvider {

    @Override
    public @Nullable Object getData(@NotNull @NonNls String dataId) {

        if (PlatformCoreDataKeys.SELECTED_ITEM.is(dataId)) {
            return getSelectionPath();
        }

        return null;
    }
}
