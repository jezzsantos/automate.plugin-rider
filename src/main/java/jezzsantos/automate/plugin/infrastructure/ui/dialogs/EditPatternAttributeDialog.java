package jezzsantos.automate.plugin.infrastructure.ui.dialogs;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.intellij.uiDesigner.core.GridLayoutManager;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EditPatternAttributeDialog extends DialogWrapper {

    private final EditPatternAttributeDialogContext context;
    private final Project project;
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

    public EditPatternAttributeDialog(@NotNull Project project, @NotNull EditPatternAttributeDialog.EditPatternAttributeDialogContext context) {

        super(project);

        this.context = context;
        this.project = project;

        this.init();
        this.setTitle(context.getIsNew()
                        ? AutomateBundle.message("dialog.EditPatternAttribute.NewElement.Title")
                        : AutomateBundle.message("dialog.EditPatternAttribute.UpdateElement.Title"));
        this.nameTitle.setText(AutomateBundle.message("dialog.EditPatternAttribute.Name.Title"));
        this.name.setText(this.context.getName());
        this.isRequired.setText(AutomateBundle.message("dialog.EditPatternAttribute.IsRequired.Title"));
        this.isRequired.setSelected(this.context.getIsRequired());
        this.defaultValueTitle.setText(AutomateBundle.message("dialog.EditPatternAttribute.DefaultValue.Title"));
        this.defaultValue.setText(this.context.getDefaultValue());
        this.dataTypeTitle.setText(AutomateBundle.message("dialog.EditPatternAttribute.DataType.Title"));
        this.dataTypes.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            var label = new JLabel();
            label.setText(Objects.requireNonNullElseGet(value.getDisplayName(), () -> AutomateBundle.message("dialog.EditPatternAttribute.NoDataTypes.Message")));
            return label;
        });
        for (var type : this.context.getAvailableDataTypes()) {
            this.dataTypes.addItem(type);
        }
        this.dataTypes.setSelectedItem(this.context.getDataType());
        this.choicesTitle.setText(AutomateBundle.message("dialog.EditPatternAttribute.Choices.Title"));
        this.choices.getEmptyText().setText(AutomateBundle.message("dialog.EditPatternAttribute.EmptyChoices.Message"));
        var model = new CollectionListModel<String>();
        this.context.choices.forEach(model::add);
        this.choices.setModel(model);
        setOKButtonText(context.getIsNew()
                          ? AutomateBundle.message("dialog.EditPatternAttribute.NewElement.Confirm.Title")
                          : AutomateBundle.message("dialog.EditPatternAttribute.UpdateElement.Confirm.Title"));
    }

    @TestOnly
    public static @Nullable ValidationInfo doValidate(@NotNull EditPatternAttributeDialogContext context, @NotNull String name, @Nullable AutomateConstants.AttributeDataType dataType, @NotNull String defaultValue, @NotNull List<String> choices) {

        if (!context.isValidName(name)) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternAttribute.NameValidation.NotMatch.Message"));
        }
        if (!context.isAvailableName(name)) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternAttribute.NameValidation.Exists.Message"));
        }
        if (!context.isValidDataType(dataType)) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternAttribute.DataTypeValidation.NotMatch.Message", context.getAvailableDataTypesAsString()));
        }
        if (!defaultValue.isEmpty()) {
            if (!context.isValidValue(dataType, defaultValue)) {
                return new ValidationInfo(AutomateBundle.message("dialog.EditPatternAttribute.DefaultValueValidation.NotDataType.Message", dataType));
            }
        }
        if (!choices.isEmpty()) {
            if (!defaultValue.isEmpty()) {
                if (!context.isValidChoice(choices, defaultValue)) {
                    return new ValidationInfo(AutomateBundle.message("dialog.EditPatternAttribute.DefaultValueValidation.NotAChoice.Message"));
                }
            }
            var invalidChoice = context.getNextInvalidChoice(Objects.requireNonNull(dataType), choices);
            if (invalidChoice != null) {
                return new ValidationInfo(AutomateBundle.message("dialog.EditPatternAttribute.ChoicesValidation.NotDataType.Message", invalidChoice, dataType.getDisplayName()));
            }
        }

        return null;
    }

    public EditPatternAttributeDialogContext getContext() {

        return this.context;
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
    protected @Nullable JComponent createCenterPanel() {

        var decorator = ToolbarDecorator.createDecorator(this.choices);
        decorator.setAddAction(anActionButton -> {
            var dialog = new EditValueDialog(this.project, new EditValueDialog.EditValueDialogContext(true, AutomateBundle.message(
              "dialog.EditPatternAttribute.Choices.NewValue.Title"), value -> {
                var existingValues = ((CollectionListModel<String>) this.choices.getModel()).toList();
                if (existingValues.contains(value)) {
                    return new ValidationInfo(AutomateBundle.message("dialog.EditPatternAttribute.ChoicesValidation.Exists.Message", value));
                }
                var dataType = this.dataTypes.getSelectedItem() != null
                  ? (AutomateConstants.AttributeDataType) this.dataTypes.getSelectedItem()
                  : AutomateConstants.AttributeDataType.STRING;
                if (!Attribute.isValidDataType(dataType, value)) {
                    return new ValidationInfo(AutomateBundle.message("dialog.EditPatternAttribute.ChoicesValidation.NotDataType.Message", value, dataType.getDisplayName()));
                }
                return null;
            }));
            if (dialog.showAndGet()) {
                var model = ((CollectionListModel<String>) this.choices.getModel());
                model.add(dialog.getContext().Value);
            }
        });
        decorator.setRemoveAction(anActionButton -> {
            var selectedValue = this.choices.getSelectedValue();
            if (selectedValue != null) {
                var model = ((CollectionListModel<String>) this.choices.getModel());
                model.remove(selectedValue);
            }
        });

        var constraints = ((GridLayoutManager) this.contents.getLayout()).getConstraintsForComponent(this.choices);
        this.contents.add(decorator.createPanel(), constraints);

        return this.contents;
    }

    @Override
    protected void doOKAction() {

        super.doOKAction();
        this.context.setName(this.name.getText());
        this.context.setIsRequired(this.isRequired.isSelected());
        this.context.setDefaultValue(this.defaultValue.getText());
        this.context.setDataType((AutomateConstants.AttributeDataType) this.dataTypes.getSelectedItem());
        this.context.setChoices(((CollectionListModel<String>) this.choices.getModel()).toList());
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {

        return this.context.getIsNew()
          ? this.name
          : this.isRequired;
    }

    public static class EditPatternAttributeDialogContext {

        private final List<Attribute> attributes;
        private final boolean isNew;
        private final List<AutomateConstants.AttributeDataType> dataTypes;
        private final String originalName;
        private String name;
        private boolean isRequired;
        private String defaultValue;
        private AutomateConstants.AttributeDataType dataType;
        private List<String> choices;

        public EditPatternAttributeDialogContext(@NotNull List<Attribute> attributes, @NotNull List<AutomateConstants.AttributeDataType> dataTypes) {

            this.isNew = true;
            this.attributes = attributes;
            this.dataTypes = dataTypes;
            this.name = "";
            this.originalName = null;
            this.isRequired = false;
            this.defaultValue = "";
            this.dataType = AutomateConstants.AttributeDataType.STRING;
            this.choices = new ArrayList<>();
        }

        public EditPatternAttributeDialogContext(@NotNull Attribute attribute, @NotNull List<Attribute> attributes, @NotNull List<AutomateConstants.AttributeDataType> dataTypes) {

            this.isNew = false;
            this.attributes = attributes;
            this.dataTypes = dataTypes;
            this.name = attribute.getName();
            this.originalName = attribute.getName();
            this.isRequired = attribute.isRequired();
            this.defaultValue = attribute.getDefaultValue();
            this.dataType = attribute.getDataType();
            this.choices = attribute.getChoices();
        }

        public boolean getIsNew() {return this.isNew;}

        @NotNull
        public String getId() {

            return this.isNew
              ? this.name
              : this.originalName;
        }

        @NotNull
        public String getName() {

            return this.name;
        }

        public void setName(@NotNull String name) {

            this.name = name;
        }

        public boolean getIsRequired() {return this.isRequired;}

        public void setIsRequired(boolean isRequired) {

            this.isRequired = isRequired;
        }

        public AutomateConstants.AttributeDataType getDataType() {return this.dataType;}

        public void setDataType(AutomateConstants.AttributeDataType dataType) {

            this.dataType = dataType;
        }

        @Nullable
        public String getDefaultValue() {return this.defaultValue;}

        public void setDefaultValue(@Nullable String value) {

            this.defaultValue = value;
        }

        @NotNull
        public List<String> getChoices() {return this.choices;}

        public void setChoices(List<String> choices) {

            this.choices = choices;
        }

        public boolean isAvailableName(@NotNull String name) {

            var existingNames = this.attributes.stream()
              .map(Attribute::getName);
            var reservedNames = AutomateConstants.ReservedAttributeNames.stream();
            var illegalNames = Stream.concat(existingNames, reservedNames);

            if (this.isNew) {
                return illegalNames
                  .noneMatch(in -> in.equalsIgnoreCase(name));
            }
            else {
                return illegalNames
                  .filter(in -> !in.equalsIgnoreCase(this.originalName))
                  .noneMatch(in -> in.equalsIgnoreCase(name));
            }
        }

        @NotNull
        public String getAvailableDataTypesAsString() {

            return this.dataTypes.stream()
              .map(AutomateConstants.AttributeDataType::getDisplayName)
              .collect(Collectors.joining(", "));
        }

        @NotNull
        public List<AutomateConstants.AttributeDataType> getAvailableDataTypes() {

            return this.dataTypes;
        }

        public boolean isValidName(@Nullable String name) {

            if (name == null || name.isEmpty()) {
                return false;
            }
            return name.matches(AutomateConstants.AttributeNameRegex);
        }

        public boolean isValidDataType(@Nullable AutomateConstants.AttributeDataType dataType) {

            if (dataType == null) {
                return false;
            }

            return this.dataTypes.contains(dataType);
        }

        public boolean isValidValue(@Nullable AutomateConstants.AttributeDataType dataType, @NotNull String defaultValue) {

            if (dataType == null) {
                return false;
            }

            return Attribute.isValidDataType(dataType, defaultValue);
        }

        public boolean isValidChoice(@NotNull List<String> choices, @NotNull String defaultValue) {

            return Attribute.isOneOfChoices(choices, defaultValue);
        }

        @Nullable
        public String getNextInvalidChoice(@NotNull AutomateConstants.AttributeDataType dataType, @NotNull List<String> choices) {

            var invalidChoices = choices.stream()
              .filter((choice -> !Attribute.isValidDataType(dataType, choice))).toList();
            if (!invalidChoices.isEmpty()) {
                return invalidChoices.get(0);
            }

            return null;
        }
    }
}
