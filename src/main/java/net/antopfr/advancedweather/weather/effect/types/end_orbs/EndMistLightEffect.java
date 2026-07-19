package net.antopfr.advancedweather.weather.effect.types.end_orbs;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.data.PointLightData;
import foundry.veil.api.client.render.light.renderer.LightRenderHandle;
import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.weather.WeatherEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@EventBusSubscriber(modid = "advancedweather", value = Dist.CLIENT)
public class EndMistLightEffect {

    private static final int MAX_ORBS = 12;
    private static final float SPAWN_RADIUS = 40f;
    private static final float DESPAWN_RADIUS = 55f;
    private static final float MIN_RADIUS = 70f;
    private static final Random random = new Random();

    private static final List<MistOrb> orbs = new ArrayList<>();
    private static boolean wasActive = false;

    private static class MistOrb {
        LightRenderHandle<PointLightData> handle;
        Vec3 origin;
        float bobOffset;
        float bobSpeed;
        float driftAngle;
        float driftSpeed;
        int life;
        int maxLife;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            clearAll();
            return;
        }

        boolean active = ClientWeatherState.hasEffect(WeatherEffects.END_GROUND_FOG);

        if (!active) {
            if (wasActive) clearAll();
            wasActive = false;
            return;
        }
        wasActive = true;

        orbs.removeIf(orb -> {
            orb.life++;
            double dist = mc.player.position().distanceTo(orb.origin);
            if (dist > DESPAWN_RADIUS || orb.life > orb.maxLife) {
                orb.handle.free();
                return true;
            }
            return false;
        });

        if (orbs.size() < MAX_ORBS && random.nextFloat() < 0.04f) {
            spawnOrb(mc);
        }

        long time = mc.level.getGameTime();
        for (MistOrb orb : orbs) {
            float t = (time + orb.bobOffset) * orb.bobSpeed;
            float bobY = Mth.sin(t) * 1.2f;

            orb.driftAngle += orb.driftSpeed;
            double driftX = Math.cos(orb.driftAngle) * 0.02;
            double driftZ = Math.sin(orb.driftAngle) * 0.02;

            orb.origin = orb.origin.add(driftX, 0, driftZ);

            PointLightData data = orb.handle.getLightData();
            data.setPosition(orb.origin.x, orb.origin.y + bobY, orb.origin.z);

            float pulse = 0.7f + 0.3f * Mth.sin(t * 0.5f);
            data.setBrightness(pulse * fadeForLife(orb));
            orb.handle.markDirty();
        }
    }

    private static float fadeForLife(MistOrb orb) {
        int fadeIn = 60;
        int fadeOut = 60;
        if (orb.life < fadeIn) return orb.life / (float) fadeIn;
        if (orb.life > orb.maxLife - fadeOut) return Math.max(0f, (orb.maxLife - orb.life) / (float) fadeOut);
        return 1f;
    }

    private static void spawnOrb(Minecraft mc) {
        Vec3 playerPos = mc.player.position();

        double angle = random.nextDouble() * Math.PI * 2;
        double dist = MIN_RADIUS + random.nextDouble() * (SPAWN_RADIUS);
        double offsetX = Math.cos(angle) * dist;
        double offsetZ = Math.sin(angle) * dist;
        double offsetY = -3 + random.nextDouble() * 14;

        Vec3 origin = playerPos.add(offsetX, offsetY, offsetZ);

        float hueVariant = random.nextFloat();
        float r = Mth.lerp(hueVariant, 0.55f, 0.75f);
        float g = Mth.lerp(hueVariant, 0.35f, 0.55f);
        float b = 0.95f;

        PointLightData data = new PointLightData()
                .setPosition(origin.x, origin.y, origin.z)
                .setRadius(8.0f)
                .setColor(r, g, b)
                .setBrightness(0f);

        LightRenderHandle<PointLightData> handle = VeilRenderSystem.renderer()
                .getLightRenderer()
                .addLight(data);

        MistOrb orb = new MistOrb();
        orb.handle = handle;
        orb.origin = origin;
        orb.bobOffset = random.nextFloat() * 1000f;
        orb.bobSpeed = 0.02f + random.nextFloat() * 0.015f;
        orb.driftAngle = random.nextFloat() * (float) Math.PI * 2;
        orb.driftSpeed = (random.nextFloat() - 0.5f) * 0.01f;
        orb.life = 0;
        orb.maxLife = 400 + random.nextInt(400);

        orbs.add(orb);
    }

    private static void clearAll() {
        for (MistOrb orb : orbs) {
            orb.handle.free();
        }
        orbs.clear();
    }
}