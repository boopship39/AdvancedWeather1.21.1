package net.antopfr.advancedweather.content.item.crystals;

import net.antopfr.advancedweather.util.AWTooltips;
import net.antopfr.advancedweather.weather.AtmosphericForcing;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RainCrystalItem extends Item implements ICrystalItem {

    public RainCrystalItem(Properties props) { super(props); }

    @Override public int tint() { return 0x5B8FD8; }
    @Override public AtmosphericForcing.Bias bias() { return AtmosphericForcing.Bias.SEEDING; }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.translatable(getDescriptionId(stack))
                .withStyle(s -> s.withColor(TextColor.fromRgb(tint())));
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        crystalInventoryTick(level, entity, selected);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        AWTooltips.append(tooltip, "item.advancedweather.rain_crystal.tip", 3);
    }
}
