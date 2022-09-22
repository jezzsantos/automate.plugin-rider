package jezzsantos.automate.plugin.infrastructure.ui.actions;

import com.jetbrains.rd.util.lifetime.LifetimeDefinition;
import com.jetbrains.rider.test.base.BaseTestWithSolution;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;

abstract class IntegrationTestBase extends BaseTestWithSolution {

    private LifetimeDefinition lifetimeDefinition;

    @BeforeMethod
    public void setUp() {

        super.setUpTestCaseShell();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

        super.tearDownTestCaseShell();
        this.lifetimeDefinition.terminate(true);
    }

    @Override
    protected boolean getWaitForCaches() {

        return true;
    }

    @Override
    protected void preprocessTempDirectory(@NotNull File tempDir) {

        super.preprocessTempDirectory(tempDir);
        this.lifetimeDefinition = new LifetimeDefinition();
    }
}
