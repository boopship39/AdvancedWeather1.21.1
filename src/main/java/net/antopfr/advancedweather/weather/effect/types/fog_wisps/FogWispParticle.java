package net.antopfr.advancedweather.weather.effect.types.fog_wisps;

import net.antopfr.advancedweather.config.AWClientConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class FogWispParticle extends TextureSheetParticle {

    private final float baseAlpha;

    protected FogWispParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);

        AWClientConfig config = AWClientConfig.get();

        this.pickSprite(sprites);

        this.rCol = 0.85f;
        this.gCol = 0.87f;
        this.bCol = 0.90f;

        this.alpha = 0f;
        this.baseAlpha = (float) (config.fogWispAlphaMin +
                        random.nextFloat() * (config.fogWispAlphaMax - config.fogWispAlphaMin));

        this.quadSize = (float) (config.fogWispSizeMin +
                        random.nextFloat() * (config.fogWispSizeMax - config.fogWispSizeMin));

        this.xd = (random.nextDouble() - 0.5) * 0.008;
        this.yd = (random.nextDouble() - 0.5) * 0.002;
        this.zd = (random.nextDouble() - 0.5) * 0.008;

        this.gravity = 0f;
        this.friction = 0.99f;
        this.lifetime = config.fogWispLifetimeMin +
                random.nextInt(config.fogWispLifetimeMax - config.fogWispLifetimeMin);
        this.hasPhysics = false;
    }

    @Override
    public void tick() {
        super.tick();

        float fadeIn = Math.min(1f, (float) age / 40f);
        float fadeOut = Math.min(1f, (float) (lifetime - age) / 40f);
        this.alpha = baseAlpha * fadeIn * fadeOut;

        this.xd += Math.sin(age * 0.05) * 0.0003;
        this.zd += Math.cos(age * 0.05) * 0.0003;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new FogWispParticle(level, x, y, z, sprites);
        }
    }
}
