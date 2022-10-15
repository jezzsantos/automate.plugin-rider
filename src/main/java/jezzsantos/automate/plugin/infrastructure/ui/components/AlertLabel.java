package jezzsantos.automate.plugin.infrastructure.ui.components;

import com.intellij.ui.ColoredSideBorder;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import jezzsantos.automate.plugin.AutomateIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AlertLabel extends JBLabel {

    public AlertLabel() {

        setType(AlertLabelType.NONE);
    }

    public void setType(@NotNull AlertLabelType type) {

        this.setVerticalTextPosition(SwingConstants.CENTER);
        switch (type) {

            case NONE -> {
                this.setIconTextGap(0);
                this.setIcon(null);
                this.setOpaque(false);
                this.setBorder(null);
            }
            case SUCCESS -> {
                this.setIconTextGap(10);
                this.setIconWithAlignment(AutomateIcons.StatusSuccess, SwingConstants.LEFT, SwingConstants.TOP);
                this.setOpaque(true);
                this.setBackground(createColor(55, 88, 50));
                this.setBorder(createBorder(JBColor.GREEN)); //98, 150, 85
            }
            case WARNING -> {
                this.setIconTextGap(10);
                this.setIconWithAlignment(AutomateIcons.StatusWarning, SwingConstants.LEFT, SwingConstants.TOP);
                this.setOpaque(true);
                this.setBackground(createColor(80, 80, 38));
                this.setBorder(createBorder(JBColor.ORANGE)); //159, 107, 0
            }
            case ERROR -> {
                this.setIconTextGap(10);
                this.setIconWithAlignment(AutomateIcons.StatusAborted, SwingConstants.LEFT, SwingConstants.TOP);
                this.setOpaque(true);
                this.setBackground(createColor(128, 0, 0));
                this.setBorder(createBorder(JBColor.RED)); //255, 100, 100
            }
        }
    }

    @Override
    public void setText(@Nullable String text) {

        if (text == null || text.isEmpty()) {
            super.setText(text);
            return;
        }

        var multiLined = text.contains(System.lineSeparator());
        if (!multiLined) {
            super.setText(text);
            return;
        }

        var paragraphs = Arrays.stream(text
                                         .split(System.lineSeparator()))
          .map(line -> String.format("<p>%s</p>", line))
          .collect(Collectors.joining());

        super.setText(String.format("<html>%s<br></html>", paragraphs));
    }

    @SuppressWarnings("UseJBColor")
    @NotNull
    private JBColor createColor(int red, int green, int blue) {

        var color = new Color(red, green, blue);
        return new JBColor(color, color);
    }

    @NotNull
    private Border createBorder(@NotNull JBColor color) {

        return new ColoredSideBorder(color, color, color, color, 1);
    }

    public enum AlertLabelType {
        NONE,
        SUCCESS,
        WARNING,
        ERROR
    }
}
