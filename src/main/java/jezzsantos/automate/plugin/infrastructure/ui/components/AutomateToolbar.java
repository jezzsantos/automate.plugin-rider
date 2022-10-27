package jezzsantos.automate.plugin.infrastructure.ui.components;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.impl.ActionToolbarImpl;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.Topic;
import jezzsantos.automate.plugin.infrastructure.ui.actions.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

interface StateChangedListener {

    Topic<StateChangedListener> TOPIC = new Topic<>(StateChangedListener.class, Topic.BroadcastDirection.TO_CHILDREN);

    void settingsChanged();
}

public class AutomateToolbar extends ActionToolbarImpl {

    @NotNull
    private final MessageBus messageBus;
    @NotNull
    private final AutomateNotifier notifier;

    public AutomateToolbar(@NotNull Project project, @NotNull AutomateTree tree, @NotNull String place, boolean horizontal) {

        this(project.getMessageBus(), tree, place, horizontal);
    }

    @TestOnly
    public AutomateToolbar(@NotNull MessageBus messageBus, @NotNull AutomateNotifier notifier, @NotNull String place, boolean horizontal) {

        super(place, createActions(messageBus), horizontal);
        this.messageBus = messageBus;
        this.notifier = notifier;
        setupActionNotifications();
    }

    @NotNull
    private static DefaultActionGroup createActions(MessageBus messageBus) {

        final Runnable update = notifyUpdated(messageBus);

        final var actions = new DefaultActionGroup();
        actions.add(new TogglePatternEditingModeAction(update));
        actions.add(new ToggleDraftEditingModeAction(update));
        actions.addSeparator();
        actions.add(new PatternsListToolbarAction(update));
        actions.add(new AddPatternAction(update));
        actions.add(new InstallToolkitToolbarAction(update));
        actions.add(new DraftsListToolbarAction(update));
        actions.add(new AddDraftAction(update));
        actions.add(new RefreshAllAction(update));
        actions.addSeparator();
        actions.add(new ShowSettingsToolbarAction());
        actions.add(new AdvancedOptionsToolbarActionGroup(update));
        actions.addSeparator();
        actions.add(new ToggleAuthoringModeToolbarAction(update));

        return actions;
    }

    @NotNull
    private static Runnable notifyUpdated(MessageBus messageBus) {

        return () -> messageBus.syncPublisher(StateChangedListener.TOPIC).settingsChanged();
    }

    private void setupActionNotifications() {

        var connection = this.messageBus.connect();
        connection.subscribe(StateChangedListener.TOPIC, (StateChangedListener) AutomateToolbar.this.notifier::update);
    }
}
