package net.antopfr.advancedweather.weather.effect.types.ash_particles;

import net.antopfr.advancedweather.config.AWClientConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class AshParticle extends TextureSheetParticle {

    protected AshParticle(ClientLevel level, double x, double y, double z,
                          double xSpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);

        AWClientConfig config = AWClientConfig.get();

        this.pickSprite(sprites);

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;
        this.alpha = 0.5f + (random.nextFloat() * 0.4f);

        this.xd = xSpeed;
        this.yd = -0.005f + (random.nextFloat() * 0.02f);
        this.zd = zSpeed;

        this.gravity = 0.002f;
        this.friction = 0.96f;
        this.lifetime = 60 + random.nextInt(40);
        this.hasPhysics = true;

        this.quadSize = (float) (config.sandSizeMin + random.nextFloat() * (config.sandSizeMax - config.sandSizeMin)) * 0.75f;

        this.roll = this.random.nextFloat() * ((float)Math.PI * 2F);
    }

    @Override
    public void tick() {
        super.tick();
        this.xd *= 0.98f;
        this.zd *= 0.98f;

        this.yd += (random.nextFloat() - 0.5f) * 0.002f;

        this.oRoll = this.roll;
        this.roll += 0.02f;
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
            return new AshParticle(level, x, y, z, xSpeed, zSpeed, sprites);
        }
    }
}
