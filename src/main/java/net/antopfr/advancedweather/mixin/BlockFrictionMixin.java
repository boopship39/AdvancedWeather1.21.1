package net.antopfr.advancedweather.mixin;

import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(targets = "net.neoforged.neoforge.common.extensions.IBlockExtension")
public interface BlockFrictionMixin {

    @Inject(
            method = "getFriction(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F",
            at = @At("RETURN"),
            cancellable = true
    )
    private void onGetFriction(BlockState state, LevelReader level, BlockPos pos,
                               @Nullable Entity entity, CallbackInfoReturnable<Float> cir) {
        if (!(entity instanceof LocalPlayer player)) return;
        if (ClientWeatherState.getCurrentWeather() != WeatherTypes.FREEZING_RAIN) return;

        int topY = player.level().getHeight(
                Heightmap.Types.WORLD_SURFACE,
                pos.getX(), pos.getZ()
        );
        if (pos.getY() < topY - 1) return;

        cir.setReturnValue(0.98f);
    }
}
