package net.antopfr.advancedweather.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
import net.antopfr.advancedweather.content.item.AWItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class AmalgamationCategory implements IRecipeCategory<AmalgamationRecipe> {

    public static final RecipeType<AmalgamationRecipe> TYPE =
            RecipeType.create(AdvancedWeather.MOD_ID, "amalgamation", AmalgamationRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slot;

    public AmalgamationCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(170, 70);
        this.icon = helper.createDrawableItemStack(new ItemStack(AWItems.DORMANT_CRYSTAL.get()));
        this.slot = helper.getSlotDrawable();
    }

    @Override
    public @NotNull RecipeType<AmalgamationRecipe> getRecipeType() { return TYPE; }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("advancedweather.jei.category.amalgamation");
    }

    @Override
    public @NotNull IDrawable getBackground() { return background; }

    @Override
    public @NotNull IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AmalgamationRecipe recipe,
                          @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 26)
                .setBackground(slot, -1, -1)
                .addItemStack(recipe.mercury());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 132, 26)
                .setBackground(slot, -1, -1)
                .addItemStack(recipe.output());
    }

    @Override
    public void draw(@NotNull AmalgamationRecipe recipe, @NotNull IRecipeSlotsView slots,
                     GuiGraphics graphics, double mouseX, double mouseY) {
        var font = Minecraft.getInstance().font;

        graphics.drawString(font, "→", 52, 30, 0xFF808080, false);
        graphics.drawString(font, "→", 110, 30, 0xFF808080, false);

        renderBlock3D(graphics, AWBlocks.ALUMINUM_BLOCK.get().defaultBlockState(), 85, 34);

        graphics.drawString(font,
                Component.translatable("advancedweather.jei.amalgamation.humidity"),
                4, 58, 0xFF5B8FD8, true);
    }

    private static void renderBlock3D(GuiGraphics graphics, BlockState state, int x, int y) {
        Minecraft mc = Minecraft.getInstance();
        PoseStack pose = graphics.pose();

        pose.pushPose();
        pose.translate(x, y, 100);
        pose.scale(20f, -20f, 20f);
        pose.mulPose(Axis.XP.rotationDegrees(30f));
        pose.mulPose(Axis.YP.rotationDegrees(45f));
        pose.translate(-0.5, -0.5, -0.5);

        MultiBufferSource.BufferSource buffers = mc.renderBuffers().bufferSource();
        mc.getBlockRenderer().renderSingleBlock(
                state, pose, buffers,
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        buffers.endBatch();

        pose.popPose();
    }
}
