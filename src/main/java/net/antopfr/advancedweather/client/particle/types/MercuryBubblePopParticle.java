package net.antopfr.advancedweather.client.particle.types;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.BubblePopParticle;
import net.minecraft.client.particle.SpriteSet;

public class MercuryBubblePopParticle extends BubblePopParticle {
    protected MercuryBubblePopParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed, sprites);
    }
}
