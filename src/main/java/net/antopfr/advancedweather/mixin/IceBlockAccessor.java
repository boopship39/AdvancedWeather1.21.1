package net.antopfr.advancedweather.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(IceBlock.class)
public interface IceBlockAccessor {

    @Invoker("melt")
    void invokeMelt(BlockState state, Level level, BlockPos pos);
}