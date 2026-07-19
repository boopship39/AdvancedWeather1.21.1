package net.antopfr.advancedweather.mixin.client;

import net.antopfr.advancedweather.client.state.ClientAtmosphereState;
import net.antopfr.advancedweather.config.AWClientConfig;
import net.antopfr.advancedweather.weather.effect.global.wind.WindDirection;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Particle.class)
public class ParticleMixin {

    @Unique
    private static final double BASE_PUSH = 0.02;

    @ModifyVariable(method = "move", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private double advancedweather$windX(double x) {
        return x + advancedweather$windOffset(true);
    }

    @ModifyVariable(method = "move", at = @At("HEAD"), argsOnly = true, ordinal = 2)
    private double advancedweather$windZ(double z) {
        return z + advancedweather$windOffset(false);
    }

    @Unique
    private static double advancedweather$windOffset(boolean xAxis) {
        AWClientConfig config = AWClientConfig.get();
        if (!config.windPushesParticles) return 0.0;

        float intensity = ClientAtmosphereState.getWindIntensity();
        if (intensity <= 0.01f) return 0.0;

        Vec3 dir = WindDirection.get(0f);
        double push = intensity * BASE_PUSH * config.windParticleStrength;
        return (xAxis ? dir.x : dir.z) * push;
    }
}
