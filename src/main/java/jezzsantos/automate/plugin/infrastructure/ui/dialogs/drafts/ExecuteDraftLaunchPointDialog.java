package jezzsantos.automate.plugin.infrastructure.ui.dialogs.drafts;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBList;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.AutomateIcons;
import jezzsantos.automate.plugin.application.interfaces.drafts.LaunchPointExecutionResult;
import jezzsantos.automate.plugin.common.AutomateBundle;
import jezzsantos.automate.plugin.common.IContainer;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.infrastructure.ITaskRunner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.util.function.Supplier;

public class ExecuteDraftLaunchPointDialog extends DialogWrapper {

    private final ExecuteDraftLaunchPointDialogContext context;
    private final ILogFileEditor fileEditor;
    private final Project project;
    private final ITaskRunner taskRunner;
    private LaunchPointExecutionResult executionResult;
    private JPanel contents;
    private JBList<ListItem> logs;
    private JLabel logsDescription;
    private JLabel result;

    public ExecuteDraftLaunchPointDialog(@NotNull Project project, @NotNull ExecuteDraftLaunchPointDialogContext context) {

        this(project, new TemporaryFileViewer(project), IContainer.getTaskRunner(), context);
    }

    @TestOnly
    public ExecuteDraftLaunchPointDialog(@NotNull Project project, @NotNull ILogFileEditor fileEditor, @NotNull ITaskRunner taskRunner, @NotNull ExecuteDraftLaunchPointDialogContext context) {

        super(project);
        this.project = project;
        this.fileEditor = fileEditor;
        this.taskRunner = taskRunner;
        this.context = context;

        init();
        this.setTitle(AutomateBundle.message("dialog.ExecuteDraftLaunchPoint.Title", context.getLaunchPointName()));
        this.logsDescription.setText(AutomateBundle.message("dialog.ExecuteDraftLaunchPoint.Status.Title"));
        setOKButtonText(AutomateBundle.message("dialog.ExecuteDraftLaunchPoint.OKAction.Title"));
        setOKActionEnabled(false);
        setCancelButtonText(AutomateBundle.message("dialog.ExecuteDraftLaunchPoint.CancelAction.Title"));
        this.logs.setCellRenderer(new ColoredListCellRenderer<>() {
            @Override
            protected void customizeCellRenderer(@NotNull JList<? extends ListItem> list, ListItem value, int index, boolean selected, boolean hasFocus) {

                switch (value.getType()) {

                    case SUCCEEDED -> {
                        setIcon(AutomateIcons.StatusSuccess);
                        append(value.getMessage(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.GREEN));
                    }
                    case WARNING -> {
                        setIcon(value.isValidationError()
                                  ? AutomateIcons.StatusWarning
                                  : AutomateIcons.StatusInformation);
                        append(value.getMessage(), value.isValidationError()
                          ? new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.ORANGE)
                          : new SimpleTextAttributes(SimpleTextAttributes.STYLE_ITALIC, JBColor.GRAY));
                    }
                    case FAILED -> {
                        setIcon(AutomateIcons.StatusFailed);
                        append(value.getMessage(), new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.RED));
                    }
                }
            }
        });

        initResults();
    }

    public ExecuteDraftLaunchPointDialogContext getContext() {return this.context;}

    public void initResults() {

        try {
            this.executionResult = this.taskRunner.runModal(this.project, AutomateBundle.message("general.RunLaunchPoint.Title"), () -> this.context.getExecutor().get());
        } catch (Exception ex) {
            this.executionResult = LaunchPointExecutionResult.failure(ex.getMessage());
        }

        var isSuccess = this.executionResult.isSuccess();
        var hasValidationErrors = this.executionResult.hasValidationErrors();
        var listModel = new DefaultListModel<ListItem>();
        if (hasValidationErrors) {

            this.executionResult.getValidationErrors().forEach(validationError -> listModel.addElement(
              new ListItem(AutomateConstants.CommandExecutionLogItemType.WARNING, String.format("%s: %s", validationError.getPath(), validationError.getMessage()), true)));
        }
        else {
            this.executionResult.getExecutionItems().forEach(item -> listModel.addElement(new ListItem(item.getType(), item.getMessage(), false)));
        }
        this.logs.setModel(listModel);
        this.result.setIcon(isSuccess
                              ? AutomateIcons.StatusSuccess
                              : AutomateIcons.StatusFailed);
        this.result.setText(isSuccess
                              ? AutomateBundle.message("dialog.ExecuteDraftLaunchPoint.Success.Message")
                              : hasValidationErrors
                                ? AutomateBundle.message("dialog.ExecuteDraftLaunchPoint.Aborted.Message")
                                : AutomateBundle.message("dialog.ExecuteDraftLaunchPoint.Failed.Message"));
        setOKActionEnabled(!isSuccess);
        saveMessagesToOutputFile();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {

        return this.contents;
    }

    @Override
    protected void doOKAction() {

        super.doOKAction();
        this.fileEditor.openFile();
    }

    private void saveMessagesToOutputFile() {

        if (this.executionResult.isSuccess()) {
            return;
        }

        var tempFile = Try.safely(this.fileEditor::createFile);
        if (tempFile == null) {
            return;
        }

        if (this.executionResult.hasValidationErrors()) {
            tempFile.appendLine(AutomateBundle.message("dialog.ExecuteDraftLaunchPoint.LogFile.ValidationErrors.Message"));
            tempFile.appendLine();
            this.executionResult.getValidationErrors()
              .forEach(validationError -> tempFile.appendLine(String.format("\t- %s: %s", validationError.getPath(), validationError.getMessage())));
        }
        else {
            tempFile.appendLine(AutomateBundle.message("dialog.ExecuteDraftLaunchPoint.LogFile.ExecutionErrors.Message"));
            tempFile.appendLine();
            this.executionResult.getExecutionItems().forEach(item -> tempFile.appendLine(String.format("\t- %s: %s", item.getType(), item.getMessage())));
        }
    }

    interface TemporaryFile {

        void appendLine(@NotNull String text);

        void appendLine();
    }

    interface ILogFileEditor {

        void openFile();

        TemporaryFile createFile() throws Exception;
    }

    static class TemporaryFileViewer implements ILogFileEditor {

        private final Project project;
        private File outputLog;

        public TemporaryFileViewer(@NotNull Project project) {this.project = project;}

        @Override
        public void openFile() {

            if (this.outputLog == null) {
                return;
            }

            var path = LocalFileSystem.getInstance().findFileByIoFile(this.outputLog);
            if (path != null) {
                FileEditorManager.getInstance(this.project).openFile(path, false);
            }
        }

        @Override
        public TemporaryFile createFile() throws Exception {

            this.outputLog = File.createTempFile(AutomateBundle.message("settings.Title"), ".log");
            return new SystemTemporaryFile(this.outputLog);
        }
    }

    static class SystemTemporaryFile implements TemporaryFile {

        private final File file;

        public SystemTemporaryFile(@NotNull File file) {

            this.file = file;
        }

        @Override
        public void appendLine(@NotNull String text) {

            try {
                var writer = new FileWriter(this.file);

                writer.write(text + System.lineSeparator());
                writer.close();
            } catch (Exception ignored) {
            }
        }

        @Override
        public void appendLine() {

            appendLine("");
        }
    }

    public static class ExecuteDraftLaunchPointDialogContext {

        private final String launchPointName;
        private final Supplier<LaunchPointExecutionResult> action;

        public ExecuteDraftLaunchPointDialogContext(@NotNull String launchPointName, @NotNull Supplier<LaunchPointExecutionResult> action) {

            this.launchPointName = launchPointName;
            this.action = action;
        }

        public String getLaunchPointName() {return this.launchPointName;}

        @NotNull
        public Supplier<LaunchPointExecutionResult> getExecutor() {

            return this.action;
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static class ListItem {

        private final String message;
        private final AutomateConstants.CommandExecutionLogItemType type;
        private final boolean isValidationError;

        public ListItem(@NotNull AutomateConstants.CommandExecutionLogItemType type, @NotNull String message, boolean isValidationError) {

            this.type = type;
            this.message = message;
            this.isValidationError = isValidationError;
        }

        public String getMessage() {return this.message;}

        public AutomateConstants.CommandExecutionLogItemType getType() {return this.type;}

        public boolean isValidationError() {return this.isValidationError;}
    }
}
