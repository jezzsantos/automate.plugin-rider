package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;
import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.drafts.DraftElementSchema;
import jezzsantos.automate.plugin.infrastructure.AutomateBundle;
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
    private boolean autoCreate;
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

    public PatternElement(@NotNull String id, @NotNull String name) {

        this(id, name, AutomateConstants.ElementCardinality.One);
    }

    @TestOnly
    public PatternElement(@NotNull String id, @NotNull String name, AutomateConstants.ElementCardinality cardinality) {

        this.id = id;
        this.name = name;
        this.cardinality = cardinality;
        this.isCollection =
          cardinality == AutomateConstants.ElementCardinality.ZeroOrMany
            || cardinality == AutomateConstants.ElementCardinality.OneOrMany;
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

        return this.displayName != null
          ? this.displayName
          : this.name;
    }

    @TestOnly
    public void setDisplayName(@NotNull String text) {

        this.displayName = text;
    }

    @NotNull
    public String getEditPath() {

        return this.editPath;
    }

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

    public void removeAttribute(@NotNull Attribute attribute) {

        this.attributes.remove(attribute);
    }

    public void addElement(@NotNull PatternElement element) {

        this.elements.add(element);
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

    public boolean isCollection() {

        return this.isCollection;
    }

    @NotNull
    public DraftElementSchema findSchema(@NotNull String schemaId) {

        var schema = findSchema(this, schemaId);
        if (schema == null) {
            throw new RuntimeException(AutomateBundle.message("exception.PatternElement.FindSchema.NotExists.Message", schemaId));
        }

        return schema;
    }

    private static String toCardinalityString(AutomateConstants.ElementCardinality cardinality) {

        if (cardinality == null) {
            return "";
        }
        switch (cardinality) {
            case One:
            case OneOrMany:
                return AutomateBundle.message("general.PatternElement.Cardinality.Required.Title");
            case ZeroOrOne:
            case ZeroOrMany:
                return AutomateBundle.message("general.PatternElement.Cardinality.Optional.Title");
            default:
                return "";
        }
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
