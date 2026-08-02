package net.antopfr.advancedweather.content.item.kite;

import net.antopfr.advancedweather.content.entity.KiteEntity;
import net.antopfr.advancedweather.content.AWDataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KiteItem extends Item {

    public KiteItem(Properties props) { super(props); }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            List<KiteEntity> existing = level.getEntitiesOfClass(KiteEntity.class,
                    player.getBoundingBox().inflate(24));

            boolean mine = existing.stream()
                    .anyMatch(k -> player.equals(k.getOwnerPlayer()));

            if (mine) {
                existing.stream()
                        .filter(k -> player.equals(k.getOwnerPlayer()))
                        .forEach(KiteEntity::discard);
                stack.remove(AWDataComponents.KITE_DEPLOYED.get());
            } else {
                KiteColors colors = stack.getOrDefault(
                        AWDataComponents.KITE_COLORS.get(), KiteColors.WHITE);
                KiteEntity kite = new KiteEntity(level, player, colors);
                level.addFreshEntity(kite);
                stack.set(AWDataComponents.KITE_DEPLOYED.get(), true);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
