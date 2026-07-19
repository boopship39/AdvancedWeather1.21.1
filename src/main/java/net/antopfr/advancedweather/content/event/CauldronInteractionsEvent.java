package net.antopfr.advancedweather.content.event;

import net.antopfr.advancedweather.content.block.AWBlocks;
import net.antopfr.advancedweather.content.item.AWItems;
import net.antopfr.advancedweather.util.AWTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LayeredCauldronBlock;

public class CauldronInteractionsEvent {

    public static void register() {
        CauldronInteraction.EMPTY.map().put(AWItems.WASHED_CINNABAR_DUST.get(),
                (state, level, pos, player, hand, stack) -> {
                    if (!isHeated(level, pos)) {
                        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                    }
                    if (!level.isClientSide) {
                        stack.consume(1, player);
                        level.setBlockAndUpdate(pos, AWBlocks.MERCURY_CAULDRON.getDefaultState()
                                .setValue(LayeredCauldronBlock.LEVEL, 1));
                        level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH,
                                SoundSource.BLOCKS, 0.5f, 1.4f);
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                });
        CauldronInteraction.WATER.map().put(AWItems.HORSEHAIR.get(),
                (state, level, pos, player, hand, stack) -> {
                    if (!level.isClientSide) {
                        stack.consume(1, player);
                        ItemStack fiber = new ItemStack(AWItems.SENSITIVE_FIBER.get());
                        if (!player.getInventory().add(fiber)) player.drop(fiber, false);
                        LayeredCauldronBlock.lowerFillLevel(state, level, pos);
                        level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.6f, 1.3f);
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                });
        CauldronInteraction.WATER.map().put(AWItems.CINNABAR_DUST.get(),
                (state, level, pos, player, hand, stack) -> {
                    if (!level.isClientSide) {
                        stack.consume(1, player);
                        ItemStack washed = new ItemStack(AWItems.WASHED_CINNABAR_DUST.get());
                        if (!player.getInventory().add(washed)) player.drop(washed, false);
                        LayeredCauldronBlock.lowerFillLevel(state, level, pos);
                        level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 0.5f, 1.2f);
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                });
    }

    public static boolean isHeated(Level level, BlockPos pos) {
        var below = level.getBlockState(pos.below());
        return below.is(AWTags.CAULDRON_HEAT_SOURCES);
    }
}
