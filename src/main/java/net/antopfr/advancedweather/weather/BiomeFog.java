package net.antopfr.advancedweather.weather;

import com.mojang.serialization.Codec;

import java.util.Map;

public record BiomeFog(Map<String, Integer> colors) {

    public static final Codec<BiomeFog> CODEC =
            Codec.unboundedMap(Codec.STRING, colorCodec())
                    .fieldOf("colors")
                    .xmap(BiomeFog::new, BiomeFog::colors)
                    .codec();

    private static Codec<Integer> colorCodec() {
        return Codec.STRING.xmap(
                s -> Integer.parseInt(s.replace("#", "").trim(), 16),
                i -> String.format("%06X", i & 0xFFFFFF));
    }

    public Integer get(WeatherTypes type) {
        return colors.get(type.name().toLowerCase());
    }
}
