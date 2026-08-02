package net.antopfr.advancedweather.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.antopfr.advancedweather.content.block.AmalgamatingAluminumBlockEntity;
import net.antopfr.advancedweather.content.item.AWItems;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class AmalgamatingAluminumRenderer implements BlockEntityRenderer<AmalgamatingAluminumBlockEntity> {

    private final ItemRenderer itemRenderer;
    private final ItemStack crystal;

    public AmalgamatingAluminumRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.crystal = new ItemStack(AWItems.DORMANT_CRYSTAL.get());
    }

    @Override
    public void render(AmalgamatingAluminumBlockEntity be, float partialTick,
                       @NotNull PoseStack pose, @NotNull MultiBufferSource buffers,
                       int light, int overlay) {

        float growth = be.growthFraction();
        if (growth <= 0.01f) return;

        pose.pushPose();
        pose.translate(0.5, 1.0, 0.5);

        float emerge = Mth.lerp(growth, -0.5f, 0.0f);
        pose.translate(0.0, emerge, 0.0);

        float scale = Mth.lerp(growth, 0.6f, 1.1f);
        pose.scale(scale, scale, scale);

        int fullBright = LightTexture.pack(15, 15);
        itemRenderer.renderStatic(crystal, ItemDisplayContext.GROUND,
                fullBright, OverlayTexture.NO_OVERLAY, pose, buffers,
                be.getLevel(), 0);

        pose.popPose();
    }
}
