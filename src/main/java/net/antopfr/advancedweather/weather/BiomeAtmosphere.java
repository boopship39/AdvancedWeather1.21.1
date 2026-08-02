package net.antopfr.advancedweather.weather;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record BiomeAtmosphere(float temperature, float humidity) {

    public static final Codec<BiomeAtmosphere> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.FLOAT.fieldOf("temperature").forGetter(BiomeAtmosphere::temperature),
            Codec.FLOAT.fieldOf("humidity").forGetter(BiomeAtmosphere::humidity)
    ).apply(i, BiomeAtmosphere::new));

    public static final BiomeAtmosphere DEFAULT = new BiomeAtmosphere(15f, 60f);
}