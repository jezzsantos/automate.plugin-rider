package jezzsantos.automate.plugin.infrastructure.ui;

import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.never;

public class AutomateToolWindowFactoryTests {

    @Nested
    class GivenNoPatterns {

        @Nested
        class GivenNoAuthoringMode {

            @Nested
            class GivenNoToolkits {

                @Test
                public void whenInitStartupState_ThenResetsToDraftMode() {

                    var application = Mockito.mock(IAutomateApplication.class);
                    Mockito.when(application.refreshLocalState())
                      .thenReturn(new AllStateLite());
                    Mockito.when(application.isAuthoringMode())
                      .thenReturn(false);

                    AutomateToolWindowFactory.initStartupState(application);

                    Mockito.verify(application).setEditingMode(EditingMode.Drafts);
                    Mockito.verify(application, never()).setAuthoringMode(anyBoolean());
                }
            }

            @Nested
            class GivenSomeInstalledToolkits {

                @Test
                public void whenInitStartupState_ThenResetsToDraftMode() {

                    var application = Mockito.mock(IAutomateApplication.class);
                    Mockito.when(application.refreshLocalState())
                      .thenReturn(new AllStateLite(
                        List.of(),
                        List.of(new ToolkitLite("anid", "aname", "aversion")),
                        List.of()));
                    Mockito.when(application.isAuthoringMode())
                      .thenReturn(false);

                    AutomateToolWindowFactory.initStartupState(application);

                    Mockito.verify(application).setEditingMode(EditingMode.Drafts);
                    Mockito.verify(application, never()).setAuthoringMode(anyBoolean());
                }
            }

        }

        @Nested
        class GivenAuthoringMode {

            @Nested
            class GivenNoToolkits {

                @Test
                public void whenInitStartupState_ThenResetsToDraftMode() {

                    var application = Mockito.mock(IAutomateApplication.class);
                    Mockito.when(application.refreshLocalState())
                      .thenReturn(new AllStateLite());
                    Mockito.when(application.isAuthoringMode())
                      .thenReturn(true);

                    AutomateToolWindowFactory.initStartupState(application);

                    Mockito.verify(application).setEditingMode(EditingMode.Drafts);
                    Mockito.verify(application).setAuthoringMode(false);
                }
            }

            @Nested
            class GivenSomeInstalledToolkits {

                @Test
                public void whenInitStartupState_ThenResetsToDraftMode() {

                    var application = Mockito.mock(IAutomateApplication.class);
                    Mockito.when(application.refreshLocalState())
                      .thenReturn(new AllStateLite(
                        List.of(),
                        List.of(new ToolkitLite("anid", "aname", "aversion")),
                        List.of()));
                    Mockito.when(application.isAuthoringMode())
                      .thenReturn(true);

                    AutomateToolWindowFactory.initStartupState(application);

                    Mockito.verify(application).setEditingMode(EditingMode.Drafts);
                    Mockito.verify(application).setAuthoringMode(false);
                }
            }

        }

    }

    @Nested
    class GivenSomePatterns {

        @Nested
        class GivenNoAuthoringMode {

            @Nested
            class GivenNoToolkits {

                @Test
                public void whenInitStartupState_ThenResetsToPatternMode() {

                    var application = Mockito.mock(IAutomateApplication.class);
                    Mockito.when(application.refreshLocalState())
                      .thenReturn(new AllStateLite(
                        List.of(new PatternLite("anid", "aname", "aversion", false)),
                        List.of(),
                        List.of()));
                    Mockito.when(application.isAuthoringMode())
                      .thenReturn(false);

                    AutomateToolWindowFactory.initStartupState(application);

                    Mockito.verify(application).setEditingMode(EditingMode.Patterns);
                    Mockito.verify(application).setAuthoringMode(true);
                }
            }

            @Nested
            class GivenSomeInstalledToolkits {

