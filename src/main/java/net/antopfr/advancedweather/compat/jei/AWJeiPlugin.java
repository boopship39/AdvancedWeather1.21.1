package net.antopfr.advancedweather.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.content.AWDataComponents;
import net.antopfr.advancedweather.content.block.AWBlocks;
import net.antopfr.advancedweather.content.item.AWItems;
import net.antopfr.advancedweather.content.item.kite.KiteColors;
import net.antopfr.advancedweather.content.item.kite.KiteCraftRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@JeiPlugin
public class AWJeiPlugin implements IModPlugin {

    private static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, "jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CalibrationCategory(helper));
        registration.addRecipeCategories(new AmalgamationCategory(helper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CalibrationCategory.TYPE, CalibrationRecipe.all());
        registration.addRecipes(AmalgamationCategory.TYPE, AmalgamationRecipe.all());
        registration.addRecipes(RecipeTypes.CRAFTING, kiteExamples());

        registration.addIngredientInfo(
                new ItemStack(AWItems.KITE_ITEM.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("advancedweather.jei.info.kite"));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(AWBlocks.CALIBRATION_BENCH.get()),
                CalibrationCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(AWBlocks.ALUMINUM_BLOCK.get()),
                AmalgamationCategory.TYPE);
    }

    private static List<RecipeHolder<CraftingRecipe>> kiteExamples() {
        List<RecipeHolder<CraftingRecipe>> out = new ArrayList<>();

        out.add(kiteExample("kite_example",
                DyeColor.RED, DyeColor.YELLOW, DyeColor.GREEN, DyeColor.BLUE));

        return out;
    }

    private static RecipeHolder<CraftingRecipe> kiteExample(String id, DyeColor topLeft, DyeColor topRight, DyeColor bottomLeft, DyeColor bottomRight) {

        NonNullList<Ingredient> ing = NonNullList.withSize(9, Ingredient.EMPTY);
        ing.set(0, Ingredient.of(DyeItem.byColor(topLeft)));
        ing.set(1, Ingredient.of(Items.PAPER));
        ing.set(2, Ingredient.of(DyeItem.byColor(topRight)));
        ing.set(3, Ingredient.EMPTY);
        ing.set(4, Ingredient.of(Items.LEAD));
        ing.set(5, Ingredient.EMPTY);
        ing.set(6, Ingredient.of(DyeItem.byColor(bottomLeft)));
        ing.set(7, Ingredient.of(AWItems.ALUMINUM_NUGGET.get()));
        ing.set(8, Ingredient.of(DyeItem.byColor(bottomRight)));

        ItemStack result = new ItemStack(AWItems.KITE_ITEM.get());
        result.set(AWDataComponents.KITE_COLORS.get(), new KiteColors(
                KiteCraftRecipe.dyeColor(new ItemStack(DyeItem.byColor(topRight))),
                KiteCraftRecipe.dyeColor(new ItemStack(DyeItem.byColor(topLeft))),
                KiteCraftRecipe.dyeColor(new ItemStack(DyeItem.byColor(bottomRight))),
                KiteCraftRecipe.dyeColor(new ItemStack(DyeItem.byColor(bottomLeft)))));

        ShapedRecipePattern pattern = new ShapedRecipePattern(3, 3, ing, Optional.empty());
        ShapedRecipe recipe = new ShapedRecipe("", CraftingBookCategory.MISC, pattern, result);

        return new RecipeHolder<>(ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, id), recipe);
    }
}
