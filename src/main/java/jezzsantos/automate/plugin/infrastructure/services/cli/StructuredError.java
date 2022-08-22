package jezzsantos.automate.plugin.infrastructure.services.cli;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unused")
public class StructuredError {

    public ArrayList<String> Info;
    public StructureOutputError Error;
    public ArrayList<StructureOutputOutput<HashMap<String, Object>>> Output;

    public String getErrorMessage() {
        return String.format("%s", Error.Message);
    }
}
