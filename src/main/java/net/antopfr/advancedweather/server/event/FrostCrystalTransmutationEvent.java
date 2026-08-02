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
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.*;

@EventBusSubscriber(modid = AdvancedWeather.MOD_ID)
public class FrostCrystalTransmutationEvent {

    private static final int CATCH_Y = -40;
    private static final int FALLBACK_Y = 70;

    private static final double RISE_SPEED = 0.5;

    private static final Map<UUID, Double> ORIGIN_Y = new HashMap<>();
    private static final Map<UUID, Double> RISING  = new HashMap<>();

    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (level.dimension() != Level.END) return;

        if (WeatherManager.get(level).getCurrentWeather(level) != WeatherTypes.ENDERSTORM) {
            ORIGIN_Y.clear();
        }

        tickRising(level);

        if (WeatherManager.get(level).getCurrentWeather(level) != WeatherTypes.ENDERSTORM) return;
        if (++tickCounter < 3) return;
        tickCounter = 0;

        List<ItemEntity> toTransmute = new ArrayList<>();
        for (Entity entity : level.getAllEntities()) {
            if (!(entity instanceof ItemEntity item)) continue;
            if (!item.getItem().is(AWItems.DORMANT_CRYSTAL.get())) continue;

            if (item.getY() <= CATCH_Y) {
                toTransmute.add(item);
            } else {
                ORIGIN_Y.merge(item.getUUID(), item.getY(), Math::max);
            }
        }
        for (ItemEntity item : toTransmute) transmute(level, item);
    }

    private static void tickRising(ServerLevel level) {
        if (RISING.isEmpty()) return;

        Iterator<Map.Entry<UUID, Double>> it = RISING.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Double> e = it.next();
            Entity entity = level.getEntity(e.getKey());

            if (!(entity instanceof ItemEntity item) || item.isRemoved()) {
                it.remove();
                continue;
            }

            double targetY = e.getValue();
            item.setNoGravity(true);

            if (item.getY() >= targetY) {
                item.setPos(item.getX(), targetY, item.getZ());
                item.setDeltaMovement(Vec3.ZERO);
                it.remove();

                level.sendParticles(ParticleTypes.SNOWFLAKE,
                        item.getX(), targetY, item.getZ(), 30, 0.3, 0.3, 0.3, 0.05);
                level.playSound(null, item.blockPosition(),
                        SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.5f, 1.4f);
            } else {
                item.setDeltaMovement(0, RISE_SPEED, 0);

                if (level.random.nextInt(2) == 0) {
                    level.sendParticles(ParticleTypes.SNOWFLAKE,
                            item.getX(), item.getY(), item.getZ(), 2, 0.1, 0.15, 0.1, 0.02);
                }
            }
        }
    }

    private static void transmute(ServerLevel level, ItemEntity item) {
        int count = item.getItem().getCount();
        double x = item.getX(), z = item.getZ();

        double returnY = ORIGIN_Y.getOrDefault(item.getUUID(), (double) FALLBACK_Y);
        ORIGIN_Y.remove(item.getUUID());

        level.sendParticles(ParticleTypes.SNOWFLAKE, x, CATCH_Y, z, 60, 0.4, 0.6, 0.4, 0.12);
        level.sendParticles(ParticleTypes.END_ROD,   x, CATCH_Y, z, 40, 0.3, 0.5, 0.3, 0.08);
        level.sendParticles(ParticleTypes.FLASH,     x, CATCH_Y, z,  5, 0, 0, 0, 0);
        level.playSound(null, item.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 2f, 0.6f);

        item.discard();

        ItemEntity result = new ItemEntity(level, x, CATCH_Y, z,
                new ItemStack(AWItems.FROST_CRYSTAL.get(), count));
        result.setNoGravity(true);
        result.setDeltaMovement(0, RISE_SPEED, 0);
        result.setInvulnerable(true);
        result.setExtendedLifetime();
        result.setGlowingTag(true);
        result.setPickUpDelay(20);
        level.addFreshEntity(result);

        RISING.put(result.getUUID(), returnY);
    }
}