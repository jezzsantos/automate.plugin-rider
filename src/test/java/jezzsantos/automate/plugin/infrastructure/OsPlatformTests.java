package jezzsantos.automate.plugin.infrastructure;

import jezzsantos.automate.plugin.common.AutomateBundle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

public class OsPlatformTests {

    private OsPlatform platform;

    @BeforeEach
    public void setUp() {

        this.platform = new OsPlatform();
    }

    @Nested
    class GivenWindows {

        @Test
        public void whenGetDotNetToolsDirectory_ThenReturnsWindowsPath() {

            var variables = Map.of(
              "USERPROFILE", "awindowspath"
            );
            var properties = new Properties();
            properties.put("user.home", "anixpath");

            var result = OsPlatformTests.this.platform.getDotNetToolsDirectory(true, variables, properties);

            assertEquals(String.format("awindowspath%1$s.dotnet%1$stools%1$s", File.separatorChar), result);
        }

        @Test
        public void whenGetDotNetInstallationDirectoryAndNotFoundInPathOnWindows_ThenReturnsFallback() {

            var result = OsPlatformTests.this.platform.getDotNetInstallationDirectory(true,
                                                                                      Map.of("ProgramFiles", "programfiles"),
                                                                                      (path, filename) -> null);

            assertEquals(String.format("programfiles%1$sdotnet%1$s", File.separatorChar), result);
        }

        @SuppressWarnings("unchecked")
        @Test
        public void whenGetDotNetInstallationDirectoryAndFoundInPathAndExists_ThenReturnsPath() {

            var callback = (BiFunction<String, String, String>) Mockito.mock(BiFunction.class);
            Mockito.when(callback.apply(anyString(), anyString()))
              .thenReturn("afilepath");

            var result = OsPlatformTests.this.platform.getDotNetInstallationDirectory(true,
                                                                                      Map.of("Path", "apathcomponent"),
                                                                                      callback);

            assertEquals("apathcomponent", result);
            Mockito.verify(callback).apply("apathcomponent", "dotnet.exe");
            Mockito.verifyNoMoreInteractions(callback);
        }
    }

    @Nested
    class GivenNix {

        @Test
        public void whenGetDotNetToolsDirectory_ThenReturnsPath() {

            var variables = Map.of(
              "USERPROFILE", "awindowspath"
            );
            var properties = new Properties();
            properties.put("user.home", "anixpath");

            var result = OsPlatformTests.this.platform.getDotNetToolsDirectory(false, variables, properties);

            assertEquals(String.format("anixpath%1$s.dotnet%1$stools%1$s", File.separatorChar), result);
        }

        @Test
        public void whenGetDotNetInstallationDirectoryAndNotFoundInPathAndNotInFallback_ThenThrows() {

            assertThrows(RuntimeException.class,
                         () -> OsPlatformTests.this.platform.getDotNetInstallationDirectory(false,
                                                                                            Map.of(),
                                                                                            (path, filename) -> null),
                         AutomateBundle.message("exception.OSPlatform.DotNetInstallationDirectory.NotFound"));
        }

        @Test
        public void whenGetDotNetInstallationDirectoryAndNotFoundInPathButInFallback_ThenReturnsFallback() {

            var result = OsPlatformTests.this.platform.getDotNetInstallationDirectory(false,
                                                                                      Map.of(),
                                                                                      (path, filename) -> path.equals(OsPlatform.DotNetExecutableLocationFallbacksNix.get(0))
                                                                                        ? "afallbackpath"
                                                                                        : null);

            assertEquals(OsPlatform.DotNetExecutableLocationFallbacksNix.get(0), result);
        }

        @SuppressWarnings("unchecked")
        @Test
        public void whenGetDotNetInstallationDirectoryAndFoundInPathAndExists_ThenReturnsPath() {

            var callback = (BiFunction<String, String, String>) Mockito.mock(BiFunction.class);
            Mockito.when(callback.apply(anyString(), anyString()))
              .thenReturn("afilepath");

            var result = OsPlatformTests.this.platform.getDotNetInstallationDirectory(false,
                                                                                      Map.of("PATH", "apathcomponent"),
                                                                                      callback);

            assertEquals("apathcomponent", result);
            Mockito.verify(callback).apply("apathcomponent", "dotnet");
            Mockito.verifyNoMoreInteractions(callback);
        }
    }
}
