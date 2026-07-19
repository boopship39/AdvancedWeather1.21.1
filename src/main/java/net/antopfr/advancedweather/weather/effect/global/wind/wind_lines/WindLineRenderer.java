package net.antopfr.advancedweather.weather.effect.global.wind.wind_lines;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.antopfr.advancedweather.client.state.ClientAtmosphereState;
import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.config.AWClientConfig;
import net.antopfr.advancedweather.util.PlayerChecks;
import net.antopfr.advancedweather.weather.effect.global.wind.WindBurstManager;
import net.antopfr.advancedweather.weather.WeatherEffects;
import net.antopfr.advancedweather.weather.effect.global.wind.WindDirection;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = "advancedweather", value = Dist.CLIENT)
public class WindLineRenderer {

    private static final List<WindLine> lines = new ArrayList<>();
    private static final Random random = new Random();

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        AWClientConfig config = AWClientConfig.get();
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        boolean hasWindActive = ClientWeatherState.hasEffect(WeatherEffects.WIND_LINES)
                || ClientWeatherState.hasEffect(WeatherEffects.NETHER_WIND_LINES)
                || ClientWeatherState.hasEffect(WeatherEffects.END_WIND_LINES);

        if (!hasWindActive) {
            lines.clear();
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        Camera camera = event.getCamera();
        Vec3 camPos = camera.getPosition();

        if (mc.level == null || mc.player == null || mc.isPaused()) return;
        if (PlayerChecks.isShielded(mc.level, mc.player.blockPosition())) return;

        if (config.windBurstEnabled && WindBurstManager.getHeavyIntensity() <= 0.01f) {
            return;
        }

        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
        Vec3 windDir = WindDirection.get(partialTick);

        float intensity = ClientAtmosphereState.getWindIntensity();
        int maxLines = (int)(AWClientConfig.get().windLineMax * intensity);
        if (!(ClientAtmosphereState.getWindIntensity() < 0.4f)) {
            while (lines.size() < maxLines) {
                spawnLine(camPos, windDir);
            }
        }

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();

        MultiBufferSource.BufferSource bufferSource = mc.renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.LINES);
        Matrix4f matrix = poseStack.last().pose();

        Iterator<WindLine> it = lines.iterator();
        while (it.hasNext()) {
            WindLine line = it.next();
            line.life += partialTick * line.speed * 0.01f;

            if (line.life >= 1.0f) {
                it.remove();
                continue;
            }

            Vec3 offset = windDir.scale(line.life * 40.0);
            Vec3 pos = line.start.add(offset);

            double rx = pos.x - camPos.x;
            double ry = pos.y - camPos.y;
            double rz = pos.z - camPos.z;

            float alpha = line.opacity;
            if (line.life < 0.1f) alpha *= line.life / 0.1f;
            if (line.life > 0.8f) alpha *= (1.0f - line.life) / 0.2f;

            alpha *= WindBurstManager.getHeavyIntensity();

            Vec3 end = windDir.scale(line.length);

            consumer.addVertex(matrix, (float) rx, (float) ry, (float) rz)
                    .setColor(1f, 1f, 1f, alpha)
                    .setNormal(poseStack.last(), (float) windDir.x, (float) windDir.y, (float) windDir.z);

            consumer.addVertex(matrix, (float) (rx + end.x), (float) (ry + end.y), (float) (rz + end.z))
                    .setColor(1f, 1f, 1f, alpha)
                    .setNormal(poseStack.last(), (float) windDir.x, (float) windDir.y, (float) windDir.z);
        }

        bufferSource.endBatch(RenderType.LINES);
        poseStack.popPose();
    }

    private static void spawnLine(Vec3 camPos, Vec3 windDir) {
        AWClientConfig config = AWClientConfig.get();
        double spawnDist = config.windLineSpawnDistMin + random.nextFloat() * (config.windLineSpawnDistMax - config.windLineSpawnDistMin);
        double lateralSpread = (random.nextFloat() - 0.5f) * config.windLineLateralSpread;
        double verticalSpread = (random.nextFloat() - 0.5f) * config.windLineHeightSpread;

        Vec3 lateral = new Vec3(-windDir.z, 0, windDir.x);
        double x = camPos.x - windDir.x * spawnDist + lateral.x * lateralSpread;
        double y = camPos.y + verticalSpread;
        double z = camPos.z - windDir.z * spawnDist + lateral.z * lateralSpread;

        float length = (float) (config.windLineLengthMin + random.nextFloat() * (config.windLineLengthMax - config.windLineLengthMin));
        float speed = (float) (config.windLineSpeedMin + random.nextFloat() * (config.windLineSpeedMax - config.windLineSpeedMin));

        lines.add(new WindLine(new Vec3(x, y, z), windDir, length, speed));
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        WindBurstManager.tick();
    }
}