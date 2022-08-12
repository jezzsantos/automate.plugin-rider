package jezzsantos.automate.plugin.infrastructure.common;

import java.util.ArrayList;

class StructureOutputError {
    public String Message;
}

class StructureOutputOutput<TValues> {
    public String Message;
    public TValues Values;
}

public class StructuredOutput<TValues> {
    public ArrayList<String> Info;
    public ArrayList<StructureOutputOutput<TValues>> Output;
    public StructureOutputError Error;
}

