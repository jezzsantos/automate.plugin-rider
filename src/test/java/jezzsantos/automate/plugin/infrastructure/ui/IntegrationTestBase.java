package jezzsantos.automate.plugin.infrastructure.ui;

import com.jetbrains.rd.util.lifetime.LifetimeDefinition;
import com.jetbrains.rider.test.base.BaseTestWithSolution;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.AfterMethod;

import java.io.File;

public abstract class IntegrationTestBase extends BaseTestWithSolution {

    private LifetimeDefinition lifetimeDefinition;

    @AfterMethod(alwaysRun = true)
    public void tearDown() {

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
