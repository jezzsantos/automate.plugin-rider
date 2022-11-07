package jezzsantos.automate.plugin.infrastructure.ui.dialogs.patterns;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.util.text.SemVer;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternDetailed;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import javax.swing.*;
import java.util.Objects;

public class PublishPatternDialog extends DialogWrapper {

    private final PublishPatternDialogContext context;
    private JPanel contents;
    private JCheckBox installLocally;
    private JCheckBox autoVersion;
    private JTextField version;

    public PublishPatternDialog(@Nullable Project project, @NotNull PublishPatternDialogContext context) {

        super(project);
        this.context = context;
        this.init();
        this.setTitle(AutomateBundle.message("dialog.PublishPattern.Title"));

        this.installLocally.setText(AutomateBundle.message("dialog.PublishPattern.InstallLocally.Title"));
        this.installLocally.setSelected(context.getInstallLocally());
        this.autoVersion.setSelected(context.isAutoVersion());
        initAutoVersion(this.context.isAutoVersion());
        this.autoVersion.addActionListener(e -> {
            var isAutoVersion = this.autoVersion.isSelected();
            initAutoVersion(isAutoVersion);
        });
    }

    @TestOnly
    @Nullable
    public static ValidationInfo doValidate(PublishPatternDialog.PublishPatternDialogContext context, boolean isAutoVersion, @NotNull String customVersion) {

        if (isAutoVersion) {
            return null;
        }

        if (customVersion.isEmpty()) {
            return new ValidationInfo(AutomateBundle.message("dialog.PublishPattern.Validation.CustomAndMissing.Message"));
        }

        var version = SemVer.parseFromText(customVersion);
        if (version == null) {
            return new ValidationInfo(AutomateBundle.message("dialog.PublishPattern.Validation.CustomAndInvalid.Message"));
        }

        if (version.getPreRelease() != null) {
            return new ValidationInfo(AutomateBundle.message("dialog.PublishPattern.Validation.CustomAndPreRelease.Message"));
        }

        var currentVersion = context.getCurrentVersion();
        if (version.compareTo(currentVersion) < 0) {
            return new ValidationInfo(AutomateBundle.message("dialog.PublishPattern.Validation.CustomAndOld.Message", currentVersion.toString()));
        }

        return null;
    }

    public PublishPatternDialogContext getContext() {return this.context;}

    @Override
    protected @Nullable ValidationInfo doValidate() {

        var isAutoVersion = this.autoVersion.isSelected();
        var customVersion = this.version.getText();
        return doValidate(this.context, isAutoVersion, customVersion);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {

        return this.contents;
    }

    @Override
    protected void doOKAction() {

        super.doOKAction();
        this.context.setInstallLocally(this.installLocally.isSelected());
        if (!this.autoVersion.isSelected()) {
            this.context.setCustomVersion(this.version.getText());
        }
    }

    private void initAutoVersion(boolean isAutoVersion) {

        var nextVersion = this.context.getNextVersion().toString();
        if (isAutoVersion) {
            this.autoVersion.setText(AutomateBundle.message("dialog.PublishPattern.Version.AutoVersion.Title"));
            this.version.setText(nextVersion);
            this.version.setEnabled(false);
        }
        else {
            this.autoVersion.setText(AutomateBundle.message("dialog.PublishPattern.Version.CustomVersion.Title"));
            var customVersion = this.context.getCustomVersion();
            this.version.setText(customVersion == null || customVersion.isEmpty()
                                   ? nextVersion
                                   : customVersion);
            this.version.setEnabled(true);
            this.version.selectAll();
            this.version.grabFocus();
        }
    }

    public static class PublishPatternDialogContext {

        private final PatternDetailed pattern;
        private boolean installLocally;
        private boolean autoVersion;
        @Nullable
        private String customVersion;

        public PublishPatternDialogContext(PatternDetailed pattern) {

            this.pattern = pattern;
            this.installLocally = true;
            this.autoVersion = true;
            this.customVersion = null;
        }

        public boolean getInstallLocally() {return this.installLocally;}

        public void setInstallLocally(boolean installLocally) {this.installLocally = installLocally;}

        public boolean isAutoVersion() {

            if (this.customVersion == null) {
                return this.autoVersion;
            }
            else {
                return false;
            }
        }

        @Nullable
        public String getCustomVersion() {

            if (this.autoVersion) {
                return null;
            }

            return this.customVersion;
        }

        public void setCustomVersion(@NotNull String customVersion) {

            this.autoVersion = false;
            this.customVersion = customVersion;
        }

        @NotNull
        public SemVer getNextVersion() {return Objects.requireNonNull(SemVer.parseFromText(this.pattern.getVersion().getNext()));}

        @NotNull
        public SemVer getCurrentVersion() {return Objects.requireNonNull(SemVer.parseFromText(this.pattern.getVersion().getCurrent()));}
    }
}
