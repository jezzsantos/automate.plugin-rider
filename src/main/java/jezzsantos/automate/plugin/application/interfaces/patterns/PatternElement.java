package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElementSchema;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("unused")
public class PatternElement {

    @SerializedName(value = "Id")
    private String id;
    @SerializedName(value = "Name")
    private String name;
    @SerializedName(value = "DisplayName")
    private String displayName;
    @SerializedName(value = "Description")
    private String description;
    @SerializedName(value = "EditPath")
    private String editPath;
    private boolean isRoot = false;
    @SerializedName(value = "AutoCreate")
    private boolean isAutoCreate;
    @SerializedName(value = "IsCollection")
    private boolean isCollection;
    @SerializedName(value = "Cardinality")
    private AutomateConstants.ElementCardinality cardinality;
    @SerializedName(value = "CodeTemplates")
    private List<CodeTemplate> codeTemplates = new ArrayList<>();
    @SerializedName(value = "Automation")
    private List<Automation> automation = new ArrayList<>();
    @SerializedName(value = "Attributes")
    private List<Attribute> attributes = new ArrayList<>();
    @SerializedName(value = "Elements")
    private List<PatternElement> elements = new ArrayList<>();

    @UsedImplicitly()
    public PatternElement() {}

    public PatternElement(@NotNull String id, @NotNull String name) {

        this(id, name, AutomateConstants.ElementCardinality.ONE);
    }

    @TestOnly
    public PatternElement(@NotNull String id, @NotNull String name, AutomateConstants.ElementCardinality cardinality) {

        this(id, name, null, null, null, cardinality, true);
    }

    public PatternElement(@NotNull String id, @NotNull String name, @Nullable String editPath, @Nullable String displayName, @Nullable String description, @NotNull AutomateConstants.ElementCardinality cardinality, boolean autoCreate) {

        this.id = id;
        this.name = name;
        this.editPath = editPath;
        this.displayName = displayName;
        this.description = description;
        this.cardinality = cardinality;
        this.isCollection =
          cardinality == AutomateConstants.ElementCardinality.ZERO_OR_MANY
            || cardinality == AutomateConstants.ElementCardinality.ONE_OR_MANY;
        this.isAutoCreate = autoCreate;
        this.isRoot = false;
    }

    public void setRoot() {

        this.isRoot = true;
    }

    public boolean isRoot() {

        return this.isRoot;
    }

    @NotNull
    public String getId() {

        return this.id;
    }

    @NotNull
    public String getName() {

        return this.name;
    }

    @NotNull
    public String getDisplayName() {

        return (this.displayName != null && !this.displayName.isEmpty())
          ? this.displayName
          : this.name;
    }

    @TestOnly
    public void setDisplayName(@NotNull String text) {

        this.displayName = text;
    }

    @NotNull
    public String getDescription() {

        return (this.description != null && !this.description.isEmpty())
          ? this.description
          : this.name;
    }

    @TestOnly
    public void setDescription(@NotNull String text) {

        this.description = text;
    }

    @NotNull
    public String getEditPath() {

        return this.editPath;
    }

    @Nullable
    public AutomateConstants.ElementCardinality getCardinality() {

        return this.cardinality;
    }

    @NotNull
    public List<CodeTemplate> getCodeTemplates() {

        this.codeTemplates.sort(Comparator.comparing(CodeTemplate::getName));
        return this.codeTemplates;
    }

    @NotNull
    public List<Automation> getAutomation() {

        this.automation.sort(Comparator.comparing(Automation::getName));
        return this.automation;
    }

    @NotNull
    public List<Attribute> getAttributes() {

        this.attributes.sort(Comparator.comparing(Attribute::getName));
        return this.attributes;
    }

    @NotNull
    public List<PatternElement> getElements() {

        this.elements.sort(Comparator.comparing(PatternElement::getDisplayName));
        return this.elements;
    }

    public void addCodeTemplate(@NotNull CodeTemplate codeTemplate) {

        this.codeTemplates.add(codeTemplate);
    }

    public void addAutomation(@NotNull Automation automation) {

        this.automation.add(automation);
    }

    public void addAttribute(@NotNull Attribute attribute) {

        this.attributes.add(attribute);
    }

    public void updateAttribute(@NotNull Attribute attribute) {

        var id = attribute.getId();
        var oldAttribute = this.attributes.stream()
          .filter(attr -> attr.getId().equals(id))
          .findFirst();
        if (oldAttribute.isEmpty()) {
            return;
        }

        var indexOfAttribute = this.attributes.indexOf(oldAttribute.get());
        this.attributes.set(indexOfAttribute, attribute);
    }

    public void removeAttribute(@NotNull Attribute attribute) {

        this.attributes.remove(attribute);
    }

    public void addElement(@NotNull PatternElement element) {

        this.elements.add(element);
    }

    public void updateElement(@NotNull PatternElement element) {

        var id = element.getId();
        var oldElement = this.elements.stream()
          .filter(ele -> ele.getId().equals(id))
          .findFirst();
        if (oldElement.isEmpty()) {
            return;
        }

        var indexOfElement = this.elements.indexOf(oldElement.get());
        this.elements.set(indexOfElement, element);
    }

    public void removeElement(@NotNull PatternElement element) {

        this.elements.remove(element);
    }

    public boolean isCollection() {

        return this.isCollection;
    }

    public boolean isAutoCreate() {

        return this.isAutoCreate;
    }

    @NotNull
    public DraftElementSchema findSchema(@NotNull String schemaId) {

        var schema = findSchema(this, schemaId);
        if (schema == null) {
            throw new RuntimeException(AutomateBundle.message("exception.PatternElement.FindSchema.NotExists.Message", schemaId));
        }

        return schema;
    }

    @Override
    public String toString() {

        var cardinality = toCardinalityString(this.cardinality);
        var type = this.isRoot
          ? ""
          : this.isCollection
            ? String.format("(collection, %s)", cardinality)
            : String.format("(%s)", cardinality);
        return String.format("%s %s", this.name, type);
    }

    private static String toCardinalityString(@Nullable AutomateConstants.ElementCardinality cardinality) {

        if (cardinality == null) {
            return "";
        }
        return switch (cardinality) {
            case ONE, ONE_OR_MANY -> AutomateBundle.message("general.PatternElement.Cardinality.Required.Title");
            case ZERO_OR_ONE, ZERO_OR_MANY -> AutomateBundle.message("general.PatternElement.Cardinality.Optional.Title");
        };
    }

    @Nullable
    private DraftElementSchema findSchema(@NotNull PatternElement patternElement, @NotNull String schemaId) {

        if (patternElement.getId().equals(schemaId)) {
            return new DraftElementSchema(patternElement);
        }

        var elements = patternElement.getElements();
        for (var element : elements) {
            var schema = findSchema(element, schemaId);
            if (schema != null) {
                return schema;
            }
        }

        return null;
    }
}
