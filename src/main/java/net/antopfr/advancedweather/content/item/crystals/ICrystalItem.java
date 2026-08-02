package net.antopfr.advancedweather.content.item.crystals;

import net.antopfr.advancedweather.weather.AtmosphericForcing;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public interface ICrystalItem {
    int tint();

    AtmosphericForcing.Bias bias();

    default void crystalInventoryTick(Level level, Entity entity, boolean selected) {
        if (!level.isClientSide || !selected) return;
        if (!(entity instanceof Player player)) return;

        float favor = CrystalFavor.favor(bias());
        int count = 1 + (int) (favor * 4);

        Vec3 look = player.getLookAngle();
        Vec3 right = new Vec3(-look.z, 0, look.x).normalize();
        double hx = player.getX() + look.x * 0.4 + right.x * 0.4;
        double hy = player.getEyeY() - 0.2;
        double hz = player.getZ() + look.z * 0.4 + right.z * 0.4;

        Vector3f color = new Vector3f(
                ((tint() >> 16) & 0xFF) / 255f,
                ((tint() >> 8) & 0xFF) / 255f,
                (tint() & 0xFF) / 255f);
        DustParticleOptions dust = new DustParticleOptions(color, 1.0f);

        for (int i = 0; i < count; i++) {
            level.addParticle(dust,
                    hx + (level.random.nextDouble() - 0.5) * 0.2,
                    hy + (level.random.nextDouble() - 0.5) * 0.2,
                    hz + (level.random.nextDouble() - 0.5) * 0.2,
                    0, 0.02, 0);
        }
    }
}
