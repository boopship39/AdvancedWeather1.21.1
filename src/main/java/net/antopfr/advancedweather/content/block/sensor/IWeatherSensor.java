package net.antopfr.advancedweather.content.block.sensor;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public interface IWeatherSensor {

    enum SensorType {
        THERMOMETER,
        HYGROMETER,
        BAROMETER,
        ANEMOMETER
        // WIND_VANE
    }

    SensorType getSensorType();

    boolean isValidlyPlaced(ServerLevel level, BlockPos pos);
}
