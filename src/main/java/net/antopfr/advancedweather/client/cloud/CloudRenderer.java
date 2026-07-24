package net.antopfr.advancedweather.client.cloud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexBuffer;
import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.config.AWClientConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.Objects;

@EventBusSubscriber(modid = AdvancedWeather.MOD_ID, value = Dist.CLIENT)
public class CloudRenderer {

    private static VertexBuffer buffer;
    private static int builtCellX = Integer.MIN_VALUE, builtCellZ = Integer.MIN_VALUE;
    private static float builtThreshold = -1f;
    private static int builtRadius = -1;
    private static float builtSunAngle = Float.MIN_VALUE;
    private static int builtCamY = Integer.MIN_VALUE;
    private static boolean empty = true;

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || !CloudState.enabled()) return;
        if (mc.level.dimension() != Level.OVERWORLD) return;

        Camera cam = event.getCamera();
        Vec3 camPos = cam.getPosition();

        float s = CloudState.CELL_SIZE;
        int radiusCells = Math.max(4, (int) (AWClientConfig.get().cloudRenderDistance / s));

        double cloudX = camPos.x - CloudState.scrollX();
        double cloudZ = camPos.z - CloudState.scrollZ();
        int centerCellX = Mth.floor(cloudX / s);
        int centerCellZ = Mth.floor(cloudZ / s);

        float threshold = CloudState.threshold();
        float sunAngle = CloudState.sunAngle();
        int camYq = Mth.floor(camPos.y / 16.0) * 16;

        if (centerCellX != builtCellX || centerCellZ != builtCellZ
                || threshold != builtThreshold || radiusCells != builtRadius
                || sunAngle != builtSunAngle || camYq != builtCamY) {
            rebuild(centerCellX, centerCellZ, radiusCells, threshold, sunAngle);
        }
        if (empty) return;

        double meshWorldX = centerCellX * s + CloudState.scrollX();
        double meshWorldZ = centerCellZ * s + CloudState.scrollZ();

        Matrix4f mv = new Matrix4f(RenderSystem.getModelViewMatrix());
        mv.translate((float) (meshWorldX - camPos.x),
                (float) (CloudState.altitude() - camPos.y),
                (float) (meshWorldZ - camPos.z));

        int tint = CloudState.tint();
        float tr = ((tint >> 16) & 0xFF) / 255f;
        float tg = ((tint >> 8) & 0xFF) / 255f;
        float tb = (tint & 0xFF) / 255f;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        buffer.bind();

        RenderSystem.colorMask(false, false, false, false);
        RenderSystem.depthMask(true);
        buffer.drawWithShader(mv, event.getProjectionMatrix(),
                Objects.requireNonNull(RenderSystem.getShader()));

        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthMask(false);
        RenderSystem.setShaderColor(tr, tg, tb, CloudState.opacity());
        buffer.drawWithShader(mv, event.getProjectionMatrix(), RenderSystem.getShader());

        VertexBuffer.unbind();

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
    }

    private static void rebuild(int cellX, int cellZ, int radiusCells, float threshold,
                                float sunAngle) {
        MeshData mesh = CloudMeshBuilder.build(cellX, cellZ, radiusCells, threshold);

        builtCellX = cellX;
        builtCellZ = cellZ;
        builtRadius = radiusCells;
        builtThreshold = threshold;
        builtSunAngle = sunAngle;

        if (mesh == null) {
            empty = true;
            return;
        }
        empty = false;

        if (buffer == null) {
            buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        }
        buffer.bind();
        buffer.upload(mesh);
        VertexBuffer.unbind();
    }
}
