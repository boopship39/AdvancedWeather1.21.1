package net.antopfr.advancedweather.content.item.almanac;

import net.antopfr.advancedweather.content.item.AWDataComponents;
import net.antopfr.advancedweather.content.report.WeatherRecord;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WeatherAlmanacItem extends Item {

    public WeatherAlmanacItem(Properties properties) {
        super(properties);
    }

    public static List<WeatherRecord> getRecords(ItemStack stack) {
        List<WeatherRecord> records = stack.get(AWDataComponents.WEATHER_ALMANAC.get());
        return records != null ? records : List.of();
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player,
                                                           @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            WeatherAlmanacScreenOpener.open(stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        List<WeatherRecord> records = getRecords(stack);
        if (records.isEmpty()) {
            tooltip.add(Component.literal("§8Empty - craft with weather reports to fill"));
            return;
        }
        tooltip.add(Component.literal("§7Contains §f" + records.size() + "§7 report"
                + (records.size() > 1 ? "s" : "")));
        long firstDay = records.getFirst().gameTime() / 24000L;
        long lastDay = records.getLast().gameTime() / 24000L;
        tooltip.add(Component.literal(firstDay == lastDay
                ? "§8Day " + firstDay
                : "§8Day " + firstDay + " - Day " + lastDay));
    }
}
