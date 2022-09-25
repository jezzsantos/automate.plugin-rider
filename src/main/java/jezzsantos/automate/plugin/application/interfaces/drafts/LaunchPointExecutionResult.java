package jezzsantos.automate.plugin.application.interfaces.drafts;

import jezzsantos.automate.core.AutomateConstants;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LaunchPointExecutionResult {

    private final ArrayList<LaunchPointValidationError> validationErrors;
    private ArrayList<LaunchPointExecutionItem> executionItems;
    private boolean success;

    public LaunchPointExecutionResult() {

        this.executionItems = new ArrayList<>();
        this.validationErrors = new ArrayList<>();
        this.success = true;
    }

    public static LaunchPointExecutionResult failure(@NotNull String message) {

        var result = new LaunchPointExecutionResult();
        result.success = false;
        result.executionItems = new ArrayList<>() {{
            add(new LaunchPointExecutionItem(AutomateConstants.CommandExecutionLogItemType.FAILED, message));
        }};
        return result;
    }

    public void addLog(@NotNull AutomateConstants.CommandExecutionLogItemType type, @NotNull String message) {

        this.executionItems.add(new LaunchPointExecutionItem(type, message));
        if (this.success) {
            if (type == AutomateConstants.CommandExecutionLogItemType.FAILED) {
                this.success = false;
            }
        }
    }

    public void addValidation(@NotNull String path, @NotNull String message) {

        this.validationErrors.add(new LaunchPointValidationError(path, message));
        this.success = false;
    }

    public boolean isSuccess() {return this.success;}

    public boolean hasValidationErrors() {return !this.success && !this.validationErrors.isEmpty();}

    public List<LaunchPointExecutionItem> getExecutionItems() {return this.executionItems;}

    public List<LaunchPointValidationError> getValidationErrors() {return this.validationErrors;}

    public static class LaunchPointExecutionItem {

        private final String message;
        private final AutomateConstants.CommandExecutionLogItemType type;

        public LaunchPointExecutionItem(@NotNull AutomateConstants.CommandExecutionLogItemType type, @NotNull String message) {

            this.type = type;
            this.message = message;
        }

        public String getMessage() {return this.message;}

        public AutomateConstants.CommandExecutionLogItemType getType() {return this.type;}
    }

    @SuppressWarnings("ClassCanBeRecord")
    public static class LaunchPointValidationError {

        private final String message;
        private final String path;

        public LaunchPointValidationError(@NotNull String path, @NotNull String message) {

            this.path = path;
            this.message = message;
        }

        public String getMessage() {return this.message;}

        public String getPath() {return this.path;}
    }
}
