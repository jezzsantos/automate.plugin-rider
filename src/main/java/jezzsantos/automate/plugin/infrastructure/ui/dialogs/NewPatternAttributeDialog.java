package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NewPatternAttributeDialog extends DialogWrapper {

    private final NewPatternAttributeDialogContext context;
    private JTextField name;
    private JTextField defaultValue;
    private ComboBox<AutomateConstants.AttributeDataType> dataTypes;
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
            label.setText(Objects.requireNonNullElseGet(value.getDisplayName(), () -> AutomateBundle.message("dialog.NewAttribute.NoDataTypes.Message")));
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
          ? (AutomateConstants.AttributeDataType) this.dataTypes.getSelectedItem()
          : null;
        var choices = ((CollectionListModel<String>) this.choices.getModel()).toList();
        return doValidate(this.context, this.name.getText(), dataType, this.defaultValue.getText(), choices);
    }

    @Override
    protected void doOKAction() {

        super.doOKAction();
        this.context.Name = this.name.getText();
        this.context.IsRequired = this.isRequired.isSelected();
        this.context.DefaultValue = this.defaultValue.getText();
        this.context.DataType = (AutomateConstants.AttributeDataType) this.dataTypes.getSelectedItem();
        this.context.Choices = this.choices.getSelectedValuesList();
    }

    @TestOnly
    public static @Nullable ValidationInfo doValidate(NewPatternAttributeDialogContext context, String name, AutomateConstants.AttributeDataType dataType, String defaultValue, List<String> choices) {

        if (!name.matches(AutomateConstants.AttributeNameRegex)) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewAttribute.NameValidation.NotMatch.Message"));
        }
        var existingName = context.Attributes.stream()
          .anyMatch(attribute -> attribute.getName().equalsIgnoreCase(name));
        if (existingName) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewAttribute.NameValidation.Exists.Message"));
        }
        var allowedDataTypes = context.DataTypes.stream()
          .map(AutomateConstants.AttributeDataType::getDisplayName)
          .collect(Collectors.joining(", "));
        if (dataType == null) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewAttribute.DataTypeValidation.NotMatch.Message", allowedDataTypes));
        }
        if (!context.DataTypes.contains(dataType)) {
            return new ValidationInfo(AutomateBundle.message("dialog.NewAttribute.DataTypeValidation.NotMatch.Message", allowedDataTypes));
        }
        if (!defaultValue.isEmpty()) {
            if (!Attribute.isValidDataType(dataType, defaultValue)) {
                return new ValidationInfo(AutomateBundle.message("dialog.NewAttribute.DefaultValueValidation.NotDataType.Message", dataType));
            }
        }
        if (!choices.isEmpty()) {
            if (!defaultValue.isEmpty()) {
                if (!Attribute.isOneOfChoices(choices, defaultValue)) {
                    return new ValidationInfo(AutomateBundle.message("dialog.NewAttribute.DefaultValueValidation.NotAChoice.Message"));
                }
            }
            var invalidChoices = choices.stream()
              .filter((choice -> !Attribute.isValidDataType(dataType, choice)))
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

    public static class NewPatternAttributeDialogContext {

        public List<Attribute> Attributes;
        public String Name;
        public boolean IsRequired = false;
        public String DefaultValue;
        public AutomateConstants.AttributeDataType DataType = AutomateConstants.AttributeDataType.STRING;
        public List<String> Choices = new ArrayList<>();
        public List<AutomateConstants.AttributeDataType> DataTypes;

        public NewPatternAttributeDialogContext(@NotNull List<Attribute> attributes, @NotNull List<AutomateConstants.AttributeDataType> dataTypes) {

            this.Attributes = attributes;
            this.DataTypes = dataTypes;
        }
    }
}
