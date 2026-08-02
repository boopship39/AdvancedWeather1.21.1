package net.antopfr.advancedweather.content;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.content.item.almanac.WeatherAlmanacFillRecipe;
import net.antopfr.advancedweather.content.item.kite.KiteCraftRecipe;
import net.antopfr.advancedweather.content.item.kite.KiteRecolorRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AWRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, AdvancedWeather.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<WeatherAlmanacFillRecipe>>
            ALMANAC_FILL = SERIALIZERS.register("almanac_fill",
            () -> new SimpleCraftingRecipeSerializer<>(WeatherAlmanacFillRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<KiteCraftRecipe>>
            KITE_CRAFT = SERIALIZERS.register("kite_craft",
            () -> new SimpleCraftingRecipeSerializer<>(KiteCraftRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<KiteRecolorRecipe>>
            KITE_RECOLOR = SERIALIZERS.register("kite_recolor",
            () -> new SimpleCraftingRecipeSerializer<>(KiteRecolorRecipe::new));

    public static void register(IEventBus modBus) {
        SERIALIZERS.register(modBus);
    }
}
