package net.antopfr.advancedweather.weather.effect.types.rainbows;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Random;

public class RainbowEntityRenderer extends EntityRenderer<RainbowEntity> {

    private static final float PRIMARY_MIN   = 40.6f;
    private static final float PRIMARY_MAX   = 42.3f;
    private static final float SECONDARY_MIN = 51.0f;
    private static final float SECONDARY_MAX = 53.0f;
    private static final int   SEGMENTS      = 64;
    private static final float DISTANCE      = 220f;

    private static float shimmerTime = 0f;

    private static final int[] RAINBOW_BANDS_RGB = {
            0xE22E2B, 0xFF661C, 0xFFCD05, 0x78E520, 0x1F71E2, 0x1F1FE0, 0x6B29CE
    };
    private static final int BANDS = RAINBOW_BANDS_RGB.length;

    public RainbowEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull RainbowEntity entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {

        float intensity = entity.getIntensity();
        if (intensity < 0.01f) return;

        // Scale de croissance — l'arc "grandit" depuis le sol au spawn
        float growScale = entity.getGrowScale();
        if (growScale < 0.01f) return;

        shimmerTime += 0.008f;
        float shimmer = 0.9f + 0.1f * Mth.sin(shimmerTime * 1.3f);

        Vec3 antiSunVec = entity.getAntiSunDirection();
        Vector3f antiSun = new Vector3f((float) antiSunVec.x, (float) antiSunVec.y, (float) antiSunVec.z).normalize();

        // L'entité EST l'apex du cône — poseStack déjà centré là
        Vector3f up = Math.abs(antiSun.y) > 0.99f ? new Vector3f(1, 0, 0) : new Vector3f(0, 1, 0);
        Vector3f right = new Vector3f(up).cross(antiSun).normalize();
        up = new Vector3f(antiSun).cross(right).normalize();

        VertexConsumer consumer = bufferSource.getBuffer(RainbowRenderType.rainbow());
        PoseStack.Pose pose = poseStack.last();

        float effectiveAlpha = intensity * shimmer;

        buildHalo(consumer, pose, antiSun, right, up, PRIMARY_MIN - 1.5f, PRIMARY_MAX + 1.0f, effectiveAlpha, growScale);
        buildArc(consumer, pose, antiSun, right, up, PRIMARY_MIN, PRIMARY_MAX, false, effectiveAlpha, 0.85f, growScale);
        buildArc(consumer, pose, antiSun, right, up, SECONDARY_MIN, SECONDARY_MAX, true, effectiveAlpha, 0.3f, growScale);
        spawnHaloParticles(entity, antiSun, right, up, growScale, intensity);
    }

    private void buildArc(VertexConsumer consumer, PoseStack.Pose pose,
                          Vector3f axis, Vector3f right, Vector3f up,
                          float angleMinDeg, float angleMaxDeg, boolean inverted,
                          float intensity, float alphaMul, float growScale) {

        float angleMinRad = (float) Math.toRadians(angleMinDeg);
        float angleMaxRad = (float) Math.toRadians(angleMaxDeg);
        int halfSegments = SEGMENTS / 2;
        float distance = DISTANCE * growScale;

        Vector3f[][] ring = new Vector3f[BANDS + 1][halfSegments + 1];

        for (int b = 0; b <= BANDS; b++) {
            float t = (float) b / BANDS;
            float angleRad = Mth.lerp(t, angleMinRad, angleMaxRad);

            for (int i = 0; i <= halfSegments; i++) {
                float theta = (float) (Math.PI * i / halfSegments);
                float cosT = Mth.cos(theta);
                float sinT = Mth.sin(theta);

                ring[b][i] = new Vector3f(axis).mul(Mth.cos(angleRad))
                        .add(new Vector3f(right).mul(cosT * Mth.sin(angleRad)))
                        .add(new Vector3f(up).mul(sinT * Mth.sin(angleRad)))
                        .normalize().mul(distance);
            }
        }

        for (int b = 0; b < BANDS; b++) {
            float t0 = (float) b / BANDS;
            float t1 = (float) (b + 1) / BANDS;
            float bandFade = edgeFade((t0 + t1) / 2f);

            for (int i = 0; i < halfSegments; i++) {
                float thetaA = (float) (Math.PI * i / halfSegments);
                float thetaC = (float) (Math.PI * (i + 1) / halfSegments);

                float fadeA = endpointFade(thetaA);
                float fadeC = endpointFade(thetaC);

                int colorA = bandToArgb(b, inverted, intensity * alphaMul * bandFade * fadeA);
                int colorC = bandToArgb(b, inverted, intensity * alphaMul * bandFade * fadeC);

                Vector3f a = ring[b][i];
                Vector3f c = ring[b][i + 1];
                Vector3f bb = ring[b + 1][i];
                Vector3f d = ring[b + 1][i + 1];

                consumer.addVertex(pose, a.x, a.y, a.z).setColor(colorA).setLight(15728880);
                consumer.addVertex(pose, bb.x, bb.y, bb.z).setColor(colorA).setLight(15728880);
                consumer.addVertex(pose, c.x, c.y, c.z).setColor(colorC).setLight(15728880);

                consumer.addVertex(pose, bb.x, bb.y, bb.z).setColor(colorA).setLight(15728880);
                consumer.addVertex(pose, d.x, d.y, d.z).setColor(colorC).setLight(15728880);
                consumer.addVertex(pose, c.x, c.y, c.z).setColor(colorC).setLight(15728880);
            }
        }
    }

