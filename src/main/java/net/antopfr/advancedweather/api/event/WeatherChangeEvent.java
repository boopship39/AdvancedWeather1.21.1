package net.antopfr.advancedweather.api.event;

import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.Event;

/**
 * Fired on the {@code NeoForge.EVENT_BUS} (server side) right after a dimension's
 * weather changes, whichever mode caused it (procedural transition, real-weather
 * sync, command or API call). Only fired when the weather actually changed
 * ({@code previous != current}).
 *
 * <p>This is a notification event. It is <b>not</b> cancellable — by the time it
 * fires the change has already been applied and synced to clients.
 *
 * <p>Part of the public Advanced Weather API. Example:
 * <pre>{@code
 * NeoForge.EVENT_BUS.addListener((WeatherChangeEvent e) -> {
 *     if (e.getCurrent() == WeatherTypes.THUNDERSTORM) { ... }
 * });
 * }</pre>
 */
public class WeatherChangeEvent extends Event {

    private final ServerLevel level;
    private final WeatherTypes.Dimension dimension;
    private final WeatherTypes previous;
    private final WeatherTypes current;

    public WeatherChangeEvent(ServerLevel level, WeatherTypes.Dimension dimension,
                              WeatherTypes previous, WeatherTypes current) {
        this.level = level;
        this.dimension = dimension;
        this.previous = previous;
        this.current = current;
    }

    /** The level whose weather changed. */
    public ServerLevel getLevel() {
        return level;
    }

    /** Which Advanced Weather dimension group the level belongs to. */
    public WeatherTypes.Dimension getDimension() {
        return dimension;
    }

    /** The weather that was active before this change. */
    public WeatherTypes getPrevious() {
        return previous;
    }

    /** The weather that is now active. */
    public WeatherTypes getCurrent() {
        return current;
    }
}
