package net.antopfr.advancedweather.compat.jei;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.content.block.AWBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CalibrationCategory implements IRecipeCategory<CalibrationRecipe> {

    public static final RecipeType<CalibrationRecipe> TYPE =
            RecipeType.create(AdvancedWeather.MOD_ID, "calibration", CalibrationRecipe.class);

    private static final int WIDTH = 90;
    private static final int HEIGHT = 26;

    private final IDrawable icon;

    public CalibrationCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableItemStack(new ItemStack(AWBlocks.CALIBRATION_BENCH.get()));
    }

    @Override
    public @NotNull RecipeType<CalibrationRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("advancedweather.jei.category.calibration");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CalibrationRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 4, 5)
                .addItemStack(recipe.input())
                .setStandardSlotBackground();
        builder.addSlot(RecipeIngredientRole.OUTPUT, 68, 5)
                .addItemStack(recipe.output())
                .setStandardSlotBackground();
    }

    @Override
    public void draw(@NotNull CalibrationRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView,
                     GuiGraphics g, double mouseX, double mouseY) {
        g.drawString(Minecraft.getInstance().font, "→", 42, 9, 0xFF808080, false);
    }
}
