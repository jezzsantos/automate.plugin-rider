package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.github.hypfvieh.util.TypeUtil.isDouble;
import static com.github.hypfvieh.util.TypeUtil.isInteger;

public class NewAttributeDialog extends DialogWrapper {

    private final NewAttributeDialogContext context;
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

    public NewAttributeDialog(Project project, @NotNull NewAttributeDialogContext context) {
        super(project);

        this.context = context;

        this.init();
        this.setTitle(AutomateBundle.message("dialog.NewAttribute.Title"));
        nameTitle.setText(AutomateBundle.message("dialog.NewAttribute.Name.Title"));
        name.setText(this.context.Name);
        isRequired.setText(AutomateBundle.message("dialog.NewAttribute.IsRequired.Title"));
        isRequired.setSelected(this.context.IsRequired);
        defaultValueTitle.setText(AutomateBundle.message("dialog.NewAttribute.DefaultValue.Title"));
        defaultValue.setText(this.context.DefaultValue);
        dataTypeTitle.setText(AutomateBundle.message("dialog.NewAttribute.DataType.Title"));
        dataTypes.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            var label = new JLabel();
            label.setText(Objects.requireNonNullElseGet(value, () -> AutomateBundle.message("dialog.NewAttribute.NoDataTypes.Message")));
            return label;
        });
        for (var type : this.context.DataTypes) {
            dataTypes.addItem(type);
        }
        dataTypes.setSelectedItem(this.context.DataType);
        choicesTitle.setText(AutomateBundle.message("dialog.NewAttribute.Choices.Title"));
        choices.getEmptyText().setText(AutomateBundle.message("dialog.NewAttribute.EmptyChoices.Message"));
        choices.setModel(new CollectionListModel<>());
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

    @TestOnly
    public static @Nullable ValidationInfo doValidate(NewAttributeDialogContext context, String name, String dataType, String defaultValue, List<String> choices) {
        if (!name.matches(AutomateConstants.AttributeNameRegex)) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewAttribute.NameValidation.NotMatch.Message"));
        }
        var existingName = context.Attributes.stream().anyMatch(attribute -> attribute.getName().equalsIgnoreCase(name));
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
        return name;
    }

    public NewAttributeDialogContext getContext() {
        return this.context;
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

        return contents;
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
