package net.antopfr.advancedweather.api;

/**
 * Immutable snapshot of a dimension's atmospheric state at the moment it was read.
 *
 * <p>All values are a copy; mutating the world afterwards does not affect this object.
 * Part of the public Advanced Weather API.
 *
 * @param pressure         air pressure in hPa (roughly 870-1180 depending on dimension)
 * @param pressureVelocity per-tick pressure change; positive = rising, negative = falling
 * @param dewPoint         dew point in °C
 * @param temperature      air temperature in °C
 * @param humidity         relative humidity in percent (0-100)
 * @param windIntensity    normalized wind intensity (0 = calm, 1 = storm-force)
 * @param mode             driving mode: {@code "PROCEDURAL"}, {@code "REAL"} or {@code "MANUAL"}
 * @param pressureCategory pressure band: {@code "HIGH"}, {@code "NEUTRAL"}, {@code "LOW"} or {@code "STORM"}
 */
public record AtmosphereSnapshot(
        float pressure,
        float pressureVelocity,
        float dewPoint,
        float temperature,
        float humidity,
        float windIntensity,
        String mode,
        String pressureCategory
) {}
