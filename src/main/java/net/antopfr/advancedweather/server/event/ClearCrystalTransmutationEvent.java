package net.antopfr.advancedweather.server.event;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.content.item.AWItems;
import net.antopfr.advancedweather.weather.WeatherManager;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = AdvancedWeather.MOD_ID)
public class ClearCrystalTransmutationEvent {

    private static final int SKY_Y = 319;
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (level.dimension() != Level.OVERWORLD) return;

        WeatherTypes weather = WeatherManager.get(level).getCurrentWeather(level);
        if (weather != WeatherTypes.CLEAR && weather != WeatherTypes.SUNNY) return;

        if (++tickCounter < 10) return;
        tickCounter = 0;

        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof ItemEntity item)) continue;
            if (item.getY() < SKY_Y) continue;
            if (!item.getItem().is(AWItems.DORMANT_CRYSTAL.get())) continue;
            if (!level.canSeeSky(item.blockPosition())) continue;

            transmute(level, item);
        }
    }

    private static void transmute(ServerLevel level, ItemEntity item) {
        int count = item.getItem().getCount();

        ItemEntity result = new ItemEntity(level,
                item.getX(), item.getY(), item.getZ(),
                new ItemStack(AWItems.CLEAR_CRYSTAL.get(), count));
        result.setDeltaMovement(0, 0.6, 0);
        result.setInvulnerable(true);
        result.setGlowingTag(true);
        result.setExtendedLifetime();
        result.setPickUpDelay(20);
        level.addFreshEntity(result);
        item.discard();

        level.sendParticles(ParticleTypes.END_ROD,
                item.getX(), item.getY(), item.getZ(),
                50, 0.4, 0.4, 0.4, 0.1);
        level.sendParticles(ParticleTypes.GLOW,
                item.getX(), item.getY(), item.getZ(),
                30, 0.3, 0.3, 0.3, 0.05);
        level.playSound(null, item.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1f, 1.8f);
    }
}
