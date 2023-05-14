package jezzsantos.automate.plugin.infrastructure.ui.dialogs.patterns;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.patterns.Attribute;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.util.List;
import java.util.stream.Stream;

public class EditPatternElementDialog extends DialogWrapper {

    private final EditPatternElementDialogContext context;
    private JLabel nameTitle;
    private JTextField name;
    private JLabel displayNameTitle;
    private JTextField displayName;
    private JLabel descriptionTitle;
    private JTextField description;
    private JPanel contents;
    private JCheckBox isRequired;
    private JCheckBox isAutoCreate;
    private JRadioButton isElement;
    private JRadioButton isCollection;

    public EditPatternElementDialog(@NotNull Project project, @NotNull EditPatternElementDialog.EditPatternElementDialogContext context) {

        super(project);
        this.context = context;

        this.init();
        this.setTitle(context.getIsNew()
                        ? context.getIsCollection()
          ? AutomateBundle.message("dialog.EditPatternElement.NewElement.Collection.Title")
          : AutomateBundle.message(
            "dialog.EditPatternElement.NewElement.Element.Title")
                        : context.isRoot
                          ? AutomateBundle.message("dialog.EditPatternElement.UpdateElement.Root.Title")
                          : context.getIsCollection()
                            ? AutomateBundle.message("dialog.EditPatternElement.UpdateElement.Collection.Title")
                            : AutomateBundle.message(
                              "dialog.EditPatternElement.UpdateElement.Element.Title"));
        this.nameTitle.setText(AutomateBundle.message("dialog.EditPatternElement.Name.Title"));
        this.name.setText(this.context.getName());
        this.descriptionTitle.setText(AutomateBundle.message("dialog.EditPatternElement.Description.Title"));
        this.description.setText(this.context.getDescription());
        this.displayNameTitle.setText(AutomateBundle.message("dialog.EditPatternElement.DisplayName.Title"));
        this.displayName.setText(this.context.getDisplayName());
        this.isElement.setVisible(!context.isRoot);
        this.isElement.setText(AutomateBundle.message("dialog.EditPatternElement.IsElement.Title"));
        this.isElement.setSelected(!this.context.getIsCollection());
        this.isCollection.setVisible(!context.isRoot);
        this.isCollection.setText(AutomateBundle.message("dialog.EditPatternElement.IsCollection.Title"));
        this.isCollection.setSelected(this.context.getIsCollection());
        this.isElement.setEnabled(context.getIsNew());
        this.isCollection.setEnabled(context.getIsNew());
        this.isRequired.setVisible(!context.isRoot);
        this.isRequired.setText(AutomateBundle.message("dialog.EditPatternElement.IsRequired.Title"));
        this.isRequired.setSelected(this.context.getIsRequired());
        this.isAutoCreate.setVisible(!context.isRoot);
        this.isAutoCreate.setText(AutomateBundle.message("dialog.EditPatternElement.IsAutoCreate.Title"));
        this.isAutoCreate.setSelected(this.context.getIsAutoCreate());
        setOKButtonText(context.getIsNew()
                          ? AutomateBundle.message("dialog.EditPatternElement.NewElement.Confirm.Title")
                          : AutomateBundle.message("dialog.EditPatternElement.UpdateElement.Confirm.Title"));
    }

