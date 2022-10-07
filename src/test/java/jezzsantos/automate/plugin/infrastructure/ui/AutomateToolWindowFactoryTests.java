package jezzsantos.automate.plugin.infrastructure.ui;

import jezzsantos.automate.plugin.application.IAutomateApplication;
import jezzsantos.automate.plugin.application.interfaces.AllStateLite;
import jezzsantos.automate.plugin.application.interfaces.EditingMode;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternLite;
import jezzsantos.automate.plugin.application.interfaces.patterns.PatternVersion;
import jezzsantos.automate.plugin.application.interfaces.toolkits.ToolkitLite;
import org.junit.jupiter.api.BeforeEach;
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

        private IAutomateApplication application;

        @BeforeEach
        public void setUp() {

            this.application = Mockito.mock(IAutomateApplication.class);
            Mockito.when(this.application.isCliInstalled())
              .thenReturn(true);
        }

        @Nested
        class GivenNoAuthoringMode {

            @Nested
            class GivenNoToolkits {

                @Test
                public void whenInitStartupState_ThenResetsToDraftMode() {

                    Mockito.when(GivenNoPatterns.this.application.listAllAutomation(anyBoolean()))
                      .thenReturn(new AllStateLite());
                    Mockito.when(GivenNoPatterns.this.application.isAuthoringMode())
                      .thenReturn(false);

                    AutomateToolWindowFactory.initStartupState(GivenNoPatterns.this.application);

                    Mockito.verify(GivenNoPatterns.this.application).setEditingMode(EditingMode.DRAFTS);
                    Mockito.verify(GivenNoPatterns.this.application, never()).setAuthoringMode(anyBoolean());
                }
            }

            @Nested
            class GivenSomeInstalledToolkits {

                @Test
                public void whenInitStartupState_ThenResetsToDraftMode() {

                    Mockito.when(GivenNoPatterns.this.application.listAllAutomation(anyBoolean()))
                      .thenReturn(new AllStateLite(
                        List.of(),
                        List.of(new ToolkitLite("anid", "aname", "aversion")),
                        List.of()));
                    Mockito.when(GivenNoPatterns.this.application.isAuthoringMode())
                      .thenReturn(false);

                    AutomateToolWindowFactory.initStartupState(GivenNoPatterns.this.application);

                    Mockito.verify(GivenNoPatterns.this.application).setEditingMode(EditingMode.DRAFTS);
                    Mockito.verify(GivenNoPatterns.this.application, never()).setAuthoringMode(anyBoolean());
                }
            }
        }

        @Nested
        class GivenAuthoringMode {

            @Nested
            class GivenNoToolkits {

                @Test
                public void whenInitStartupState_ThenResetsToDraftMode() {

                    Mockito.when(GivenNoPatterns.this.application.listAllAutomation(anyBoolean()))
                      .thenReturn(new AllStateLite());
                    Mockito.when(GivenNoPatterns.this.application.isAuthoringMode())
                      .thenReturn(true);

                    AutomateToolWindowFactory.initStartupState(GivenNoPatterns.this.application);

                    Mockito.verify(GivenNoPatterns.this.application).setEditingMode(EditingMode.DRAFTS);
                    Mockito.verify(GivenNoPatterns.this.application).setAuthoringMode(false);
                }
            }

            @Nested
            class GivenSomeInstalledToolkits {

                @Test
                public void whenInitStartupState_ThenResetsToDraftMode() {

                    Mockito.when(GivenNoPatterns.this.application.listAllAutomation(anyBoolean()))
                      .thenReturn(new AllStateLite(
                        List.of(),
                        List.of(new ToolkitLite("anid", "aname", "aversion")),
                        List.of()));
                    Mockito.when(GivenNoPatterns.this.application.isAuthoringMode())
                      .thenReturn(true);

                    AutomateToolWindowFactory.initStartupState(GivenNoPatterns.this.application);

                    Mockito.verify(GivenNoPatterns.this.application).setEditingMode(EditingMode.DRAFTS);
                    Mockito.verify(GivenNoPatterns.this.application).setAuthoringMode(false);
                }
            }
        }
    }

    @Nested
    class GivenSomePatterns {

        private IAutomateApplication application;

        @BeforeEach
        public void setUp() {

            this.application = Mockito.mock(IAutomateApplication.class);
            Mockito.when(this.application.isCliInstalled())
              .thenReturn(true);
        }

        @Nested
        class GivenNoAuthoringMode {

            @Nested
            class GivenNoToolkits {

                @Test
                public void whenInitStartupState_ThenResetsToPatternMode() {

                    Mockito.when(GivenSomePatterns.this.application.listAllAutomation(anyBoolean()))
                      .thenReturn(new AllStateLite(
                        List.of(new PatternLite("anid", "aname", new PatternVersion("aversion"), false)),
                        List.of(),
                        List.of()));
                    Mockito.when(GivenSomePatterns.this.application.isAuthoringMode())
                      .thenReturn(false);

                    AutomateToolWindowFactory.initStartupState(GivenSomePatterns.this.application);

                    Mockito.verify(GivenSomePatterns.this.application).setEditingMode(EditingMode.PATTERNS);
                    Mockito.verify(GivenSomePatterns.this.application).setAuthoringMode(true);
                }
            }

            @Nested
            class GivenSomeInstalledToolkits {

                @Test
                public void whenInitStartupStateAndEditingDrafts_ThenDoesNothing() {

                    Mockito.when(GivenSomePatterns.this.application.listAllAutomation(anyBoolean()))
                      .thenReturn(new AllStateLite(
                        List.of(new PatternLite("anid", "aname", new PatternVersion("aversion"), false)),
                        List.of(new ToolkitLite("anid", "aname", "aversion")),
                        List.of()));
                    Mockito.when(GivenSomePatterns.this.application.getEditingMode())
                      .thenReturn(EditingMode.DRAFTS);
                    Mockito.when(GivenSomePatterns.this.application.isAuthoringMode())
                      .thenReturn(false);

                    AutomateToolWindowFactory.initStartupState(GivenSomePatterns.this.application);

                    Mockito.verify(GivenSomePatterns.this.application, never()).setEditingMode(any());
                    Mockito.verify(GivenSomePatterns.this.application, never()).setAuthoringMode(anyBoolean());
                }

                @Test
                public void whenInitStartupStateAndEditingPatterns_ThenDoesNothing() {

                    Mockito.when(GivenSomePatterns.this.application.listAllAutomation(anyBoolean()))
                      .thenReturn(new AllStateLite(
                        List.of(new PatternLite("anid", "aname", new PatternVersion("aversion"), false)),
                        List.of(new ToolkitLite("anid", "aname", "aversion")),
                        List.of()));
                    Mockito.when(GivenSomePatterns.this.application.getEditingMode())
                      .thenReturn(EditingMode.PATTERNS);
                    Mockito.when(GivenSomePatterns.this.application.isAuthoringMode())
                      .thenReturn(false);

                    AutomateToolWindowFactory.initStartupState(GivenSomePatterns.this.application);

                    Mockito.verify(GivenSomePatterns.this.application, never()).setEditingMode(any());
                    Mockito.verify(GivenSomePatterns.this.application, never()).setAuthoringMode(anyBoolean());
                }
            }
        }

        @Nested
        class GivenAuthoringMode {

            @Nested
            class GivenNoToolkits {

                @Test
                public void whenInitStartupState_ThenResetsToDraftMode() {

                    Mockito.when(GivenSomePatterns.this.application.listAllAutomation(anyBoolean()))
                      .thenReturn(new AllStateLite(
                        List.of(new PatternLite("anid", "aname", new PatternVersion("aversion"), false)),
                        List.of(),
                        List.of()));
                    Mockito.when(GivenSomePatterns.this.application.isAuthoringMode())
                      .thenReturn(true);

                    AutomateToolWindowFactory.initStartupState(GivenSomePatterns.this.application);

                    Mockito.verify(GivenSomePatterns.this.application).setEditingMode(EditingMode.PATTERNS);
                    Mockito.verify(GivenSomePatterns.this.application, never()).setAuthoringMode(anyBoolean());
                }
            }

            @Nested
            class GivenSomeInstalledToolkits {

                @Test
                public void whenInitStartupStateAndEditingDrafts_ThenDoesNothing() {

                    Mockito.when(GivenSomePatterns.this.application.listAllAutomation(anyBoolean()))
                      .thenReturn(new AllStateLite(
                        List.of(new PatternLite("anid", "aname", new PatternVersion("aversion"), false)),
                        List.of(new ToolkitLite("anid", "aname", "aversion")),
                        List.of()));
                    Mockito.when(GivenSomePatterns.this.application.getEditingMode())
                      .thenReturn(EditingMode.DRAFTS);
                    Mockito.when(GivenSomePatterns.this.application.isAuthoringMode())
                      .thenReturn(true);

                    AutomateToolWindowFactory.initStartupState(GivenSomePatterns.this.application);

                    Mockito.verify(GivenSomePatterns.this.application, never()).setEditingMode(any());
                    Mockito.verify(GivenSomePatterns.this.application, never()).setAuthoringMode(anyBoolean());
                }

                @Test
                public void whenInitStartupStateAndEditingPatterns_ThenDoesNothing() {

                    Mockito.when(GivenSomePatterns.this.application.listAllAutomation(anyBoolean()))
                      .thenReturn(new AllStateLite(
                        List.of(new PatternLite("anid", "aname", new PatternVersion("aversion"), false)),
                        List.of(new ToolkitLite("anid", "aname", "aversion")),
                        List.of()));
                    Mockito.when(GivenSomePatterns.this.application.getEditingMode())
                      .thenReturn(EditingMode.PATTERNS);
                    Mockito.when(GivenSomePatterns.this.application.isAuthoringMode())
                      .thenReturn(true);

                    AutomateToolWindowFactory.initStartupState(GivenSomePatterns.this.application);

                    Mockito.verify(GivenSomePatterns.this.application, never()).setEditingMode(any());
                    Mockito.verify(GivenSomePatterns.this.application, never()).setAuthoringMode(anyBoolean());
                }
            }
        }
    }
}
