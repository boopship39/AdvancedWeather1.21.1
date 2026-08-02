package net.antopfr.advancedweather.content.block;

import net.antopfr.advancedweather.content.item.AWItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class AluminumBlock extends Block {

    public AluminumBlock(Properties props) {
        super(props);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, @NotNull BlockState state, @NotNull Level level,
                                                       @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hit) {
        if (stack.is(AWItems.MERCURY_VIAL.get())) {
            if (!level.isClientSide) {
                level.setBlockAndUpdate(pos,
                        AWBlocks.AMALGAMATING_ALUMINUM.get().defaultBlockState());
                level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW,
                        SoundSource.BLOCKS, 0.7f, 1.4f);

                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                    ItemStack empty = new ItemStack(AWItems.EMPTY_VIAL.get());
                    if (!player.addItem(empty)) player.drop(empty, false);
                }
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
