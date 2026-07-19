package net.antopfr.advancedweather.server.event;

import net.antopfr.advancedweather.config.AWCommonConfig;
import net.antopfr.advancedweather.server.damage_types.AWDamageSources;
import net.antopfr.advancedweather.weather.WeatherManager;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = "advancedweather")
public class HailDamageEvent {
    @SubscribeEvent
    public static void onHailDamageTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!level.dimension().equals(Level.OVERWORLD)) return;

        WeatherManager manager = WeatherManager.get(level);
        if (manager.getCurrentWeather(level) != WeatherTypes.HAIL) return;

        if (level.getGameTime() % 20 != 0) return;

        // entity damage
        if (AWCommonConfig.get().hailDamageEntities) {
            level.getEntities(EntityTypeTest.forClass(LivingEntity.class), entity -> {
                if (entity instanceof ArmorStand) return false;
                BlockPos pos = entity.blockPosition();
                BlockPos sky = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos);
                return sky.getY() <= pos.getY() + 1;
            }).forEach(entity -> {
                Vec3 motionBefore = entity.getDeltaMovement();
                entity.hurt(AWDamageSources.hail(level), 1);
                entity.setDeltaMovement(motionBefore);
            });
        }

        // block damage
        for (ServerPlayer player : level.players()) {
            tickHailBlockDamage(level, player.blockPosition());
        }
    }

    private static void tickHailBlockDamage(ServerLevel level, BlockPos center) {
        for (int dx = -8; dx <= 8; dx++) {
            for (int dz = -8; dz <= 8; dz++) {
                int x = center.getX() + dx;
                int z = center.getZ() + dz;
                int topY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;

                for (int y = topY; y <= topY + 8; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!level.canSeeSky(pos)) break;

                    BlockState state = level.getBlockState(pos);
                    Block block = state.getBlock();

                    if (block instanceof CropBlock) {
                        if (level.random.nextFloat() < 0.01f) {
                            BlockPos farmlandPos = pos.below();
                            BlockState farmlandState = level.getBlockState(farmlandPos);
                            if (farmlandState.is(Blocks.FARMLAND) && AWCommonConfig.get().hailDamageCrops) {
                                Block.dropResources(state, level, pos, null);
                                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                                FarmBlock.turnToDirt(null, farmlandState, level, farmlandPos);
                                level.playSound(null, farmlandPos, SoundEvents.ROOTED_DIRT_FALL,
                                        SoundSource.BLOCKS, 0.8f, 1.0f);
                            }
                        }
                        break;
                    } else if (block == Blocks.PUMPKIN || block == Blocks.MELON) {
                        if (level.random.nextFloat() < 0.01f)
                            level.destroyBlock(pos, true);
                        break;
                    } else if (state.is(BlockTags.LEAVES)) {
                        if (level.random.nextFloat() < 0.01f)
                            level.destroyBlock(pos, false);
                        break;
                    } else if (state.is(BlockTags.IMPERMEABLE) && AWCommonConfig.get().hailBreakGlass) {
                        if (level.random.nextFloat() < 0.01f) {
                            level.destroyBlock(pos, false);
                            level.playSound(null, pos, SoundEvents.GLASS_BREAK,
                                    SoundSource.BLOCKS, 0.8f,
                                    0.9f + level.random.nextFloat() * 0.2f);
                        }
                        break;
                    } else if (!state.isAir()) {
                        break;
                    }
                }
            }
        }
    }
}
