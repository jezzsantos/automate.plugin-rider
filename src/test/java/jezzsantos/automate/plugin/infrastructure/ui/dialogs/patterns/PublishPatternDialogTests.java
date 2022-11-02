package jezzsantos.automate.plugin.infrastructure.ui.dialogs.patterns;

import jezzsantos.automate.plugin.application.interfaces.patterns.PatternDetailed;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternElement;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternVersion;
import jezzsantos.automate.plugin.common.AutomateBundle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PublishPatternDialogTests {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void whenDoValidateAndAutoVersion_ThenReturnsNull() {

        var context = new PublishPatternDialog.PublishPatternDialogContext(new PatternDetailed("anid", "aname", new PatternVersion("aversion"), new PatternElement()));

        var result = PublishPatternDialog.doValidate(context, true, "acustomversion");

        assertNull(result);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void whenDoValidateAndCustomVersionAndEmpty_ThenReturnsError() {

        var context = new PublishPatternDialog.PublishPatternDialogContext(new PatternDetailed("anid", "aname", new PatternVersion("aversion"), new PatternElement()));

        var result = PublishPatternDialog.doValidate(context, false, "");

        assertFalse(result.okEnabled);
        assertEquals(AutomateBundle.message("dialog.PublishPattern.Validation.CustomAndMissing.Message"), result.message);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void whenDoValidateAndCustomVersionAndNotASemVersion_ThenReturnsError() {

        var context = new PublishPatternDialog.PublishPatternDialogContext(new PatternDetailed("anid", "aname", new PatternVersion("aversion"), new PatternElement()));

        var result = PublishPatternDialog.doValidate(context, false, "notaversionnumber");

        assertFalse(result.okEnabled);
        assertEquals(AutomateBundle.message("dialog.PublishPattern.Validation.CustomAndInvalid.Message"), result.message);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void whenDoValidateAndCustomVersionAndOlderThanCurrent_ThenReturnsError() {

        var context = new PublishPatternDialog.PublishPatternDialogContext(new PatternDetailed("anid", "aname", new PatternVersion("0.0.1"), new PatternElement()));

        var result = PublishPatternDialog.doValidate(context, false, "0.0.0");

        assertFalse(result.okEnabled);
        assertEquals(AutomateBundle.message("dialog.PublishPattern.Validation.CustomAndOld.Message", "0.0.1"), result.message);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void whenDoValidateAndCustomVersionAndHasPrerelease_ThenReturnsError() {

        var context = new PublishPatternDialog.PublishPatternDialogContext(new PatternDetailed("anid", "aname", new PatternVersion("0.0.1"), new PatternElement()));

        var result = PublishPatternDialog.doValidate(context, false, "0.0.0-preview");

        assertFalse(result.okEnabled);
        assertEquals(AutomateBundle.message("dialog.PublishPattern.Validation.CustomAndPreRelease.Message"), result.message);
    }

    @Test
    public void whenDoValidateAndCustomVersionAndSameAsCurrent_ThenReturnsNull() {

        var context = new PublishPatternDialog.PublishPatternDialogContext(new PatternDetailed("anid", "aname", new PatternVersion("0.0.1"), new PatternElement()));

        var result = PublishPatternDialog.doValidate(context, false, "0.0.1");

        assertNull(result);
    }

    @Test
    public void whenDoValidateAndCustomVersionAndNewerAsCurrent_ThenReturnsNull() {

        var context = new PublishPatternDialog.PublishPatternDialogContext(new PatternDetailed("anid", "aname", new PatternVersion("0.0.1"), new PatternElement()));

        var result = PublishPatternDialog.doValidate(context, false, "0.0.2");

        assertNull(result);
    }
}
