package net.antopfr.advancedweather.content.block;

import net.antopfr.advancedweather.client.particle.AWParticles;
import net.antopfr.advancedweather.server.event.CauldronInteractionsEvent;
import net.antopfr.advancedweather.content.item.AWItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class MercuryCauldronBlock extends LayeredCauldronBlock {

    public MercuryCauldronBlock(Properties properties) {
        super(Biome.Precipitation.NONE, CauldronInteraction.EMPTY, properties);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack held, @NotNull BlockState state, @NotNull Level level,
                                                       @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hit) {
        if (held.is(AWItems.EMPTY_VIAL.get())) {
            if (!level.isClientSide) {
                held.consume(1, player);
                ItemStack filled = new ItemStack(AWItems.MERCURY_VIAL.get());
                if (!player.getInventory().add(filled)) player.drop(filled, false);

                lowerFillLevel(state, level, pos);
                level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1f, 0.7f);
                player.awardStat(Stats.USE_CAULDRON);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (held.is(AWItems.WASHED_CINNABAR_DUST.get())) {
            int lvl = state.getValue(LEVEL);
            if (lvl >= 3 || !CauldronInteractionsEvent.isHeated(level, pos)) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (!level.isClientSide) {
                held.consume(1, player);
                level.setBlockAndUpdate(pos, state.setValue(LEVEL, lvl + 1));
                level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 1.4f);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!CauldronInteractionsEvent.isHeated(level, pos)) return;

        int fillLevel = state.getValue(LEVEL);
        double surfaceY = pos.getY() + switch (fillLevel) {
            case 1 -> 0.62;
            case 2 -> 0.77;
            default -> 0.97;
        };
        if (random.nextFloat() < 0.6f) {
            double px = pos.getX() + 0.3125 + random.nextDouble() * 0.375;
            double pz = pos.getZ() + 0.3125 + random.nextDouble() * 0.375;

            level.addParticle(AWParticles.MERCURY_BUBBLE_POP.get(), px, surfaceY, pz, 0.0, 0.01, 0.0);
        }
        if (random.nextFloat() < 0.15f) {
            double px = pos.getX() + 0.4 + random.nextDouble() * 0.2;
            double pz = pos.getZ() + 0.4 + random.nextDouble() * 0.2;
            level.addParticle(ParticleTypes.SMOKE, px, surfaceY + 0.05, pz, 0.0, 0.02, 0.0);
        }
    }
}
