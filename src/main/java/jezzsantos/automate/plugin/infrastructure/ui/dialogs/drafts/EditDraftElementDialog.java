package jezzsantos.automate.plugin.infrastructure.ui.dialogs.drafts;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElement;
import jezzsantos.automate.plugin.application.interfaces.drafts.ElementValueMap;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class EditDraftElementDialog extends DialogWrapper {

    private final EditDraftElementDialogContext context;
    private JPanel contents;

    public EditDraftElementDialog(@Nullable Project project, @NotNull EditDraftElementDialog.EditDraftElementDialogContext context) {

        super(project);
        this.context = context;

        this.init();
        this.setTitle(context.getIsNew()
                        ? AutomateBundle.message("dialog.EditDraftElement.NewElement.Title", context.getSchema().getName())
                        : AutomateBundle.message("dialog.EditDraftElement.UpdateElement.Title", context.getSchema().getName()));
        buildUI(context, this.contents);
        setOKButtonText(context.getIsNew()
                          ? AutomateBundle.message("dialog.EditDraftElement.NewElement.Confirm.Title")
                          : AutomateBundle.message("dialog.EditDraftElement.UpdateElement.Confirm.Title"));
    }

    @TestOnly
    public static @Nullable ValidationInfo doValidate(EditDraftElementDialog.@NotNull EditDraftElementDialogContext context) {

        var behaviours = context.getBehaviours();
        for (var entry : behaviours.entrySet()) {
            var validation = entry.getValue().validate();
            if (validation != null) {
                return validation;
            }
        }

        return null;
    }

    @TestOnly
    public static void buildUI(EditDraftElementDialog.@NotNull EditDraftElementDialogContext context, @NotNull JPanel contents) {

        var behaviours = context.getBehaviours();
        var rowIndex = new AtomicInteger();
        var totalRows = behaviours.size();
        if (totalRows == 0) {
            contents.setLayout(new GridLayoutManager(1, 1));
            var label = new JLabel(AutomateBundle.message("dialog.EditDraftElement.NoControls.Message"));
            contents.add(label, createSimpleConstraints(0, 0, true));
        }
        else {
            contents.setLayout(new GridLayoutManager(totalRows, 2));
            behaviours.forEach((name, behaviour) -> {
                if (behaviour.getComponentLabel() != null) {
                    contents.add(behaviour.getComponentLabel(), createSimpleConstraints(rowIndex.get(), 0, false));
                }
                contents.add(behaviour.getComponent(), createSimpleConstraints(rowIndex.get(), 1, true));
                rowIndex.getAndIncrement();
            });
        }
        contents.revalidate();
    }

    public EditDraftElementDialog.EditDraftElementDialogContext getContext() {

        return this.context;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {

        return doValidate(this.context);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {

        return this.contents;
    }

    @Override
    protected void doOKAction() {

        super.doOKAction();
        this.context.commitValues();
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {

        var behaviours = this.context.getBehaviours();
        if (behaviours.isEmpty()) {
            return this.contents;
        }
        else {
            return behaviours.entrySet().iterator().next().getValue().getComponent();
        }
    }

    @NotNull
    private static GridConstraints createSimpleConstraints(int rowIndex, int column, boolean fillHorizontally) {

        var fill = fillHorizontally
          ? GridConstraints.FILL_HORIZONTAL
          : GridConstraints.FILL_NONE;
        var horizSizePolicy = fillHorizontally
          ? GridConstraints.SIZEPOLICY_CAN_GROW + GridConstraints.SIZEPOLICY_WANT_GROW
          : GridConstraints.SIZEPOLICY_FIXED;
        return new GridConstraints(rowIndex, column, 1, 1, GridConstraints.ANCHOR_WEST, fill, horizSizePolicy,
                                   GridConstraints.SIZEPOLICY_FIXED, null, null, null);
    }

    public static class EditDraftElementDialogContext {

        private final PatternElement schema;
        private final DraftElement element;
        private final boolean isNew;
        private final LinkedHashMap<String, Behaviour> behaviours;

        public EditDraftElementDialogContext(PatternElement schema) {

            this.isNew = true;
            this.element = new DraftElement("new", Map.of(), false);
            this.schema = schema;
            this.behaviours = populatePairs(schema.getAttributes());
        }

        public EditDraftElementDialogContext(DraftElement element, PatternElement schema) {

            this.isNew = false;
            this.element = element;
            this.schema = schema;
            this.behaviours = populatePairs(element.getProperties(), schema.getAttributes());
        }

        public boolean getIsNew() {return this.isNew;}

        public PatternElement getSchema() {return this.schema;}

        public DraftElement getElement() {return this.element;}

        public Map<String, Behaviour> getBehaviours() {

            return this.behaviours;
        }

        public void commitValues() {

            this.behaviours.forEach((name, behaviour) -> behaviour.commitValue());
        }

        public Map<String, String> getValues() {

            return this.behaviours.entrySet()
              .stream()
              .filter(entry -> entry.getValue().hasChangedValue())
              .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getFinalValue()));
        }

        private LinkedHashMap<String, Behaviour> populatePairs(List<Attribute> attributes) {

            var pairs = new LinkedHashMap<String, Behaviour>();
            attributes.stream()
              .sorted(Comparator.comparing(Attribute::getName))
              .forEach(attribute -> {
                  var name = attribute.getName();
                  pairs.put(name, new Behaviour(name, attribute.getDefaultValue(), attribute));
              });

            return pairs;
        }

        private LinkedHashMap<String, Behaviour> populatePairs(ElementValueMap properties, List<Attribute> attributes) {

            var pairs = new LinkedHashMap<String, Behaviour>();

            attributes.stream()
              .sorted(Comparator.comparing(Attribute::getName))
              .forEach(attribute -> {
                  var name = attribute.getName();
                  var property = properties.get(name);
                  pairs.put(name, new Behaviour(name, property == null
                    ? attribute.getDefaultValue()
                    : property.getValue(), attribute));
              });

            return pairs;
        }
    }

    public static class Behaviour {

        private final Attribute attribute;
        private final String initialValue;
        private final String name;
        private String finalValue;
        private JComponent component;
        private JLabel componentLabel;

        private Supplier<String> getFunction;
        private boolean hasBeenFinalised = false;

        public Behaviour(@NotNull String name, @Nullable String initialValue, @NotNull Attribute attribute) {

            this.name = name;
            this.initialValue = initialValue;
            this.attribute = attribute;
            calculateComponent(name, attribute, initialValue);
        }

        @NotNull
        public JComponent getComponent() {return this.component;}

        @Nullable
        public JLabel getComponentLabel() {return this.componentLabel;}

        public ValidationInfo validate() {

            var currentValue = this.getFunction.get();
            if (this.attribute.isRequired()) {
                if (currentValue == null || currentValue.isEmpty()) {
                    return new ValidationInfo(AutomateBundle.message("dialog.EditDraftElement.Validation.RequiredAndMissing.Message", this.name));
                }
            }
            else {
                if (!this.attribute.isValidDataType(currentValue)) {
                    return new ValidationInfo(
                      AutomateBundle.message("dialog.EditDraftElement.Validation.InvalidDataType.Message", this.name, this.attribute.getDataType().getDisplayName()));
                }
                var choices = this.attribute.getChoices();
                if (!choices.isEmpty()) {
                    if (!this.attribute.isOneOfChoices(currentValue)) {
                        return new ValidationInfo(
                          AutomateBundle.message("dialog.EditDraftElement.Validation.InvalidChoice.Message", this.name, String.join(", ", choices)));
                    }
                }
            }

            return null;
        }

        public void commitValue() {

            this.finalValue = this.getFunction.get();
            this.hasBeenFinalised = true;
        }

        public String getFinalValue() {return this.finalValue;}

        @TestOnly
        public void setFinalValue(String value) {

            this.finalValue = value;
            this.hasBeenFinalised = true;
        }

        public boolean hasChangedValue() {

            if (!this.hasBeenFinalised) {
                return false;
            }

            return !this.finalValue.equals(this.initialValue);
        }

        @SuppressWarnings("unchecked")
        private void calculateComponent(@NotNull String name, @NotNull Attribute attribute, @Nullable String value) {

            var dataType = attribute.getDataType();

            var requiresLabel = true;
            if (attribute.hasChoices()) {
                this.component = new ComboBox<String>();
                var choices = attribute.getChoices();
                var combo = ((ComboBox<String>) this.component);
                combo.addItem("");
                choices.forEach(combo::addItem);
                combo.setItem(Objects.requireNonNullElse(value, ""));
                this.getFunction = () -> (String) combo.getSelectedItem();
            }
            else {
                if (dataType == AutomateConstants.AttributeDataType.BOOLEAN) {
                    this.component = new JCheckBox();
                    var checkbox = (JCheckBox) this.component;
                    checkbox.setSelected(Boolean.parseBoolean(value));
                    checkbox.setText(name);
                    this.getFunction = () -> Boolean.toString(checkbox.isSelected());
                    requiresLabel = false;
                }
                else {
                    this.component = new JTextField();
                    var textField = (JTextField) this.component;
                    textField.setText(value);
                    this.getFunction = () -> (String) textField.getText();
                }
            }

            if (requiresLabel) {
                this.componentLabel = new JLabel(name);
                this.componentLabel.setLabelFor(this.component);
            }
        }
    }
}
