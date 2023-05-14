package jezzsantos.automate.plugin.infrastructure.ui.components;

import com.intellij.ide.BrowserUtil;
import com.intellij.ui.components.JBLabel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class HyperLink extends JBLabel {

    private String url;

    public HyperLink() {

        super();

        init();
        this.url = "";
    }

    public HyperLink(@NotNull String linkText, @NotNull String url) {

        this();
        setLink(null, linkText, url);
    }

    @SuppressWarnings("unused")
    public HyperLink(@NotNull String normalText, @NotNull String linkText, @NotNull String url) {

        this();
        setLink(normalText, linkText, url);
    }

    @SuppressWarnings("unused")
    public void setLink(@NotNull String linkText, @NotNull String url) {

        setLink(null, linkText, url);
    }

    public void setLink(@Nullable String normalText, @NotNull String linkText, @NotNull String url) {

        if (normalText != null && !normalText.isEmpty()) {
            this.setText(String.format("<html><span>%s</span> <a href=\"#\">%s</a></html>", normalText, linkText));
        }
        else {
            this.setText(String.format("<html><a href=\"#\">%s</a></html>", linkText));
        }
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.url = url;
    }

    private void init() {

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                var url = HyperLink.this.url;
                if (url == null
                  || url.isEmpty()) {
                    return;
                }

                try {
                    BrowserUtil.browse(url);
                } catch (Exception ignored) {
                }
            }
        });
    }
}
