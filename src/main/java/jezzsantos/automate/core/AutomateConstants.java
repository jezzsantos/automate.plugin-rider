package jezzsantos.automate.core;

import java.util.ArrayList;
import java.util.List;

public class AutomateConstants {

    public static final String PatternNameRegex = "^[a-zA-Z\\d_\\.\\-]+$";
    public static final String DraftNameRegex = "^[a-zA-Z\\d_\\.\\-]+$";
    public static final String AttributeNameRegex = "^[a-zA-Z\\d_\\.\\-]+$";
    public static final List<String> AttributeDataTypes = new ArrayList<>(List.of("string", "bool", "int", "float", "datetime"));
    public static final String OutputStructuredShorthand = "--os";
    public static final List<String> OutputStructuredAliases = List.of(OutputStructuredShorthand, "--output-structured");
    public static String ExecutableName = "automate";
    public static String ToolkitFileExtension = "toolkit";

    public enum ElementCardinality {
        One,
        ZeroOrOne,
        ZeroOrMany,
        OneOrMany,
    }

    public enum AttributeDataType {
        STRING("string"),
        BOOL("bool"),
        INT("int"),
        FLOAT("float"),
        DATETIME("datetime");
        private final String displayName;

        AttributeDataType(String displayName) {

            this.displayName = displayName;
        }

        @Override
        public String toString() {

            return this.displayName;
        }
    }

    public enum AutomationType {
        CodeTemplateCommand,
        CliCommand,
        CommandLaunchPoint
    }
}
