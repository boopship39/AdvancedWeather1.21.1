package net.antopfr.advancedweather.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.antopfr.advancedweather.config.AWCommonConfig;
import net.antopfr.advancedweather.content.block.sensor.IWeatherSensor;
import net.antopfr.advancedweather.content.block.station.WeatherStationBlockEntity;
import net.antopfr.advancedweather.weather.LocalAtmosphere;
import net.antopfr.advancedweather.weather.WeatherManager;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public record WeatherRecord(
        long gameTime,
        ResourceLocation dimension,
        BlockPos pos,
        ResourceLocation biome,
        String stationName,
        int weatherOrdinal,
        int sensorMask,
        float temperature,
        float pressure,
        float humidity,
        float windIntensity
) {
    public static final int MASK_TEMPERATURE = 1;
    public static final int MASK_PRESSURE = 2;
    public static final int MASK_HUMIDITY = 4;
    public static final int MASK_WIND = 8;
    public static final int MASK_ALL = 0b1111;

    public static final Codec<WeatherRecord> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.LONG.fieldOf("time").forGetter(WeatherRecord::gameTime),
            ResourceLocation.CODEC.fieldOf("dim").forGetter(WeatherRecord::dimension),
            BlockPos.CODEC.fieldOf("pos").forGetter(WeatherRecord::pos),
            ResourceLocation.CODEC.fieldOf("biome").forGetter(WeatherRecord::biome),
            Codec.STRING.optionalFieldOf("station", "").forGetter(WeatherRecord::stationName),
            Codec.INT.fieldOf("weather").forGetter(WeatherRecord::weatherOrdinal),
            Codec.INT.optionalFieldOf("mask", MASK_ALL).forGetter(WeatherRecord::sensorMask),
            Codec.FLOAT.fieldOf("temp").forGetter(WeatherRecord::temperature),
            Codec.FLOAT.fieldOf("pressure").forGetter(WeatherRecord::pressure),
            Codec.FLOAT.fieldOf("humidity").forGetter(WeatherRecord::humidity),
            Codec.FLOAT.fieldOf("wind").forGetter(WeatherRecord::windIntensity)
    ).apply(i, WeatherRecord::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WeatherRecord> STREAM_CODEC =
            StreamCodec.of(
                    (buf, r) -> {
                        buf.writeLong(r.gameTime);
                        buf.writeResourceLocation(r.dimension);
                        buf.writeBlockPos(r.pos);
                        buf.writeResourceLocation(r.biome);
                        buf.writeUtf(r.stationName, 32);
                        buf.writeVarInt(r.weatherOrdinal);
                        buf.writeVarInt(r.sensorMask);
                        buf.writeFloat(r.temperature);
                        buf.writeFloat(r.pressure);
                        buf.writeFloat(r.humidity);
                        buf.writeFloat(r.windIntensity);
                    },
                    buf -> new WeatherRecord(
                            buf.readLong(), buf.readResourceLocation(), buf.readBlockPos(),
                            buf.readResourceLocation(), buf.readUtf(32), buf.readVarInt(),
                            buf.readVarInt(),
                            buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat()));

    public boolean hasTemperature() { return (sensorMask & MASK_TEMPERATURE) != 0; }
    public boolean hasPressure()    { return (sensorMask & MASK_PRESSURE) != 0; }
    public boolean hasHumidity()    { return (sensorMask & MASK_HUMIDITY) != 0; }
    public boolean hasWind()        { return (sensorMask & MASK_WIND) != 0; }

    public WeatherTypes weatherType() {
        var values = WeatherTypes.values();
        return weatherOrdinal >= 0 && weatherOrdinal < values.length
                ? values[weatherOrdinal] : values[0];
    }

    public static WeatherRecord capture(ServerLevel level, BlockPos pos,
                                        WeatherStationBlockEntity station) {
        WeatherManager manager = WeatherManager.get(level);
        boolean gating = AWCommonConfig.get().stationRequiresSensors;

        int mask = 0;
        if (!gating || validSensor(station, IWeatherSensor.SensorType.THERMOMETER)) mask |= MASK_TEMPERATURE;
        if (!gating || validSensor(station, IWeatherSensor.SensorType.BAROMETER))   mask |= MASK_PRESSURE;
        if (!gating || validSensor(station, IWeatherSensor.SensorType.HYGROMETER))  mask |= MASK_HUMIDITY;
        if (!gating || validSensor(station, IWeatherSensor.SensorType.ANEMOMETER))  mask |= MASK_WIND;

        BlockPos tempPos  = sensorPosOr(station, IWeatherSensor.SensorType.THERMOMETER, pos);
        BlockPos pressPos = sensorPosOr(station, IWeatherSensor.SensorType.BAROMETER, pos);
        BlockPos humPos   = sensorPosOr(station, IWeatherSensor.SensorType.HYGROMETER, pos);

        return new WeatherRecord(
                level.getDayTime(),
                level.dimension().location(),
                pos,
                level.getBiome(pos).unwrapKey().map(ResourceKey::location)
                        .orElse(ResourceLocation.withDefaultNamespace("plains")),
                station.getStationName(),
                manager.getCurrentWeather(level).ordinal(),
                mask,
                (mask & MASK_TEMPERATURE) != 0 ? LocalAtmosphere.getLocalTemperature(level, tempPos) : 0f,
                (mask & MASK_PRESSURE) != 0 ? LocalAtmosphere.getLocalPressure(level, pressPos) : 0f,
                (mask & MASK_HUMIDITY) != 0 ? LocalAtmosphere.getLocalHumidity(level, humPos) : 0f,
                (mask & MASK_WIND) != 0 ? manager.getAtmosphere(level).getWindIntensity() : 0f
        );
    }

    private static boolean validSensor(WeatherStationBlockEntity st, IWeatherSensor.SensorType t) {
        var s = st.getSensor(t);
        return s != null && s.valid();
    }

    private static BlockPos sensorPosOr(WeatherStationBlockEntity st,
                                        IWeatherSensor.SensorType t, BlockPos fallback) {
        var s = st.getSensor(t);
        return s != null ? s.pos() : fallback;
    }
}