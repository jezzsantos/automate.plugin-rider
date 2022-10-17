package jezzsantos.automate.plugin.application.services.interfaces;

import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.infrastructure.ui.IntelliJNotifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public interface INotifier {

    static INotifier getInstance() {

        return new IntelliJNotifier();
    }

    void alert(@NotNull NotificationType type, @NotNull String title, @NotNull String message, @Nullable LinkDescriptor link);

    void alert(@NotNull NotificationType type, @Nullable Project project, @NotNull String title, @NotNull String message, @Nullable LinkDescriptor link);

    void alert(@NotNull NotificationType type, @NotNull String title, @NotNull String message, @Nullable LinkDescriptor link, @NotNull Runnable andThen);

    void alert(@NotNull NotificationType type, @Nullable Project project, @NotNull String title, @NotNull String message, @Nullable LinkDescriptor link, @NotNull Runnable andThen);

    @SuppressWarnings("ClassCanBeRecord")
    class LinkDescriptor {

        private final String url;
        private final String description;

        public LinkDescriptor(@NotNull String url, @NotNull String description) {

            this.url = url;
            this.description = description;
        }

        @NotNull
        public String getUrl() {return this.url;}

        @NotNull
        public String getDescription() {return this.description;}
    }
}