    private void buildHalo(VertexConsumer consumer, PoseStack.Pose pose,
                           Vector3f axis, Vector3f right, Vector3f up,
                           float angleMinDeg, float angleMaxDeg, float intensity, float growScale) {

        float angleMinRad = (float) Math.toRadians(angleMinDeg);
        float angleMaxRad = (float) Math.toRadians(angleMaxDeg);
        int halfSegments = SEGMENTS / 2;
        float distance = DISTANCE * growScale;

        Vector3f[] inner = new Vector3f[halfSegments + 1];
        Vector3f[] outer = new Vector3f[halfSegments + 1];

        for (int i = 0; i <= halfSegments; i++) {
            float theta = (float) (Math.PI * i / halfSegments);
            float cosT = Mth.cos(theta);
            float sinT = Mth.sin(theta);

            inner[i] = new Vector3f(axis).mul(Mth.cos(angleMinRad))
                    .add(new Vector3f(right).mul(cosT * Mth.sin(angleMinRad)))
                    .add(new Vector3f(up).mul(sinT * Mth.sin(angleMinRad)))
                    .normalize().mul(distance);

            outer[i] = new Vector3f(axis).mul(Mth.cos(angleMaxRad))
                    .add(new Vector3f(right).mul(cosT * Mth.sin(angleMaxRad)))
                    .add(new Vector3f(up).mul(sinT * Mth.sin(angleMaxRad)))
                    .normalize().mul(distance);
        }

        for (int i = 0; i < halfSegments; i++) {
            float thetaA = (float) (Math.PI * i / halfSegments);
            float thetaC = (float) (Math.PI * (i + 1) / halfSegments);

            float fadeA = endpointFade(thetaA);
            float fadeC = endpointFade(thetaC);

            int colorA = (Mth.clamp((int)(intensity * 0.18f * fadeA * 255), 0, 255) << 24) | 0xFFFFFF;
            int colorC = (Mth.clamp((int)(intensity * 0.18f * fadeC * 255), 0, 255) << 24) | 0xFFFFFF;

            Vector3f a = inner[i];
            Vector3f c = inner[i + 1];
            Vector3f bb = outer[i];
            Vector3f d = outer[i + 1];

            consumer.addVertex(pose, a.x, a.y, a.z).setColor(colorA).setLight(15728880);
            consumer.addVertex(pose, bb.x, bb.y, bb.z).setColor(colorA).setLight(15728880);
            consumer.addVertex(pose, c.x, c.y, c.z).setColor(colorC).setLight(15728880);

            consumer.addVertex(pose, bb.x, bb.y, bb.z).setColor(colorA).setLight(15728880);
            consumer.addVertex(pose, d.x, d.y, d.z).setColor(colorC).setLight(15728880);
            consumer.addVertex(pose, c.x, c.y, c.z).setColor(colorC).setLight(15728880);
        }
    }

    private static final Random particleRandom = new Random();

    private void spawnHaloParticles(RainbowEntity entity, Vector3f axis, Vector3f right, Vector3f up,
                                    float growScale, float intensity) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;
        if (mc.isPaused()) return;
        if (intensity < 0.1f) return;

        // Seulement 1 particule toutes les quelques frames pour rester discret
        if (particleRandom.nextFloat() > 0.15f) return;

        float angleDeg = Mth.lerp(particleRandom.nextFloat(), PRIMARY_MIN - 1.0f, PRIMARY_MAX + 0.5f);
        float angleRad = (float) Math.toRadians(angleDeg);
        float theta = particleRandom.nextFloat() * (float) Math.PI;

        float cosT = Mth.cos(theta);
        float sinT = Mth.sin(theta);
        float distance = DISTANCE * growScale;

        Vector3f dir = new Vector3f(axis).mul(Mth.cos(angleRad))
                .add(new Vector3f(right).mul(cosT * Mth.sin(angleRad)))
                .add(new Vector3f(up).mul(sinT * Mth.sin(angleRad)))
                .normalize().mul(distance);

        double px = entity.getX() + dir.x;
        double py = entity.getY() + dir.y;
        double pz = entity.getZ() + dir.z;

        mc.level.addParticle(
                ParticleTypes.END_ROD,
                px, py, pz,
                0.0, 0.01, 0.0
        );
    }

    private float endpointFade(float theta) {
        float fadeZone = 0.25f;
        float t = theta / (float) Math.PI;

        float fadeStart = Mth.clamp(t / fadeZone, 0f, 1f);
        float fadeEnd   = Mth.clamp((1f - t) / fadeZone, 0f, 1f);

        return Math.min(fadeStart, fadeEnd);
    }

    private float edgeFade(float t) {
        return Mth.clamp(Math.min(t / 0.15f, (1f - t) / 0.15f), 0f, 1f);
    }

    private int bandToArgb(int bandIndex, boolean inverted, float alpha) {
        int idx = inverted ? (BANDS - 1 - bandIndex) : bandIndex;
        idx = Mth.clamp(idx, 0, BANDS - 1);
        int rgb = RAINBOW_BANDS_RGB[idx];

        int ir = (rgb >> 16) & 0xFF;
        int ig = (rgb >> 8) & 0xFF;
        int ib = rgb & 0xFF;
        int ia = Mth.clamp((int)(alpha * 255), 0, 255);

        return (ia << 24) | (ir << 16) | (ig << 8) | ib;
    }

    @Override
    public ResourceLocation getTextureLocation(RainbowEntity entity) {
        return null;
    }

    @Override
    public boolean shouldRender(@NotNull RainbowEntity entity, @NotNull Frustum frustum, double camX, double camY, double camZ) {
        return true;
    }
}