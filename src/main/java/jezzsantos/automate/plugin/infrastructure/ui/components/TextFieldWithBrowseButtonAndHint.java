package jezzsantos.automate.plugin.infrastructure.ui.components;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import jezzsantos.automate.plugin.infrastructure.ui.AutomateColors;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class TextFieldWithBrowseButtonAndHint extends TextFieldWithBrowseButton {

    private String hint;

    public TextFieldWithBrowseButtonAndHint() {

        this.hint = "";
    }

    public void setHint(@NotNull String hint) {

        this.hint = hint;
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        var textField = this.getTextField();
        if (textField.hasFocus()) {
            textField.paint(g);
        }
        else {
            var currentText = textField.getText();
            if (currentText.isEmpty()) {
                int height = getHeight();
                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                var insets = textField.getInsets();
                var fontMetrics = g.getFontMetrics();
                g.setColor(AutomateColors.getDisabledText());
                g.drawString(this.hint, insets.left + 5, height / 2 + fontMetrics.getAscent() / 2 - 2);
            }
        }
    }
}
