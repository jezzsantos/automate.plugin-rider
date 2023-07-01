package jezzsantos.automate.plugin.infrastructure.ui.dialogs.patterns;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.patterns.Automation;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class EditPatternCommandLaunchPointDialog extends DialogWrapper {

    private final EditPatternCommandLaunchPointDialogContext context;
    private JPanel contents;
    private JTextField name;
    private JLabel nameTitle;
    private JBList<Automation> localAutomation;
    private JLabel localAutomationTitle;

    public EditPatternCommandLaunchPointDialog(@NotNull Project project, @NotNull EditPatternCommandLaunchPointDialogContext context) {

        super(project);

        this.context = context;

        this.init();
        this.setTitle(context.getIsNew()
                        ? AutomateBundle.message("dialog.EditPatternCommandLaunchPoint.NewLaunchPoint.Title")
                        : AutomateBundle.message("dialog.EditPatternCommandLaunchPoint.UpdateLaunchPoint.Title"));
        this.nameTitle.setText(AutomateBundle.message("dialog.EditPatternCommandLaunchPoint.Name.Title"));
        this.name.setText(this.context.getName());
        this.localAutomationTitle.setText(AutomateBundle.message("dialog.EditPatternCommandLaunchPoint.LocalAutomation.Title"));
        this.localAutomation.getEmptyText().setText(AutomateBundle.message("dialog.EditPatternCommandLaunchPoint.EmptyAutomation.Message"));
        this.localAutomation.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        var model = new CollectionListModel<Automation>();
        this.context.getAutomation().forEach(model::add);
        this.localAutomation.setModel(model);
        if (context.getIsNew()) {
            this.localAutomation.setSelectedIndices(IntStream.range(0, model.getSize()).toArray());
        }
        else {
            var commands = this.context.getAddIdentifiers();
            var selectedIndexes = new ArrayList<Integer>();
            AtomicInteger counter = new AtomicInteger();
            model.toList().forEach(item -> {
                if (commands.contains(item.getId())) {
                    selectedIndexes.add(counter.get());
                }
                counter.getAndIncrement();
            });
            this.localAutomation.setSelectedIndices(selectedIndexes.stream().mapToInt(x -> x).toArray());
        }
        setOKButtonText(context.getIsNew()
                          ? AutomateBundle.message("dialog.EditPatternCommandLaunchPoint.NewLaunchPoint.Confirm.Title")
                          : AutomateBundle.message("dialog.EditPatternCommandLaunchPoint.UpdateLaunchPoint.Confirm.Title"));
        this.contents.invalidate();
    }

    @TestOnly
    public static @Nullable ValidationInfo doValidate(@NotNull EditPatternCommandLaunchPointDialog.EditPatternCommandLaunchPointDialogContext context,
                                                      @NotNull String name, List<Automation> commands) {

        if (!context.isValidName(name)) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternCommandLaunchPoint.NameValidation.NotMatch.Message"));
        }
        if (!context.isAvailableName(name)) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternCommandLaunchPoint.NameValidation.Exists.Message"));
        }

        if (commands.isEmpty()) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternCommandLaunchPoint.AddIdentifiersValidation.None.Message"));
        }

        return null;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {

        return doValidate(this.context, this.name.getText(), this.localAutomation.getSelectedValuesList());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {

        return this.contents;
    }

    @Override
    protected void doOKAction() {

        super.doOKAction();
        this.context.setName(this.name.getText());
        var selected = this.localAutomation.getSelectedValuesList();
        var unselected = new ArrayList<>(this.context.getAutomation());
        unselected.removeAll(selected);
        this.context.setAddIdentifiers(selected);
        this.context.setRemoveIdentifiers(unselected);
    }

    public EditPatternCommandLaunchPointDialogContext getContext() {

        return this.context;
    }

    public static class EditPatternCommandLaunchPointDialogContext {

        private final boolean isNew;
        private final List<Automation> automations;
        private final String originalName;
        private final String from;
        private List<String> removeIdentifiers;
        private List<String> addIdentifiers;
        private String name;

        public EditPatternCommandLaunchPointDialogContext(@NotNull List<Automation> automations) {

            this.isNew = true;
            this.automations = getLaunchableAutomations(automations);
            this.name = guessNextName(automations.stream().map(Automation::getName).toList());
            this.originalName = this.name;
            this.from = "";
            this.addIdentifiers = new ArrayList<>();
            this.removeIdentifiers = new ArrayList<>();
        }

        public EditPatternCommandLaunchPointDialogContext(@NotNull Automation automation, @NotNull List<Automation> automations) {

            this.isNew = false;
            this.automations = getLaunchableAutomations(automations);
            this.name = automation.getName();
            this.originalName = automation.getName();
            this.from = "";
            this.addIdentifiers = automation.getCommandIdentifiers();
            this.removeIdentifiers = new ArrayList<>();
        }

        public boolean getIsNew() {return this.isNew;}

        @NotNull
        public String getId() {

            return this.isNew
              ? this.name
              : this.originalName;
        }

        @NotNull
        public String getName() {

            return this.name;
        }

        public void setName(@NotNull String name) {

            this.name = name;
        }

        public boolean isAvailableName(@NotNull String name) {

            var existingNames = this.automations.stream()
              .map(Automation::getName);
            var reservedNames = AutomateConstants.ReservedAutomationNames.stream();
            var illegalNames = Stream.concat(existingNames, reservedNames);

            if (this.isNew) {
                return illegalNames
                  .noneMatch(in -> in.equalsIgnoreCase(name));
            }
            else {
                return illegalNames
                  .filter(in -> !in.equalsIgnoreCase(this.originalName))
                  .noneMatch(in -> in.equalsIgnoreCase(name));
            }
        }

        public boolean isValidName(@Nullable String name) {

            if (name == null || name.isEmpty()) {
                return false;
            }
            return name.matches(AutomateConstants.AutomationNameRegex);
        }

        @Nullable
        public String getFrom() {

            return this.from;
        }

        public List<String> getAddIdentifiers() {

            return this.addIdentifiers;
        }

        public void setAddIdentifiers(@NotNull List<Automation> automation) {

            this.addIdentifiers = automation.stream().map(Automation::getId).collect(Collectors.toList());
        }

        public List<String> getRemoveIdentifiers() {

            return this.removeIdentifiers;
        }

        public void setRemoveIdentifiers(@NotNull List<Automation> automation) {

            this.removeIdentifiers = automation.stream().map(Automation::getId).collect(Collectors.toList());
        }

        public List<Automation> getAutomation() {

            return this.automations;
        }

        private List<Automation> getLaunchableAutomations(@NotNull List<Automation> automations) {

            return automations.stream().filter(a -> a.getType() != AutomateConstants.AutomationType.COMMAND_LAUNCH_POINT).collect(Collectors.toList());
        }

        private String guessNextName(@NotNull List<String> names) {

            var counter = 1;
            var name = AutomateBundle.message("dialog.EditPatternCommandLaunchPoint.LaunchPointName.Format", names.size() + 1);
            while (names.stream().anyMatch(name::equalsIgnoreCase)) {
                counter += 1;
                name = AutomateBundle.message("dialog.EditPatternCommandLaunchPoint.LaunchPointName.Format", names.size() + counter);
            }

            return name;
        }
    }
}
