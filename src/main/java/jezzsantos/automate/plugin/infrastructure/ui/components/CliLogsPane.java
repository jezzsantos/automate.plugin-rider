package jezzsantos.automate.plugin.infrastructure.ui.components;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.project.Project;
import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntry;
import jezzsantos.automate.plugin.application.interfaces.CliLogEntryType;
import jezzsantos.automate.plugin.common.Try;
import jezzsantos.automate.plugin.infrastructure.services.cli.AutomateCliRunner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.beans.PropertyChangeListener;
import java.util.List;

public class CliLogsPane extends JTextPane implements Disposable {

    @NotNull
    private final IAutomateApplication application;

    public CliLogsPane(@NotNull Project project) {

        this(IAutomateApplication.getInstance(project));
    }

    @TestOnly
    public CliLogsPane(@NotNull IAutomateApplication application) {

        super();
        this.application = application;
        init();
    }

    @Override
    public void dispose() {

        this.application.removePropertyListener(cliLogUpdatedListener());
    }

    private void init() {

        this.application.addPropertyListener(cliLogUpdatedListener());

        var entries = this.application.getCliLogEntries();
        for (var entry : entries) {
            displayLogEntry(entry);
        }
        var scheme = EditorColorsManager.getInstance().getGlobalScheme();
        this.setFont(scheme.getFont(EditorFontType.CONSOLE_PLAIN));
        this.setBackground(scheme.getColor(ConsoleViewContentType.CONSOLE_BACKGROUND_KEY));
    }

    @SuppressWarnings("unchecked")
    @NotNull
    private PropertyChangeListener cliLogUpdatedListener() {

        return event -> {
            if (event.getPropertyName().equalsIgnoreCase(AutomateCliRunner.PropertyChanged_Logs)) {
                var latestEntries = ((List<CliLogEntry>) event.getNewValue());
                for (var entry : latestEntries) {
                    displayLogEntry(entry);
                }
            }
        };
    }

    private void displayLogEntry(@NotNull CliLogEntry entry) {

        var scheme = EditorColorsManager.getInstance().getGlobalScheme();
        var infoColor = scheme.getAttributes(ConsoleViewContentType.LOG_INFO_OUTPUT_KEY).getForegroundColor();
        var errorColor = scheme.getAttributes(ConsoleViewContentType.LOG_ERROR_OUTPUT_KEY).getForegroundColor();

        var attributes = new SimpleAttributeSet();
        if (entry.Type != CliLogEntryType.NORMAL) {
            StyleConstants.setForeground(attributes, entry.Type == CliLogEntryType.ERROR
              ? errorColor
              : infoColor);
            StyleConstants.setBold(attributes, entry.Type == CliLogEntryType.ERROR);
        }
        var text = String.format("%s%s", entry.Text, System.lineSeparator());
        var document = this.getDocument();
        Try.safely(() -> document.insertString(document.getLength(), text, attributes));
    }
}
