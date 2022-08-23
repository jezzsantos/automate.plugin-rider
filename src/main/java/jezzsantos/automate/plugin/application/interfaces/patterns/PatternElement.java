package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("unused")
public class PatternElement {

    @SerializedName(value = "Id")
    private String id;
    @SerializedName(value = "Name")
    private String name;
    @SerializedName(value = "AutoCreate")
    private boolean autoCreate;
    @SerializedName(value = "IsCollection")
    private boolean isCollection;
    @SerializedName(value = "Cardinality")
    private ElementCardinality cardinality;
    @SerializedName(value = "CodeTemplates")
    private List<CodeTemplate> codeTemplates = new ArrayList<>();
    @SerializedName(value = "Automation")
    private List<Automation> automation = new ArrayList<>();
    @SerializedName(value = "Attributes")
    private List<Attribute> attributes = new ArrayList<>();
    @SerializedName(value = "Elements")
    private List<PatternElement> elements = new ArrayList<>();

    public PatternElement(@NotNull String id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return this.name;
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
        this.elements.sort(Comparator.comparing(PatternElement::getName));
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

        var type = this.isCollection
                ? "(collection)"
                : "";
        return String.format("%s %s", this.name, type);
    }

    public boolean isCollection() {
        return this.isCollection;
    }
}
