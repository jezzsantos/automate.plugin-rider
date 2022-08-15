package jezzsantos.automate.plugin.application.interfaces;

import org.jetbrains.annotations.NotNull;

public class CliLogEntry {

    @NotNull
    public String Text;
    public CliLogEntryType Type;

    public CliLogEntry(@NotNull String text, CliLogEntryType type) {
        this.Text = text;
        this.Type = type;
    }
}
