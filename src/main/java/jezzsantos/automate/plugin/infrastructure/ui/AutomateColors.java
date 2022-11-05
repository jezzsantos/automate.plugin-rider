package jezzsantos.automate.plugin.infrastructure.ui;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * The returned JBColor values from this class will adjust themselves when the theme changes from light and dark
 */
@SuppressWarnings("UseJBColor")
public class AutomateColors {

    @NotNull
    public static JBColor getSuccessText() {

        return JBColor.GREEN;
    }

    @NotNull
    public static JBColor getWarningText() {

        return JBColor.ORANGE;
    }

    @NotNull
    public static JBColor getErrorText() {

        return JBColor.RED;
    }

    @NotNull
    public static JBColor getNormalText() {

        return JBColor.lazy(UIUtil::getLabelForeground);
    }

    @NotNull
    public static JBColor getDisabledText() {

        return JBColor.lazy(UIUtil::getLabelDisabledForeground);
    }

    @NotNull
    public static JBColor getSuccessBackground() {

        var darkColor = new Color(55, 88, 50);
        var lightColor = new Color(212, 243, 191);

        return new JBColor(lightColor, darkColor);
    }

    @NotNull
    public static JBColor getWarningBackground() {

        var darkColor = new Color(80, 80, 38);
        var lightColor = new Color(243, 243, 191);

        return new JBColor(lightColor, darkColor);
    }

    @NotNull
    public static JBColor getErrorBackground() {

        var darkColor = new Color(128, 0, 0);
        var lightColor = new Color(243, 191, 191);

        return new JBColor(lightColor, darkColor);
    }
}
