//package jezzsantos.automate.plugin.application.interfaces.patterns;
//
//import org.junit.jupiter.api.Test;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//public class AutomationTests {
//
//    @Test
//    public void whenToStringAndCodeTemplateCommandAndIsOneOff_ThenReturnsString() {
//
//        var result = Automation.createCodeTemplateCommand("anid", "aname", "atemplateid", true, "atargetpath")
//          .toString();
//
//        assertEquals("aname (CodeTemplate Command) (template: atemplateid, onceonly, path: atargetpath)", result);
//    }
//
//    @Test
//    public void whenToStringAndCodeTemplateCommandAndIsNotOneOff_ThenReturnsString() {
//
//        var result = Automation.createCodeTemplateCommand("anid", "aname", "atemplateid", false, "atargetpath")
//          .toString();
//
//        assertEquals("aname (CodeTemplate Command) (template: atemplateid, always, path: atargetpath)", result);
//    }
//
//    @Test
//    public void whenToStringAndCliCommand_ThenReturnsString() {
//
//        var result = Automation.createCliCommand("anid", "aname", "anapplicationname", "arguments")
//          .toString();
//
//        assertEquals("aname (CLI Command) (app: anapplicationname, args: arguments)", result);
//    }
//
//    @Test
//    public void whenToStringAndCommandLaunchPointWithNoCommands_ThenReturnsString() {
//
//        var result = Automation.createLaunchPoint("anid", "aname", List.of())
//          .toString();
//
//        assertEquals("aname (LaunchPoint) (ids: none)", result);
//    }
//
//    @Test
//    public void whenToStringAndCommandLaunchPointWithCommands_ThenReturnsString() {
//
//        var result = Automation.createLaunchPoint("anid", "aname", List.of("acommandid1", "acommandid2", "acommandid3"))
//          .toString();
//
//        assertEquals("aname (LaunchPoint) (ids: acommandid1;acommandid2;acommandid3)", result);
//    }
//}
