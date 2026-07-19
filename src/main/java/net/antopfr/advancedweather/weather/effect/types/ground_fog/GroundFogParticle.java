package net.antopfr.advancedweather.weather.effect.types.ground_fog;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.config.AWClientConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class GroundFogParticle extends TextureSheetParticle {
    AWClientConfig config = AWClientConfig.get();

    private final float WIDTH_SCALE = (float) config.groundFogWidthScale;
    private final float baseAlpha;

    protected GroundFogParticle(ClientLevel level, double x, double y, double z,
                                double xSpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z);

        this.pickSprite(sprites);

        this.rCol = 1.0f;
        this.gCol = 1.0f;
        this.bCol = 1.0f;

        this.alpha = 0f;

        this.xd = xSpeed;
        this.yd = 0f;
        this.zd = zSpeed;

        this.baseAlpha = (float) (config.groundFogAlphaMin +
                random.nextFloat() * (config.groundFogAlphaMax - config.groundFogAlphaMin));
        this.quadSize = (float) (config.groundFogSizeMin +
                random.nextFloat() * (config.groundFogSizeMax - config.groundFogSizeMin));

        this.gravity = 0f;
        this.friction = 0.995f;
        this.hasPhysics = false;
        this.lifetime = (int) (config.groundFogLifetimeMin +
                random.nextFloat() * (config.groundFogLifetimeMax - config.groundFogLifetimeMin));

        this.updateParticleColor();
    }

    @Override
    public void tick() {
        super.tick();

        this.updateParticleColor();

        float fadeIn  = Math.min(1f, (float) age / 60f);
        float fadeOut = Math.min(1f, (float) (lifetime - age) / 60f);
        float pulse = 1f + 0.15f * (float) Math.sin(age * 0.05f);
        this.alpha = baseAlpha * fadeIn * fadeOut * pulse;
    }

    /**
     * Calcule et applique la couleur du fog selon le biome sous la particule.
     */
    private void updateParticleColor() {
        if (this.level == null) return;

        ResourceLocation biomeLocation = this.level.getBiome(BlockPos.containing(this.x, this.y, this.z))
                .unwrapKey()
                .map(ResourceKey::location)
                .orElse(null);

        int color = ClientWeatherState.fogColorLerp.getCurrentColor();

        this.rCol = ((color >> 16) & 0xFF) / 255.0f;
        this.gCol = ((color >> 8) & 0xFF) / 255.0f;
        this.bCol = (color & 0xFF) / 255.0f;
    }

    @Override
    public int getLightColor(float partialTick) {
        if (this.level == null) return 0;

        BlockPos pos = BlockPos.containing(this.x, this.y + 0.75, this.z);

        if (this.level.hasChunkAt(pos)) {
            int packedLight = LevelRenderer.getLightColor(this.level, pos);

            int sky = (packedLight >> 16) & 0xFFFF;
            int block = packedLight & 0xFFFF;

            // Le brouillard est une substance volatile qui capte très bien la lumière ambiante.
            // On lui impose un seuil minimum de luminosité du ciel (ex: 6) pour qu'il reste
            // visible et vaporeux en extérieur sous les arbres, plutôt que de devenir opaque/sombre.
            sky = Math.max(sky, 6);

            // Repackage des valeurs dans l'entier 32 bits attendu par le shader de Minecraft
            return (sky << 16) | block;
        }

        return 0;
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        float x = (float)(this.xo + (this.x - this.xo) * partialTick - camera.getPosition().x);
        float y = (float)(this.yo + (this.y - this.yo) * partialTick - camera.getPosition().y);
        float z = (float)(this.zo + (this.z - this.zo) * partialTick - camera.getPosition().z);

        float w = this.quadSize * WIDTH_SCALE;
        float h = this.quadSize;

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightColor(partialTick);

        consumer.addVertex(x - w, y, z - h).setUv(u0, v1).setColor(rCol, gCol, bCol, alpha).setLight(light);
        consumer.addVertex(x - w, y, z + h).setUv(u0, v0).setColor(rCol, gCol, bCol, alpha).setLight(light);
        consumer.addVertex(x + w, y, z + h).setUv(u1, v0).setColor(rCol, gCol, bCol, alpha).setLight(light);
        consumer.addVertex(x + w, y, z - h).setUv(u1, v1).setColor(rCol, gCol, bCol, alpha).setLight(light);
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
            return new GroundFogParticle(level, x, y, z, xSpeed, zSpeed, sprites);
        }
    }
}