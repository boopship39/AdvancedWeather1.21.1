package net.antopfr.advancedweather.server.event;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.content.item.AWItems;
import net.antopfr.advancedweather.weather.WeatherManager;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = AdvancedWeather.MOD_ID)
public class RainCrystalTransmutationEvent {

    private static final double STRIKE_RADIUS = 3.0;

    @SubscribeEvent
    public static void onItemSpawn(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ItemEntity item
                && item.getItem().is(AWItems.DORMANT_CRYSTAL.get())) {
            item.setInvulnerable(true);
            item.setExtendedLifetime();
        }
    }

    @SubscribeEvent
    public static void onLightning(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof LightningBolt bolt)) return;
        if (event.getLevel().isClientSide) return;
        ServerLevel level = (ServerLevel) event.getLevel();

        if (WeatherManager.get(level).getCurrentWeather(level) != WeatherTypes.THUNDERSTORM) return;

        Vec3 pos = bolt.position();
        AABB zone = new AABB(pos, pos).inflate(STRIKE_RADIUS);

        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, zone)) {
            if (item.getItem().is(AWItems.DORMANT_CRYSTAL.get())) {
                transmute(level, item);
            }
        }
    }

    private static void transmute(ServerLevel level, ItemEntity item) {
        int count = item.getItem().getCount();

        ItemEntity result = new ItemEntity(level,
                item.getX(), item.getY(), item.getZ(),
                new ItemStack(AWItems.RAIN_CRYSTAL.get(), count));
        result.setDeltaMovement(0, 0.6, 0);
        result.setPickUpDelay(10);
        result.setInvulnerable(true);
        result.setGlowingTag(true);
        level.addFreshEntity(result);
        item.discard();

        level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                item.getX(), item.getY() + 0.2, item.getZ(),
                50, 0.3, 0.3, 0.3, 0.3);
        level.playSound(null, item.blockPosition(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1f, 1.3f);
    }
}
