package net.antopfr.advancedweather.weather.effect.types.blizzard_particles;

import net.antopfr.advancedweather.config.AWClientConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class BlizzardParticle extends TextureSheetParticle {

    protected BlizzardParticle(ClientLevel level, double x, double y, double z,
                               double xSpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);

        AWClientConfig config = AWClientConfig.get();

        this.pickSprite(sprites);

        this.rCol = 1.0f;
        this.gCol = 1.0f;
        this.bCol = 1.0f;
        this.alpha = 0.8f;

        this.xd = xSpeed;
        this.yd = -0.02f + (random.nextFloat() * 0.02f);
        this.zd = zSpeed;

        this.gravity = 0.005f;
        this.friction = 0.98f;
        this.lifetime = 80 + random.nextInt(40);
        this.hasPhysics = true;

        this.quadSize = (float) (config.blizzardSizeMin +
                        random.nextFloat() * config.blizzardSizeMax - config.blizzardSizeMin);
    }

    @Override
    public void tick() {
        super.tick();
        this.xd *= 0.98f;
        this.zd *= 0.98f;
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
            return new BlizzardParticle(level, x, y, z, xSpeed, zSpeed, sprites);
        }
    }
}
