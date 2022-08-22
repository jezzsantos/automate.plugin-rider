package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NewAttributeDialogContext {

    public List<Attribute> Attributes;
    public String Name;
    public boolean IsRequired = false;
    public String DefaultValue;
    public String DataType = "string";
    public List<String> Choices = new ArrayList<>();
    public List<String> DataTypes;

    public NewAttributeDialogContext(@NotNull List<Attribute> attributes, @NotNull List<String> dataTypes) {

        this.Attributes = attributes;
        this.DataTypes = dataTypes;
    }
}
