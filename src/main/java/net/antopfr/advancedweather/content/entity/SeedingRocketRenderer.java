package net.antopfr.advancedweather.content.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class SeedingRocketRenderer extends EntityRenderer<SeedingRocketEntity> {

    private final ItemRenderer itemRenderer;

    public SeedingRocketRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(SeedingRocketEntity entity, float yaw, float partialTick,
                       PoseStack pose, @NotNull MultiBufferSource buffers, int light) {
        pose.pushPose();
        pose.mulPose(this.entityRenderDispatcher.cameraOrientation());
        this.itemRenderer.renderStatic(entity.getItem(), ItemDisplayContext.GROUND,
                light, OverlayTexture.NO_OVERLAY, pose, buffers,
                entity.level(), entity.getId());
        pose.popPose();
        super.render(entity, yaw, partialTick, pose, buffers, light);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull SeedingRocketEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
