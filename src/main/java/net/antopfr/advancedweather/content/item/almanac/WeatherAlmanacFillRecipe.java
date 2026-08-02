package net.antopfr.advancedweather.content.item.almanac;

import net.antopfr.advancedweather.config.AWCommonConfig;
import net.antopfr.advancedweather.content.AWRecipeSerializers;
import net.antopfr.advancedweather.content.AWDataComponents;
import net.antopfr.advancedweather.content.item.AWItems;
import net.antopfr.advancedweather.content.WeatherRecord;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WeatherAlmanacFillRecipe extends CustomRecipe {

    public WeatherAlmanacFillRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int almanacs = 0, reports = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.is(AWItems.WEATHER_ALMANAC.get())) almanacs++;
            else if (stack.is(AWItems.WEATHER_REPORT.get())
                    && stack.has(AWDataComponents.WEATHER_RECORD.get())) reports++;
            else return false;
        }
        return almanacs == 1 && reports >= 1;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack almanac = ItemStack.EMPTY;
        List<WeatherRecord> incoming = new ArrayList<>();

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.is(AWItems.WEATHER_ALMANAC.get())) {
                almanac = stack;
            } else if (stack.is(AWItems.WEATHER_REPORT.get())) {
                WeatherRecord r = stack.get(AWDataComponents.WEATHER_RECORD.get());
                if (r != null) incoming.add(r);
            }
        }
        if (almanac.isEmpty()) return ItemStack.EMPTY;

        List<WeatherRecord> merged = new ArrayList<>(WeatherAlmanacItem.getRecords(almanac));
        int capacity = AWCommonConfig.get().almanacMaxRecords;

        for (WeatherRecord r : incoming) {
            if (merged.size() >= capacity) break;
            boolean duplicate = merged.stream().anyMatch(existing ->
                    existing.gameTime() == r.gameTime() && existing.pos().equals(r.pos())
                            && existing.dimension().equals(r.dimension()));
            if (!duplicate) merged.add(r);
        }
        merged.sort(Comparator.comparingLong(WeatherRecord::gameTime));

        ItemStack result = almanac.copyWithCount(1);
        result.set(AWDataComponents.WEATHER_ALMANAC.get(), List.copyOf(merged));
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AWRecipeSerializers.ALMANAC_FILL.get();
    }
}