                @Test
                public void whenInitStartupStateAndEditingDrafts_ThenDoesNothing() {

                    var application = Mockito.mock(IAutomateApplication.class);
                    Mockito.when(application.refreshLocalState())
                      .thenReturn(new AllStateLite(
                        List.of(new PatternLite("anid", "aname", "aversion", false)),
                        List.of(new ToolkitLite("anid", "aname", "aversion")),
                        List.of()));
                    Mockito.when(application.getEditingMode())
                      .thenReturn(EditingMode.Drafts);
                    Mockito.when(application.isAuthoringMode())
                      .thenReturn(false);

                    AutomateToolWindowFactory.initStartupState(application);

                    Mockito.verify(application, never()).setEditingMode(any());
                    Mockito.verify(application, never()).setAuthoringMode(anyBoolean());
                }

                @Test
                public void whenInitStartupStateAndEditingPatterns_ThenDoesNothing() {

                    var application = Mockito.mock(IAutomateApplication.class);
                    Mockito.when(application.refreshLocalState())
                      .thenReturn(new AllStateLite(
                        List.of(new PatternLite("anid", "aname", "aversion", false)),
                        List.of(new ToolkitLite("anid", "aname", "aversion")),
                        List.of()));
                    Mockito.when(application.getEditingMode())
                      .thenReturn(EditingMode.Patterns);
                    Mockito.when(application.isAuthoringMode())
                      .thenReturn(false);

                    AutomateToolWindowFactory.initStartupState(application);

                    Mockito.verify(application, never()).setEditingMode(any());
                    Mockito.verify(application, never()).setAuthoringMode(anyBoolean());
                }
            }

        }

        @Nested
        class GivenAuthoringMode {

            @Nested
            class GivenNoToolkits {

                @Test
                public void whenInitStartupState_ThenResetsToDraftMode() {

                    var application = Mockito.mock(IAutomateApplication.class);
                    Mockito.when(application.refreshLocalState())
                      .thenReturn(new AllStateLite(
                        List.of(new PatternLite("anid", "aname", "aversion", false)),
                        List.of(),
                        List.of()));
                    Mockito.when(application.isAuthoringMode())
                      .thenReturn(true);

                    AutomateToolWindowFactory.initStartupState(application);

                    Mockito.verify(application).setEditingMode(EditingMode.Patterns);
                    Mockito.verify(application, never()).setAuthoringMode(anyBoolean());
                }
            }

            @Nested
            class GivenSomeInstalledToolkits {

                @Test
                public void whenInitStartupStateAndEditingDrafts_ThenDoesNothing() {

                    var application = Mockito.mock(IAutomateApplication.class);
                    Mockito.when(application.refreshLocalState())
                      .thenReturn(new AllStateLite(
                        List.of(new PatternLite("anid", "aname", "aversion", false)),
                        List.of(new ToolkitLite("anid", "aname", "aversion")),
                        List.of()));
                    Mockito.when(application.getEditingMode())
                      .thenReturn(EditingMode.Drafts);
                    Mockito.when(application.isAuthoringMode())
                      .thenReturn(true);

                    AutomateToolWindowFactory.initStartupState(application);

                    Mockito.verify(application, never()).setEditingMode(any());
                    Mockito.verify(application, never()).setAuthoringMode(anyBoolean());
                }

                @Test
                public void whenInitStartupStateAndEditingPatterns_ThenDoesNothing() {

                    var application = Mockito.mock(IAutomateApplication.class);
                    Mockito.when(application.refreshLocalState())
                      .thenReturn(new AllStateLite(
                        List.of(new PatternLite("anid", "aname", "aversion", false)),
                        List.of(new ToolkitLite("anid", "aname", "aversion")),
                        List.of()));
                    Mockito.when(application.getEditingMode())
                      .thenReturn(EditingMode.Patterns);
                    Mockito.when(application.isAuthoringMode())
                      .thenReturn(true);

                    AutomateToolWindowFactory.initStartupState(application);

                    Mockito.verify(application, never()).setEditingMode(any());
                    Mockito.verify(application, never()).setAuthoringMode(anyBoolean());
                }
            }

        }
    }

}
