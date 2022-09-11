package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.github.hypfvieh.util.TypeUtil.isDouble;
import static com.github.hypfvieh.util.TypeUtil.isInteger;

public class NewPatternAttributeDialog extends DialogWrapper {

    private final NewPatternAttributeDialogContext context;
    private JTextField name;
    private JTextField defaultValue;
    private JComboBox<String> dataTypes;
    private JLabel nameTitle;
    private JPanel contents;
    private JCheckBox isRequired;
    private JLabel dataTypeTitle;
    private JLabel defaultValueTitle;
    private JLabel choicesTitle;
    private JBList<String> choices;

    public NewPatternAttributeDialog(Project project, @NotNull NewPatternAttributeDialogContext context) {

        super(project);

        this.context = context;

        this.init();
        this.setTitle(AutomateBundle.message("dialog.NewAttribute.Title"));
        this.nameTitle.setText(AutomateBundle.message("dialog.NewAttribute.Name.Title"));
        this.name.setText(this.context.Name);
        this.isRequired.setText(AutomateBundle.message("dialog.NewAttribute.IsRequired.Title"));
        this.isRequired.setSelected(this.context.IsRequired);
        this.defaultValueTitle.setText(AutomateBundle.message("dialog.NewAttribute.DefaultValue.Title"));
        this.defaultValue.setText(this.context.DefaultValue);
        this.dataTypeTitle.setText(AutomateBundle.message("dialog.NewAttribute.DataType.Title"));
        this.dataTypes.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            var label = new JLabel();
            label.setText(Objects.requireNonNullElseGet(value, () -> AutomateBundle.message("dialog.NewAttribute.NoDataTypes.Message")));
            return label;
        });
        for (var type : this.context.DataTypes) {
            this.dataTypes.addItem(type);
        }
        this.dataTypes.setSelectedItem(this.context.DataType);
        this.choicesTitle.setText(AutomateBundle.message("dialog.NewAttribute.Choices.Title"));
        this.choices.getEmptyText().setText(AutomateBundle.message("dialog.NewAttribute.EmptyChoices.Message"));
        this.choices.setModel(new CollectionListModel<>());
    }

    @TestOnly
    public static @Nullable ValidationInfo doValidate(NewPatternAttributeDialogContext context, String name, String dataType, String defaultValue, List<String> choices) {

        if (!name.matches(AutomateConstants.AttributeNameRegex)) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewAttribute.NameValidation.NotMatch.Message"));
        }
        var existingName = context.Attributes.stream()
          .anyMatch(attribute -> attribute.getName().equalsIgnoreCase(name));
        if (existingName) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewAttribute.NameValidation.Exists.Message"));
        }
        if (dataType.isEmpty()) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewAttribute.DataTypeValidation.NotMatch.Message", String.join(", ", context.DataTypes)));
        }
        if (!context.DataTypes.contains(dataType)) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewAttribute.DataTypeValidation.NotMatch.Message", String.join(", ", context.DataTypes)));
        }
        if (!defaultValue.isEmpty()) {
            if (!isValidDataType(dataType, defaultValue)) {
                return new ValidationInfo(AutomateBundle.message("dialog.NewAttribute.DefaultValueValidation.NotDataType.Message", dataType));
            }
        }
        if (!choices.isEmpty()) {
            if (!defaultValue.isEmpty()) {
                if (!choices.contains(defaultValue)) {
                    return new ValidationInfo(AutomateBundle.message("dialog.NewAttribute.DefaultValueValidation.NotAChoice.Message"));
                }
            }
            var invalidChoices = choices.stream()
              .filter((s -> !isValidDataType(dataType, s)))
              .collect(Collectors.toList());
            if (!invalidChoices.isEmpty()) {
                return new ValidationInfo(AutomateBundle.message("dialog.NewAttribute.ChoicesValidation.NotDataType.Message", invalidChoices.get(0), dataType));
            }
        }

        return null;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {

        return this.name;
    }

    public NewPatternAttributeDialogContext getContext() {

        return this.context;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean isValidDataType(String dataType, String value) {

        switch (dataType) {
            case "string":
                return true;

            case "bool":
                return List.of("true", "false").contains(value.toLowerCase());

            case "int":
                return isInteger(value);

            case "float":
                return isDouble(value);

            case "datetime":
                return isIsoDate(value);

            default:
                return false;
        }
    }

    private static boolean isIsoDate(String value) {

        try {
            Instant.from(DateTimeFormatter.ISO_INSTANT.parse(value));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static class NewPatternAttributeDialogContext {

        public List<Attribute> Attributes;
        public String Name;
        public boolean IsRequired = false;
        public String DefaultValue;
        public String DataType = "string";
        public List<String> Choices = new ArrayList<>();
        public List<String> DataTypes;

        public NewPatternAttributeDialogContext(@NotNull List<Attribute> attributes, @NotNull List<String> dataTypes) {

            this.Attributes = attributes;
            this.DataTypes = dataTypes;
        }
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {

        //        var decorator = ToolbarDecorator.createDecorator(choices);
        //        decorator.setAddAction(anActionButton -> {
        //
        //        });
        //        decorator.setRemoveAction(anActionButton -> {
        //
        //        });
        //        decorator.disableUpDownActions();
        //        //contents.add(decorator.createPanel(), new GridConstraints());
        //        decorator.createPanel();

        return this.contents;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {

        var dataType = this.dataTypes.getSelectedItem() != null
          ? (String) this.dataTypes.getSelectedItem()
          : "";
        var choices = ((CollectionListModel<String>) this.choices.getModel()).toList();
        return doValidate(this.context, this.name.getText(), dataType, this.defaultValue.getText(), choices);
    }

    @Override
    protected void doOKAction() {

        super.doOKAction();
        this.context.Name = this.name.getText();
        this.context.IsRequired = this.isRequired.isSelected();
        this.context.DefaultValue = this.defaultValue.getText();
        this.context.DataType = (String) this.dataTypes.getSelectedItem();
        this.context.Choices = this.choices.getSelectedValuesList();
    }
}
