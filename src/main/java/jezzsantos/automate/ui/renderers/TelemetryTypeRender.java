package jezzsantos.automate.ui.renderers;

import com.intellij.ui.JBColor;
import jezzsantos.automate.TelemetryType;

import javax.swing.*;
import java.awt.*;

public class TelemetryTypeRender extends TelemetryRenderBase {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        TelemetryType type = (TelemetryType) value;

        super.setText(type.toString());

        switch (type) {
            case Message:
                super.setForeground(JBColor.namedColor("Automate.TelemetryColor.Message", JBColor.orange));
                break;
            case Request:
                super.setForeground(JBColor.namedColor("Automate.TelemetryColor.Request", JBColor.green));
                break;
            case Exception:
                super.setForeground(JBColor.namedColor("Automate.TelemetryColor.Exception", JBColor.red));
                break;
            case Metric:
                super.setForeground(JBColor.namedColor("Automate.TelemetryColor.Metric", JBColor.gray));
                break;
            case Event:
                super.setForeground(JBColor.namedColor("Automate.TelemetryColor.CustomEvents", JBColor.cyan));
                break;
            case RemoteDependency:
                super.setText("Dependency");
                super.setForeground(JBColor.namedColor("Automate.TelemetryColor.RemoteDependency", JBColor.blue));
                break;
            case Unk:
                super.setForeground(JBColor.namedColor("Automate.TelemetryColor.Unk", JBColor.darkGray));
                break;
        }
        return this;
    }
}
