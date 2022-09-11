package jezzsantos.automate.plugin.application.interfaces.drafts;

import jezzsantos.automate.core.AutomateConstants;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.List;
import java.util.stream.Collectors;

public class DraftElementSchema {

    @NotNull
    private final PatternElement schema;

    public DraftElementSchema(@NotNull PatternElement schema) {

        this.schema = schema;
    }

    @NotNull
    @TestOnly
    public PatternElement getSchema() {

        return this.schema;
    }

    @NotNull
    public List<PatternElement> listMissingElements(@NotNull DraftElement draftElement) {

        return this.schema.getElements().stream()
          .filter(childSchema -> !singularInstanceAlreadyExists(childSchema, draftElement))
          .collect(Collectors.toList());

    }

    private boolean singularInstanceAlreadyExists(@NotNull PatternElement childSchema, @NotNull DraftElement parentDraftElement) {

        var cardinality = childSchema.getCardinality();
        if (cardinality == AutomateConstants.ElementCardinality.One
          || cardinality == AutomateConstants.ElementCardinality.ZeroOrOne) {
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
