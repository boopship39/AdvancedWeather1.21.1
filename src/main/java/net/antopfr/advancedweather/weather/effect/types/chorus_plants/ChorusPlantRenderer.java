package net.antopfr.advancedweather.weather.effect.types.chorus_plants;

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

public class ChorusPlantRenderer extends EntityRenderer<ChorusPlantEntity> {

    public ChorusPlantRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(ChorusPlantEntity entity, float yaw, float partialTick,
                       PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();
        poseStack.translate(0, 0.4f, 0);

        float rot = entity.getVisualRotation(partialTick);
        poseStack.mulPose(Axis.ZP.rotationDegrees(rot));
        poseStack.mulPose(Axis.XP.rotationDegrees(rot * 0.4f));
        poseStack.mulPose(Axis.YP.rotationDegrees(rot * 0.3f));

        poseStack.translate(-0.5, -0.5, -0.5);

        Minecraft mc = Minecraft.getInstance();
        BlockRenderDispatcher dispatcher = mc.getBlockRenderer();

        dispatcher.renderSingleBlock(
                Blocks.CHORUS_PLANT.defaultBlockState(),
                poseStack,
                buffer,
                packedLight,
                OverlayTexture.NO_OVERLAY
        );

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ChorusPlantEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