    @TestOnly
    public static @Nullable ValidationInfo doValidate(@NotNull EditPatternElementDialogContext context, @NotNull String name, @Nullable String displayName, @NotNull String description) {

        if (!context.isValidName(name)) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternElement.NameValidation.NotMatch.Message"));
        }
        if (!context.isRoot()) {
            if (!context.isAvailableName(name)) {
                return new ValidationInfo(AutomateBundle.message("dialog.EditPatternElement.NameValidation.Exists.Message"));
            }
        }
        if (!context.isValidDisplayName(displayName)) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternElement.DisplayNameValidation.NotMatch.Message"));
        }
        if (!context.isValidDescription(description)) {
            return new ValidationInfo(AutomateBundle.message("dialog.EditPatternElement.DescriptionValidation.NotMatch.Message"));
        }

        return null;
    }

    @Override
    protected @Nullable ValidationInfo doValidate() {

        return doValidate(this.context, this.name.getText(), this.displayName.getText(), this.description.getText());
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {

        return this.contents;
    }

    @Override
    protected void doOKAction() {

        super.doOKAction();
        this.context.setName(this.name.getText());
        this.context.setDisplayName(this.displayName.getText());
        this.context.setDescription(this.description.getText());
        this.context.setIsRequired(this.isRequired.isSelected());
        this.context.setIsCollection(this.isCollection.isSelected());
        this.context.setIsAutoCreate(this.isAutoCreate.isSelected());
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {

        return this.context.getIsNew()
          ? this.name
          : this.isRequired;
    }

    public EditPatternElementDialogContext getContext() {

        return this.context;
    }

    public static class EditPatternElementDialogContext {

        private final List<Attribute> attributes;
        private final List<PatternElement> elements;
        private final boolean isNew;
        private final String originalName;
        private final boolean isRoot;
        private String name;
        private String displayName;
        private String description;
        private boolean isRequired;
        private boolean isCollection;
        private boolean isAutoCreate;

        public EditPatternElementDialogContext(@NotNull List<Attribute> attributes, @NotNull List<PatternElement> elements) {

            this.isNew = true;
            this.isRoot = false;
            this.attributes = attributes;
            this.elements = elements;
            this.name = "";
            this.originalName = null;
            this.displayName = "";
            this.description = "";
            this.isRequired = true;
            this.isCollection = false;
            this.isAutoCreate = true;
        }

        public EditPatternElementDialogContext(@NotNull PatternElement element, @NotNull List<Attribute> attributes, @NotNull List<PatternElement> elements) {

            this.isNew = false;
            this.isRoot = element.isRoot();
            this.attributes = attributes;
            this.elements = elements;
            this.name = element.getName();
            this.originalName = element.getName();
            var cardinality = element.getCardinality();
            this.displayName = element.getDisplayName();
            this.description = element.getDescription();
            this.isRequired = cardinality == AutomateConstants.ElementCardinality.ONE || cardinality == AutomateConstants.ElementCardinality.ONE_OR_MANY;
            this.isCollection = element.isCollection();
            this.isAutoCreate = element.isAutoCreate();
        }

        public boolean getIsNew() {return this.isNew;}

        public boolean isRoot() {return this.isRoot;}

        @NotNull
        public String getId() {

            return this.isNew
              ? this.name
              : this.originalName;
        }

        @NotNull
        public String getName() {return this.name;}

        public void setName(@NotNull String name) {this.name = name;}

        public boolean getIsRequired() {return this.isRequired;}

        public void setIsRequired(boolean isRequired) {this.isRequired = isRequired;}

        public String getDescription() {return this.description;}

        public void setDescription(@NotNull String value) {this.description = value;}

        @Nullable
        public String getDisplayName() {return this.displayName;}

        public void setDisplayName(@Nullable String value) {this.displayName = value;}

        public boolean getIsAutoCreate() {return this.isAutoCreate;}

        public void setIsAutoCreate(boolean value) {this.isAutoCreate = value;}

        public boolean getIsCollection() {return this.isCollection;}

        public void setIsCollection(boolean value) {this.isCollection = value;}

        public boolean isAvailableName(@NotNull String name) {

            var existingAttributeNames = this.attributes.stream()
              .map(Attribute::getName);
            var existingElementNames = this.elements.stream()
              .map(PatternElement::getName);
            var existingNames = Stream.concat(existingAttributeNames, existingElementNames);
            var reservedNames = AutomateConstants.ReservedElementNames.stream();
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

        public boolean isValidName(@Nullable String name) {

            if (name == null || name.isEmpty()) {
                return false;
            }
            return name.matches(AutomateConstants.ElementNameRegex);
        }

        public boolean isValidDisplayName(String displayName) {

            if (displayName == null || displayName.isEmpty()) {
                return true;
            }
            return displayName.matches(AutomateConstants.ElementDisplayNameRegex);
        }

        public boolean isValidDescription(String description) {

            if (description == null || description.isEmpty()) {
                return true;
            }
            return description.matches(AutomateConstants.ElementDescriptionNameRegex);
        }
    }
}
