package net.antopfr.advancedweather.weather.effect.global.ripples;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class RippleRenderType {

    private static final ResourceLocation RIPPLE_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("advancedweather", "textures/environment/ripple.png");

    private static final RenderType RIPPLES = RenderType.create(
            "advancedweather:rain_ripples",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            VertexFormat.Mode.TRIANGLES,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexColorShader))
                    .setTextureState(new RenderStateShard.TextureStateShard(RIPPLE_TEXTURE, false, false))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .createCompositeState(false)
    );

    public static RenderType ripples() {
        return RIPPLES;
    }
}