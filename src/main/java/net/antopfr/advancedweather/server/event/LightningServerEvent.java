package net.antopfr.advancedweather.server.event;

import net.antopfr.advancedweather.network.toclient.FlashPacket;
import net.antopfr.advancedweather.util.PlayerChecks;
import net.antopfr.advancedweather.weather.WeatherManager;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@EventBusSubscriber(modid = "advancedweather")
public class LightningServerEvent {

    private static final Random random = new Random();
    private static final Map<UUID, Integer> lightningCooldowns = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ServerLevel level = player.serverLevel();
        WeatherTypes type = WeatherManager.get(level).getCurrentWeather(level);

        if (type == WeatherTypes.THUNDERSTORM || type == WeatherTypes.NETHERSTORM || type == WeatherTypes.ENDERSTORM) {
            tickLightning(player, level);
        }
    }

    private static void tickLightning(ServerPlayer player, ServerLevel level) {
        UUID uuid = player.getUUID();
        int cooldown = lightningCooldowns.getOrDefault(uuid, 0);
        if (cooldown > 0) {
            lightningCooldowns.put(uuid, cooldown - 1);
            return;
        }
        lightningCooldowns.put(uuid, 100 + random.nextInt(200));

        int range = 32;
        double x = player.getX() + (random.nextDouble() - 0.5) * range;
        double z = player.getZ() + (random.nextDouble() - 0.5) * range;

        BlockPos strikePos;
        boolean isNether = level.dimension().equals(Level.NETHER);

        if (isNether) {
            int startY = Math.min(player.getBlockY() + 15, 120);
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos((int) x, startY, (int) z);

            while (level.isEmptyBlock(mutablePos) && mutablePos.getY() > level.getMinBuildHeight()) {
                mutablePos.move(0, -1, 0);
            }
            strikePos = mutablePos.immutable();
        } else {
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, (int) x, (int) z);
            strikePos = new BlockPos((int) x, y, (int) z);

            if (level.dimension().equals(Level.OVERWORLD) && !level.canSeeSky(strikePos)) return;
        }

        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
        if (lightning != null) {
            lightning.moveTo(Vec3.atBottomCenterOf(strikePos));

            if (isNether) {
                lightning.setVisualOnly(true);
            }

            level.addFreshEntity(lightning);
        }

        if (!PlayerChecks.isShielded(level, player.blockPosition())) {
            PacketDistributor.sendToPlayer(player, FlashPacket.INSTANCE);
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        lightningCooldowns.remove(event.getEntity().getUUID());
    }
}
