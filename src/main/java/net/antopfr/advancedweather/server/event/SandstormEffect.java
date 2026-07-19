package net.antopfr.advancedweather.server.event;

import net.antopfr.advancedweather.config.AWCommonConfig;
import net.antopfr.advancedweather.weather.WeatherManager;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * While a sandstorm blows, players standing out in the open in a sandy biome are slowed and
 * tire faster (blowing sand). Only where the sandstorm visuals actually appear, and only when
 * exposed to the sky — sheltering under a roof protects.
 */
@EventBusSubscriber(modid = "advancedweather")
public class SandstormEffect {

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!AWCommonConfig.get().sandstormAffectsPlayers) return;
        if (player.isCreative() || player.isSpectator()) return;

        ServerLevel level = player.serverLevel();
        if (!level.dimension().equals(Level.OVERWORLD)) return;
        if (WeatherManager.get(level).getCurrentWeather(level) != WeatherTypes.SANDSTORM) return;
        if (level.getGameTime() % 40 != 0) return; // every 2s

        BlockPos pos = player.blockPosition();
        if (!level.canSeeSky(pos)) return;        // sheltered
        if (!isSandyBiome(level, pos)) return;    // only where the storm blows

        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0, false, false, true));
        player.causeFoodExhaustion(0.5f);
    }

    private static boolean isSandyBiome(ServerLevel level, BlockPos pos) {
        Holder<Biome> biome = level.getBiome(pos);
        return biome.is(Biomes.DESERT) || biome.is(Biomes.BEACH) || biome.is(BiomeTags.IS_BADLANDS);
    }
}
