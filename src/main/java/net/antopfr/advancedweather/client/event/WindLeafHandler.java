package net.antopfr.advancedweather.client.event;

import net.antopfr.advancedweather.client.particle.types.LeafParticle;
import net.antopfr.advancedweather.client.state.ClientAtmosphereState;
import net.antopfr.advancedweather.weather.effect.global.wind.WindBurstManager;
import net.antopfr.advancedweather.weather.effect.global.wind.WindDirection;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = "advancedweather", value = Dist.CLIENT)
public class WindLeafHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || mc.isPaused()) return;

        float windVolume = WindBurstManager.getHeavyIntensity();

        if (windVolume < 0.1f) return;
        if (ClientAtmosphereState.getWindIntensity() < 0.4f) return;

        Player player = mc.player;
        Vec3 windDir = WindDirection.get(0f);

        int attempts = (int) (windVolume * 4);

        for (int i = 0; i < attempts; i++) {
            double r = mc.level.random.nextDouble() * 25.0;
            double theta = mc.level.random.nextDouble() * Math.PI * 2;

            double x = player.getX() + r * Math.cos(theta);
            double z = player.getZ() + r * Math.sin(theta);

            int topY = mc.level.getHeight(Heightmap.Types.WORLD_SURFACE, (int) x, (int) z);
            if (topY <= mc.level.getMinBuildHeight()) continue;

            BlockPos leafPos = new BlockPos((int) x, topY - 1, (int) z);
            BlockState state = mc.level.getBlockState(leafPos);

            if (state.is(BlockTags.LEAVES)) {
                int biomeColor = mc.getBlockColors().getColor(state, mc.level, leafPos, 0);

                if (biomeColor == -1) {
                    biomeColor = mc.level.getBiome(leafPos).value().getFoliageColor();
                }

                double spawnX = leafPos.getX() + mc.level.random.nextDouble();
                double spawnY = leafPos.getY() - 0.1;
                double spawnZ = leafPos.getZ() + mc.level.random.nextDouble();

                double windSpeedFactor = ClientAtmosphereState.getWindIntensity() * 0.4 * windVolume;
                double windX = windDir.x * windSpeedFactor + (mc.level.random.nextDouble() - 0.5) * 0.05;
                double windZ = windDir.z * windSpeedFactor + (mc.level.random.nextDouble() - 0.5) * 0.05;

                double windY = -0.01 - mc.level.random.nextDouble() * 0.02;

                if (LeafParticle.Provider.CURRENT_SPRITES != null) {
                    LeafParticle customLeaf = new LeafParticle(
                            mc.level,
                            spawnX, spawnY, spawnZ,
                            windX, windY, windZ,
                            biomeColor,
                            LeafParticle.Provider.CURRENT_SPRITES
                    );
                    mc.particleEngine.add(customLeaf);
                }
            }
        }
    }
}