package net.antopfr.advancedweather.client.gui;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import foundry.veil.api.client.render.VeilRenderBridge;
import foundry.veil.api.client.render.VeilRenderSystem;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class StationBackdropRenderType {

    private static final ResourceLocation SHADER_ID =
            ResourceLocation.fromNamespaceAndPath("advancedweather", "station_backdrop");

    private static final RenderType BACKDROP = RenderType.create(
            "advancedweather:station_backdrop",
            DefaultVertexFormat.POSITION_TEX_COLOR,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(() -> {
                        var program = VeilRenderSystem.renderer().getShaderManager().getShader(SHADER_ID);
                        return program != null ? VeilRenderBridge.toShaderInstance(program) : null;
                    }))
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                    .createCompositeState(false)
    );

    public static RenderType backdrop() {
        return BACKDROP;
    }
}
