package jezzsantos.automate.plugin.application.interfaces;

import java.util.ArrayList;

class StructureOutputError {
    public String Message;
}

public class StructuredOutput<TValues> {
    public ArrayList<String> Info;
    public ArrayList<StructureOutputOutput<TValues>> Output;
    public StructureOutputError Error;
}
