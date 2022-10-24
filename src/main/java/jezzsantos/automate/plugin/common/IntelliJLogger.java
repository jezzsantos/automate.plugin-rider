package jezzsantos.automate.plugin.common;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IntelliJLogger implements ILogger {

    private static final Logger logger = Logger.getInstance(Recorder.class);

    @Override
    public void log(@NotNull LogLevel level, @Nullable Throwable exception, @NotNull String messageTemplate, @Nullable Object... args) {

        switch (level) {

            case TRACE -> {
                if (logger.isTraceEnabled()) {
                    logger.trace(String.format(messageTemplate, args));
                }
            }
            case DEBUG -> {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format(messageTemplate, args));
                }
            }
            case INFORMATION -> logger.info(String.format(messageTemplate, args));

            case WARNING -> {
                if (exception != null) {
                    logger.warn(String.format(messageTemplate, args), exception);
                }
                else {
                    logger.warn(String.format(messageTemplate, args));
                }
            }
            case ERROR, CRITICAL -> {
                if (exception != null) {
                    logger.error(String.format(messageTemplate, args), exception);
                }
                else {
                    logger.error(String.format(messageTemplate, args));
                }
            }
            case NONE -> {
            }
        }
    }
}
