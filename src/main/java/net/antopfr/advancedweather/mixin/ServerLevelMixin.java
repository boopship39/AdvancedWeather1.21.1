package net.antopfr.advancedweather.mixin;

import net.antopfr.advancedweather.weather.WeatherTypes;
import net.antopfr.advancedweather.weather.WeatherManager;
import net.antopfr.advancedweather.util.BiomeMixinContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {

    @Inject(method = "tickChunk", at = @At("HEAD"))
    private void onTickChunkHead(LevelChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        BiomeMixinContext.setCurrentLevel((ServerLevel) (Object) this);
    }

    @Inject(method = "tickChunk", at = @At("RETURN"))
    private void onTickChunkReturn(CallbackInfo ci) {
        BiomeMixinContext.clearCurrentLevel();
    }

    @Inject(method = "tickPrecipitation", at = @At("HEAD"), cancellable = true)
    private void onTickPrecipitation(BlockPos blockPos, CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Object) this;
        WeatherTypes type = WeatherManager.get(level).getCurrentWeather(level);

        if (type != WeatherTypes.SNOW
                && type != WeatherTypes.BLIZZARD
                && type != WeatherTypes.FREEZING_RAIN) return;

        ci.cancel();

        BlockPos topPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, blockPos);
        BlockPos belowPos = topPos.below();

        if (level.isAreaLoaded(belowPos, 1)) {
            if (belowPos.getY() >= level.getMinBuildHeight() && belowPos.getY() < level.getMaxBuildHeight() && level.getBrightness(LightLayer.BLOCK, belowPos) < 10) {

                BlockState belowState = level.getBlockState(belowPos);
                FluidState fluidState = level.getFluidState(belowPos);

                if (fluidState.getType() == Fluids.WATER
                        && belowState.getBlock() instanceof LiquidBlock) {

                    level.setBlockAndUpdate(belowPos, Blocks.ICE.defaultBlockState());
                }
            }
        }

        if (type == WeatherTypes.SNOW || type == WeatherTypes.BLIZZARD) {
            int maxLayers = level.getGameRules().getInt(GameRules.RULE_SNOW_ACCUMULATION_HEIGHT);
            if (maxLayers > 0) {
                BlockState topState = level.getBlockState(topPos);
                BlockState supportState = level.getBlockState(belowPos);
                boolean validSupport = supportState.isFaceSturdy(level, belowPos, Direction.UP);

                if (topState.is(Blocks.SNOW)) {
                    int layers = topState.getValue(SnowLayerBlock.LAYERS);
                    if (layers < Math.min(maxLayers, 8)) {
                        BlockState newState = topState.setValue(SnowLayerBlock.LAYERS, layers + 1);
                        Block.pushEntitiesUp(topState, newState, level, topPos);
                        level.setBlockAndUpdate(topPos, newState);
                    }
                } else if (topState.isAir() && validSupport) {
                    level.setBlockAndUpdate(topPos, Blocks.SNOW.defaultBlockState());
                }
            }
        }

        BlockState belowState = level.getBlockState(belowPos);
        Biome.Precipitation precipitation = (type == WeatherTypes.FREEZING_RAIN)
                ? Biome.Precipitation.RAIN
                : Biome.Precipitation.SNOW;
        belowState.getBlock().handlePrecipitation(belowState, level, belowPos, precipitation);
    }
}
