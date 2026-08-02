package net.antopfr.advancedweather.content.block;

import net.antopfr.advancedweather.content.item.AWItems;
import net.antopfr.advancedweather.weather.LocalAtmosphere;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class AmalgamatingAluminumBlockEntity extends BlockEntity {

    private int progress = 0;
    private static final int MAX_PROGRESS = 2400;

    public AmalgamatingAluminumBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void serverTick(ServerLevel level, BlockPos pos) {
        float humidity = LocalAtmosphere.getLocalHumidity(level, pos);
        float rate = Mth.lerp(humidity / 100f, 0.4f, 2.0f);

        progress += Math.max(1, Math.round(rate));

        if (progress % 20 == 0) {
            level.sendBlockUpdated(pos, getBlockState(), getBlockState(), 3);
            setChanged();
        }

        if (progress >= MAX_PROGRESS) {
            mature(level, pos);
        }
    }

    private void mature(ServerLevel level, BlockPos pos) {
        level.setBlockAndUpdate(pos, AWBlocks.ALUMINUM_BLOCK.get().defaultBlockState());

        ItemStack crystal = new ItemStack(AWItems.DORMANT_CRYSTAL.get());
        ItemEntity drop = new ItemEntity(level,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, crystal);
        drop.setDeltaMovement(0, 0.2, 0);
        level.addFreshEntity(drop);

        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1f, 1f);
    }

    public void clientTick(Level level, BlockPos pos) {
        float growth = growthFraction();
        if (growth <= 0.05f) return;

        RandomSource r = level.random;
        int count = 1 + (int) (growth * 3);

        for (int i = 0; i < count; i++) {
            if (r.nextFloat() > 0.6f) continue;

            double px = pos.getX() + 0.3 + r.nextDouble() * 0.4;
            double py = pos.getY() + 1.0 + r.nextDouble() * 0.3 * growth;
            double pz = pos.getZ() + 0.3 + r.nextDouble() * 0.4;

            level.addParticle(
                    new BlockParticleOption(ParticleTypes.BLOCK,
                            AWBlocks.ALUMINUM_BLOCK.get().defaultBlockState()),
                    px, py, pz,
                    (r.nextDouble() - 0.5) * 0.02,
                    0.02 + r.nextDouble() * 0.02,
                    (r.nextDouble() - 0.5) * 0.02);
        }
    }

    public float growthFraction() {
        return Mth.clamp(progress / (float) MAX_PROGRESS, 0f, 1f);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("progress", progress);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        progress = tag.getInt("progress");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        tag.putInt("progress", progress);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
