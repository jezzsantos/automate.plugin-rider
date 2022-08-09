package jezzsantos.automate.plugin.infrastructure;

public class AutomateService implements IAutomateService {

    public AutomateService() {
    }

    @Override
    public String tryGetExecutableVersion(String executablePath) {
        //TODO: run executable and get --version
        return "0.3.7-preview";
    }
}
