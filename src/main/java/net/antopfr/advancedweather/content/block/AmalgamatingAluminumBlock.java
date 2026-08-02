package net.antopfr.advancedweather.content.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class AmalgamatingAluminumBlock extends Block implements EntityBlock {

    public AmalgamatingAluminumBlock(Properties props) {
        super(props);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return AWBlocks.AMALGAMATING_ALUMINUM_BE.create(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return (lvl, pos, st, be) -> {
            if (be instanceof AmalgamatingAluminumBlockEntity crystal) {
                if (lvl.isClientSide) {
                    crystal.clientTick(lvl, pos);
                } else {
                    crystal.serverTick((ServerLevel) lvl, pos);
                }
            }
        };
    }
}
