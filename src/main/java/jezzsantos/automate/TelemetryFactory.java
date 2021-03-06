package jezzsantos.automate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import jezzsantos.automate.metricdata.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Map;

public class TelemetryFactory {
    @NotNull
    private final Gson gson;
    private final JsonParser jsonParser = new JsonParser();

    public TelemetryFactory() {
        gson = new Gson();
    }

    @NotNull
    public Telemetry fromJson(@NotNull String json) {
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();

        String name = jsonObject.get("name").getAsString();
        TelemetryType type = TelemetryType.fromType(name);

        JsonObject data = jsonObject.getAsJsonObject("data").getAsJsonObject("baseData");

        ITelemetryData telemetryData;
        switch (type) {
            case Message:
                telemetryData = gson.fromJson(data, MessageData.class);
                break;
            case Request:
                telemetryData = gson.fromJson(data, RequestData.class);
                break;
            case Exception:
                telemetryData = gson.fromJson(data, ExceptionData.class);
                break;
            case Metric:
                telemetryData = gson.fromJson(data, MetricData.class);
                break;
            case RemoteDependency:
                telemetryData = gson.fromJson(data, RemoteDependencyData.class);
                break;
            case Event:
                telemetryData = gson.fromJson(data, EventData.class);
                break;
            default:
                telemetryData = gson.fromJson(data, UnkData.class);
                break;
        }

        Type tagsMapType = new TypeToken<Map<String, String>>() {}.getType();

        return new Telemetry(type, json, jsonObject, telemetryData, gson.fromJson(jsonObject.get("tags"), tagsMapType));
    }

    @Nullable
    public Telemetry tryCreateFromDebugOutputLog(@NotNull String output) {
        @NotNull String logPrefix = "category: Automate";
        @NotNull String filteredByPrefix = " (filtered by ";
        @NotNull String unconfiguredPrefix = " (unconfigured) ";

        if (!output.startsWith(logPrefix)) {
            return null;
        }

        String json = output.substring(output.indexOf('{'), output.lastIndexOf('}') + 1);

        Telemetry telemetry = fromJson(json);
        String telemetryState = output.substring(logPrefix.length());
        if (telemetryState.startsWith(filteredByPrefix)) {
            telemetry.setFilteredBy(telemetryState.substring(filteredByPrefix.length(), telemetryState.indexOf(')')));
        } else if (telemetryState.startsWith(unconfiguredPrefix)) {
            telemetry.setUnConfigured();
        }

        return telemetry;
    }
}
