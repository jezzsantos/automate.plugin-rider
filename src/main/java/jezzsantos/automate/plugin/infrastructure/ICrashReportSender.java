package jezzsantos.automate.plugin.infrastructure;

import jezzsantos.automate.plugin.application.services.interfaces.INotifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface ICrashReportSender {

    void send(@NotNull ErrorReport report) throws Exception;

    @Nullable
    INotifier.LinkDescriptor getLink();

    class ErrorReport {

        private final List<Throwable> exceptions;
        private String version;
        private String lastActionId;
        private String reproSteps;
        private String deviceId;

        public ErrorReport() {

            this.exceptions = new ArrayList<>();
        }

        @Nullable
        public String getVersion() {return this.version;}

        public void setVersion(@Nullable String version) {this.version = version;}

        public List<Throwable> getExceptions() {return this.exceptions;}

        public void addException(@Nullable Throwable exception) {

            this.exceptions.add(exception);
        }

        @Nullable
        public String getLastActionId() {return this.lastActionId;}

        public void setLastActionId(@Nullable String lastActionId) {this.lastActionId = lastActionId;}

        @Nullable
        public String getReproSteps() {return this.reproSteps;}

        public void setReproSteps(@Nullable String text) {this.reproSteps = text;}

        @Nullable
        public String getDeviceId() {return this.deviceId;}

        public void setDeviceId(@Nullable String deviceId) {this.deviceId = deviceId;}
    }
}


