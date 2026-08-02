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
public class FireCrystalTransmutationEvent {

    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (level.dimension() != Level.NETHER) return;

        if (WeatherManager.get(level).getCurrentWeather(level) != WeatherTypes.HELLFIRE) return;

        if (++tickCounter < 20) return;
        tickCounter = 0;

        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof ItemEntity item)) continue;
            if (!item.getItem().is(AWItems.DORMANT_CRYSTAL.get())) continue;
            if (!item.isInLava()) continue;

            transmute(level, item);
        }
    }

    private static void transmute(ServerLevel level, ItemEntity item) {
        int count = item.getItem().getCount();

        ItemEntity result = new ItemEntity(level,
                item.getX(), item.getY() + 0.3, item.getZ(),
                new ItemStack(AWItems.FIRE_CRYSTAL.get(), count));
        result.setDeltaMovement(0, 0.6, 0);
        result.setPickUpDelay(10);
        result.setInvulnerable(true);
        result.setGlowingTag(true);
        level.addFreshEntity(result);
        item.discard();

        level.sendParticles(ParticleTypes.LAVA,
                item.getX(), item.getY() + 0.3, item.getZ(),
                30, 0.3, 0.3, 0.3, 0.1);
        level.sendParticles(ParticleTypes.FLAME,
                item.getX(), item.getY() + 0.3, item.getZ(),
                40, 0.3, 0.4, 0.3, 0.05);
        level.playSound(null, item.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1f, 0.7f);
    }
}
