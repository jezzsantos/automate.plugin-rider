package jezzsantos.automate.plugin.application.interfaces.drafts;

import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.patterns.Automation;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("ClassCanBeRecord")
public class DraftElementSchema {

    @NotNull
    private final PatternElement schema;

    public DraftElementSchema(@NotNull PatternElement schema) {

        this.schema = schema;
    }

    @NotNull
    public PatternElement getSchema() {

        return this.schema;
    }

    @NotNull
    public List<PatternElement> listMissingElements(@NotNull DraftElement draftElement) {

        return this.schema.getElements().stream()
          .filter(childSchema -> !singularInstanceAlreadyExists(childSchema, draftElement))
          .collect(Collectors.toList());
    }

    @NotNull
    public List<Automation> listLaunchPoints() {

        return this.schema.getAutomation().stream()
          .filter(automation -> automation.getType() == AutomateConstants.AutomationType.COMMAND_LAUNCH_POINT)
          .collect(Collectors.toList());
    }

    private boolean singularInstanceAlreadyExists(@NotNull PatternElement childSchema, @NotNull DraftElement parentDraftElement) {

        var cardinality = childSchema.getCardinality();
        if (cardinality == AutomateConstants.ElementCardinality.ONE
          || cardinality == AutomateConstants.ElementCardinality.ZERO_OR_ONE) {
            var schemaId = childSchema.getId();
            var properties = parentDraftElement.getElements();
            for (var property : properties) {
                var propertySchemaId = property.getSchemaId();
                if (propertySchemaId != null
                  && propertySchemaId.equals(schemaId)) {
                    return true;
                }
            }
        }
        return false;
    }
}
