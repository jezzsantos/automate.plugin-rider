package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class PatternTree {
    @SerializedName(value = "Id")
    private String id;
    @SerializedName(value = "Name")
    private String name;
    @SerializedName(value = "CodeTemplates")
    private List<CodeTemplate> codeTemplates = new ArrayList<>();
    @SerializedName(value = "Automation")
    private List<Automation> automation = new ArrayList<>();
    @SerializedName(value = "Attributes")
    private List<Attributes> attributes = new ArrayList<>();
    @SerializedName(value = "Elements")
    private List<Elements> elements = new ArrayList<>();
}
