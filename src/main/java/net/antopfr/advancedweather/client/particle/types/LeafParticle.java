package net.antopfr.advancedweather.client.particle.types;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class LeafParticle extends TextureSheetParticle {

    public LeafParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int tintColor, SpriteSet sprites) {
        super(level, x, y, z);

        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;

        this.lifetime = 80 + this.random.nextInt(80);

        float r = (float)(tintColor >> 16 & 255) / 255.0F;
        float g = (float)(tintColor >> 8 & 255) / 255.0F;
        float b = (float)(tintColor & 255) / 255.0F;

        this.rCol = r * (0.9F + this.random.nextFloat() * 0.1F);
        this.gCol = g * (0.9F + this.random.nextFloat() * 0.1F);
        this.bCol = b * (0.9F + this.random.nextFloat() * 0.1F);

        this.quadSize *= 0.8F + this.random.nextFloat() * 0.4F;

        this.hasPhysics = true;
        this.pickSprite(sprites);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.xd *= 0.98;
        this.zd *= 0.98;

        this.yd -= 0.008;

        this.xd += Math.sin(this.age * 0.2) * 0.01;

        this.move(this.xd, this.yd, this.zd);

        if (this.onGround) {
            this.xd = 0;
            this.zd = 0;
            this.yd = 0;
            if (this.age < this.lifetime - 20) {
                this.age = this.lifetime - 20;
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public static SpriteSet CURRENT_SPRITES;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
            CURRENT_SPRITES = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new LeafParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, 0xFFFFFF, this.sprites);
        }
    }
}
