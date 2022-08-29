package jezzsantos.automate.plugin.infrastructure.ui.toolwindows;

import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import org.jetbrains.annotations.NotNull;

public class PatternFolderPlaceholderNode {

    @NotNull
    private final PatternElement parent;
    @NotNull
    private final Object child;
    @NotNull
    private final String name;

    public PatternFolderPlaceholderNode(@NotNull PatternElement parent, @NotNull Object child, @NotNull String displayName) {

        this.parent = parent;
        this.name = displayName;
        this.child = child;
    }

    @NotNull
    public PatternElement getParent() {

        return this.parent;
    }

    @NotNull
    public Object getChild() {

        return this.child;
    }

    @Override
    public String toString() {

        return this.name;
    }
}
