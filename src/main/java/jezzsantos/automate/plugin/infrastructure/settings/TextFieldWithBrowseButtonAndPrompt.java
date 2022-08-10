package jezzsantos.automate.plugin.infrastructure.settings;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class TextFieldWithBrowseButtonAndPrompt extends TextFieldWithBrowseButton {

    private String promptText;
    private String promptFormat;

    public TextFieldWithBrowseButtonAndPrompt() {
        this.promptFormat = "%s";
        this.promptText = "";
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        var textField = this.getTextField();
        if (textField.hasFocus()) {
            textField.paint(g);
        } else {
            var currentText = textField.getText();
            if (currentText.isEmpty()) {
                var hint = String.format(promptFormat, promptText);
                int height = getHeight();
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                var insets = textField.getInsets();
                var fontMetrics = g.getFontMetrics();
                g.setColor(JBColor.gray);
                g.drawString(hint, insets.left + 5, height / 2 + fontMetrics.getAscent() / 2 - 2);
            }
        }
    }

    public void setPrompt(@NotNull String format, @NotNull String text) {
        this.promptFormat = format;
        this.promptText = text;
    }
}
