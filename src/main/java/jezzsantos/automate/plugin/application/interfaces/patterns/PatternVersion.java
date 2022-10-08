package jezzsantos.automate.plugin.application.interfaces.patterns;

import com.google.gson.annotations.SerializedName;
import com.jetbrains.rd.util.UsedImplicitly;
import jezzsantos.automate.core.AutomateConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

@SuppressWarnings("unused")
public class PatternVersion {

    @SerializedName(value = "Current")
    private String current;
    @SerializedName(value = "Next")
    private String next;
    @SerializedName(value = "Change")
    private AutomateConstants.PatternVersionChange change;

    @UsedImplicitly
    public PatternVersion() {}

    @TestOnly
    public PatternVersion(@NotNull String version) {

        this.current = version;
        this.next = version;
        this.change = AutomateConstants.PatternVersionChange.NO_CHANGE;
    }

    public String getCurrent() {return this.current;}

    public String getNext() {return this.next;}
}
