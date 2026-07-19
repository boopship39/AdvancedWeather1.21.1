package net.antopfr.advancedweather.weather.effect.types.tumbleweeds;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class TumbleweedRenderer extends EntityRenderer<TumbleweedEntity> {

    public TumbleweedRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(TumbleweedEntity entity, float yaw, float partialTick,
                       PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        poseStack.translate(0, 0.35f, 0);

        float rot = entity.getVisualRotation(partialTick);
        poseStack.mulPose(Axis.ZP.rotationDegrees(rot));
        poseStack.mulPose(Axis.XP.rotationDegrees(rot * 0.5f));

        // Centre le bloc (le modèle de bloc va de 0 à 1)
        poseStack.translate(-0.5, -0.5, -0.5);

        Minecraft mc = Minecraft.getInstance();
        BlockRenderDispatcher dispatcher = mc.getBlockRenderer();

        dispatcher.renderSingleBlock(
                Blocks.DEAD_BUSH.defaultBlockState(),
                poseStack,
                buffer,
                packedLight,
                OverlayTexture.NO_OVERLAY
        );

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull TumbleweedEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
