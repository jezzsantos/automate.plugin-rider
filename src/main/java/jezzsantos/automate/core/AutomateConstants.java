package jezzsantos.automate.core;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AutomateConstants {

    public static final String ElementDisplayNameRegex = "^.+$";
    public static final String ElementDescriptionNameRegex = "^.+$";
    public static final List<AutomateConstants.AttributeDataType> AttributeDataTypes =
      List.of(AttributeDataType.STRING, AttributeDataType.BOOLEAN, AttributeDataType.INTEGER, AttributeDataType.FLOAT, AttributeDataType.DATETIME);
    public static final String OutputStructuredOptionShorthand = "--os";
    public static final List<String> OutputStructuredOptionAliases = List.of(OutputStructuredOptionShorthand, "--output-structured");
    public static final String UsageCorrelationOption = "--usage-correlation";
    public static final String UsageAllowedOption = "--collect-usage";
    private static final String NameIdentifierRegex = "^[a-zA-Z\\d_\\.\\-]+$";
    public static final String PatternNameRegex = NameIdentifierRegex;
    public static final String DraftNameRegex = NameIdentifierRegex;
    public static final String AttributeNameRegex = NameIdentifierRegex;
    public static final String ElementNameRegex = NameIdentifierRegex;
    public static final String CodeTemplateNameRegex = NameIdentifierRegex;
    public static final String AutomationNameRegex = NameIdentifierRegex;
    public static String ExecutableName = "automate";
    public static String ApplicationInsightsCliRoleName = "automate CLI";
    public static String ToolkitFileExtension = "toolkit";
    public static String MinimumSupportedVersion = "1.3.1";
    public static List<String> ReservedAttributeNames = List.of("Id", "Parent", "DisplayName", "Description", "ConfigurePath", "Schema", "Items");
    public static List<String> ReservedElementNames = ReservedAttributeNames;
    public static List<String> ReservedCodeTemplateNames = List.of("Id", "Parent");
    public static List<String> ReservedAutomationNames = List.of("Id", "Parent");
    public static String InstallationInstructionsUrl = "https://jezzsantos.github.io/automate/installation-rider-plugin";
    public static String AuthoringHelpUrl = "https://jezzsantos.github.io/automate/authoring-plugin/";
    public static String RuntimeHelpUrl = "https://jezzsantos.github.io/automate/runtime-plugin/";
    public static String TemplatingExpressionsUrl = "https://jezzsantos.github.io/automate/reference/#templating-expressions";

    public enum SchemaType {
        @SerializedName("None")
        NONE("None", "None"),
        @SerializedName("Pattern")
        PATTERN("Pattern", "Pattern"),
        @SerializedName("Element")
        ELEMENT("Element", "Element"),
        @SerializedName("EphemeralCollection")
        EPHEMERALCOLLECTION("EphemeralCollection", "Collection"),
        @SerializedName("CollectionItem")
        COLLECTIONITEM("CollectionItem", "Collection Item"),
        @SerializedName("Attribute")
        ATTRIBUTE("Attribute", "Attribute");
        private final String value;
        private final String displayName;

        SchemaType(String value, String displayName) {

            this.value = value;
            this.displayName = displayName;
        }

        @Override
        public String toString() {

            return this.displayName;
        }

        @SuppressWarnings("unused")
        public String getDisplayName() {return this.displayName;}

        public String getValue() {return this.value;}
    }

    public enum ElementCardinality {
        @SerializedName("One")
        ONE,
        @SerializedName("ZeroOrOne")
        ZERO_OR_ONE,
        @SerializedName("ZeroOrMany")
        ZERO_OR_MANY,
        @SerializedName("OneOrMany")
        ONE_OR_MANY,
    }

    public enum AttributeDataType {
        @SerializedName("string")
        STRING("string", "string"),
        @SerializedName("bool")
        BOOLEAN("bool", "boolean"),
        @SerializedName("int")
        INTEGER("int", "integer"),
        @SerializedName("float")
        FLOAT("float", "number"),
        @SerializedName("datetime")
        DATETIME("datetime", "date and time");
        private final String displayName;
        private final String value;

        AttributeDataType(String value, String displayName) {

            this.value = value;
            this.displayName = displayName;
        }

        @Override
        public String toString() {

            return this.displayName;
        }

        public String getValue() {return this.value;}

        public String getDisplayName() {return this.displayName;}
    }

    public enum AutomationType {
        @SerializedName("CodeTemplateCommand")
        CODE_TEMPLATE_COMMAND("CodeTemplate Command"),
        @SerializedName("CliCommand")
        CLI_COMMAND("CLI Command"),
        @SerializedName("CommandLaunchPoint")
        COMMAND_LAUNCH_POINT("LaunchPoint");
        private final String displayName;

        AutomationType(String displayName) {

            this.displayName = displayName;
        }

        @Override
        public String toString() {

            return this.displayName;
        }

        public String getDisplayName() {return this.displayName;}
    }

    public enum CommandExecutionLogItemType {

        @SerializedName("Succeeded")
        SUCCEEDED("Succeeded"),
        @SerializedName("Warning")
        WARNING("Warning"),
        @SerializedName("Failed")
        FAILED("Failed");
        private final String displayName;

        CommandExecutionLogItemType(String displayName) {

            this.displayName = displayName;
        }

        @Override
        public String toString() {

            return this.displayName;
        }
    }

    public enum UpgradeLogType {
        @SerializedName("Abort")
        ABORT("Abort"),
        @SerializedName("NonBreaking")
        NON_BREAKING("Non-Breaking"),
        @SerializedName("Breaking")
        BREAKING("Breaking");
        private final String displayName;

        UpgradeLogType(String displayName) {

            this.displayName = displayName;
        }

        @Override
        public String toString() {

            return this.displayName;
        }
    }

    public enum DraftToolkitVersionCompatibility {

        @SerializedName("Compatible")
        COMPATIBLE("Compatible"),
        @SerializedName("DraftAheadOfToolkit")
        DRAFT_AHEADOF_TOOLKIT("Draft ahead of Toolkit"),
        @SerializedName("ToolkitAheadOfDraft")
        TOOLKIT_AHEADOF_DRAFT("Toolkit ahead of Draft");

        private final String displayName;

        DraftToolkitVersionCompatibility(String displayName) {

            this.displayName = displayName;
        }

        @Override
        public String toString() {

            return this.displayName;
        }
    }

    public enum ToolkitRuntimeVersionCompatibility {

        @SerializedName("Compatible")
        COMPATIBLE("Compatible"),
        @SerializedName("MachineAheadOfToolkit")
        MACHINE_AHEADOF_TOOLKIT("Machine ahead of Toolkit"),
        @SerializedName("ToolkitAheadOfMachine")
        TOOLKIT_AHEADOF_MACHINE("Toolkit ahead of Machine");
        private final String displayName;

        ToolkitRuntimeVersionCompatibility(String displayName) {

            this.displayName = displayName;
        }

        @Override
        public String toString() {

            return this.displayName;
        }
    }

    public enum PatternVersionChange {
        @SerializedName("NoChange")
        NO_CHANGE("NoChange"),
        @SerializedName("NonBreaking")
        NON_BREAKING("Non-breaking"),
        @SerializedName("Breaking")
        BREAKING("Breaking");
        private final String displayName;

        PatternVersionChange(String displayName) {

            this.displayName = displayName;
        }

        @Override
        public String toString() {

            return this.displayName;
        }
    }
}
