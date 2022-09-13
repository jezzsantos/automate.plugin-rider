package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ShowSettingsMenuActionTests extends BasePlatformTestCase {

    @BeforeEach
    @Override
    public void setUp() throws Exception {

        super.setUp();
    }

    @AfterEach
    @Override
    public void tearDown() throws Exception {

        super.tearDown();
    }

    @Test
    @Disabled
    public void whenConstructed_ThenIsEnabledAndVisible() {

        var presentation = this.myFixture.testAction(new ShowSettingsMenuAction());

        assertTrue(presentation.isEnabledAndVisible());
    }
}
