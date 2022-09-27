package jezzsantos.automate.plugin.common;

import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;

public class General {

    public static String toHtmlLink(@NotNull String hyperlink) {

        return toHtmlLink(hyperlink, AutomateBundle.message("general.Hyperlink.Title"));
    }

    public static String toHtmlLink(@NotNull String hyperlink, @NotNull String text) {

        return String.format("<a href=\"%s\">%s</a>", hyperlink, text);
    }
}
