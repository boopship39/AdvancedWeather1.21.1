package net.antopfr.advancedweather.network.toserver;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.content.item.AWItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record CommitCalibrationPacket(BlockPos benchPos, InteractionHand hand)
        implements CustomPacketPayload {

    public static final Type<CommitCalibrationPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, "commit_calibration"));

    public static final StreamCodec<FriendlyByteBuf, CommitCalibrationPacket> CODEC = StreamCodec.of(
            (buf, pkt) -> {
                buf.writeBlockPos(pkt.benchPos);
                buf.writeEnum(pkt.hand);
            },
            buf -> new CommitCalibrationPacket(buf.readBlockPos(), buf.readEnum(InteractionHand.class)));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    public static void handle(CommitCalibrationPacket pkt, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = ctx.player();
            if (!pkt.benchPos().closerToCenterThan(player.position(), 8.0)) return;

            ItemStack held = player.getItemInHand(pkt.hand());
            ItemStack calibrated = calibratedForm(held);
            if (calibrated.isEmpty()) return;

            held.shrink(1);
            if (!player.getInventory().add(calibrated)) player.drop(calibrated, false);

            player.level().playSound(null, pkt.benchPos(),
                    SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.7f, 1.0f);
        });
    }

    private static ItemStack calibratedForm(ItemStack raw) {
        if (raw.is(AWItems.SPRING.get()))            return new ItemStack(AWItems.CALIBRATED_SPRING.get());
        if (raw.is(AWItems.CUP_ROTOR.get()))         return new ItemStack(AWItems.CALIBRATED_CUP_ROTOR.get());
        if (raw.is(AWItems.SENSITIVE_FIBER.get()))   return new ItemStack(AWItems.CALIBRATED_SENSITIVE_FIBER.get());
        return ItemStack.EMPTY;
    }
}
