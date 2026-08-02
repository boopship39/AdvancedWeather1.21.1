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

public class FrostCrystalItem extends Item implements ICrystalItem {

    public FrostCrystalItem(Properties props) { super(props); }

    @Override public int tint() { return 0x9BE0F0; }
    @Override public AtmosphericForcing.Bias bias() { return AtmosphericForcing.Bias.COOLING; }

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
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        AWTooltips.append(tooltip, "item.advancedweather.frost_crystal.tip", 3);
    }
}
