package net.antopfr.advancedweather.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.client.render.CloudHandler;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class CloudRendererMixin {
    @Inject(
            method = "renderClouds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderType;setupRenderState()V",
                    shift = At.Shift.AFTER
            )
    )
    private void aw_bindCloudTexture(CallbackInfo ci) {
        RenderSystem.setShaderTexture(0, CloudHandler.getCloudTexture());
    }

    @Inject(
            method = "renderClouds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderType;setupRenderState()V",
                    shift = At.Shift.AFTER
            )
    )
    private void aw_bindAndTintClouds(CallbackInfo ci) {
        ResourceLocation texture = CloudHandler.getCloudTexture();
        RenderSystem.setShaderTexture(0, texture);
        float aw_partialTick = 0f;
        if (CloudHandler.isTransitioning()) {

            float t = ClientWeatherState.getSmoothedTransitionProgress(aw_partialTick);
            float alpha = t < 0.5f
                    ? 1.0f - (t * 2f)
                    : (t - 0.5f) * 2f;

            if (t >= 0.5f) {
                RenderSystem.setShaderTexture(0, texture);
            } else {
                RenderSystem.setShaderTexture(0, CloudHandler.getPreviousCloudTexture());
            }

            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
        } else {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    @Shadow
    private boolean generateClouds;

    @Inject(method = "renderClouds", at = @At("HEAD"))
    private void aw_forceCloudRebuild(CallbackInfo ci) {
        if (CloudHandler.isTransitioning()) {
            this.generateClouds = true;
        }
    }
}
