package net.antopfr.advancedweather.server.event;

import net.antopfr.advancedweather.content.item.AWItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.Random;

@EventBusSubscriber(modid = "advancedweather")
public class HorseBrushEvent {
    private static final Random random = new Random();

    @SubscribeEvent
    public static void onHorseBrush(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof Horse horse))
            return;
        Player player = event.getEntity();
        ItemStack heldItem = player.getItemInHand(event.getHand());

        if (!heldItem.is(Items.BRUSH))
            return;
        if (horse.level().isClientSide())
            return;
        if (random.nextFloat() < 0.25f) {
            ItemStack hair = new ItemStack(AWItems.HORSEHAIR.get());
            horse.spawnAtLocation(hair);
            horse.level().playSound(null, horse.blockPosition(), SoundEvents.HORSE_SADDLE, SoundSource.PLAYERS, 1.0F,1.5F);
        }
        if (player instanceof ServerPlayer sp) {
            heldItem.hurtAndBreak(1, sp.serverLevel(), sp, item -> {});
        }
    }
}
