package jezzsantos.automate.plugin.infrastructure.services.cli.responses;

import com.jetbrains.rd.util.UsedImplicitly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@UsedImplicitly
public class DictionaryStructuredOutput extends StructuredOutput<HashMap<String, Object>> {

    @UsedImplicitly
    public DictionaryStructuredOutput() {

        super(new ArrayList<>(List.of(new StructuredOutputOutput<>() {{
            this.Values = new HashMap<>();
        }})));
    }
}