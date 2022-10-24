package jezzsantos.automate.plugin.infrastructure.reporting;

import org.jetbrains.annotations.NotNull;

public interface ICorrelationIdBuilder {

    @NotNull
    String build(@NotNull String sessionId);
}
