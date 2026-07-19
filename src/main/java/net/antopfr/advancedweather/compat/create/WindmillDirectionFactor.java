package net.antopfr.advancedweather.compat.create;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class WindmillDirectionFactor {

    public static float compute(Direction facing, Vec3 windDirection) {
        double windLenSq = windDirection.x * windDirection.x + windDirection.z * windDirection.z;
        if (windLenSq < 0.0001) return 0.6f;

        Vec3 windNormalized = new Vec3(windDirection.x, 0, windDirection.z).normalize();
        Vec3 facingFlat = new Vec3(facing.getStepX(), 0, facing.getStepZ());

        if (facingFlat.lengthSqr() < 0.0001) {
            return 0.7f;
        }
        facingFlat = facingFlat.normalize();

        double dot = windNormalized.x * facingFlat.x + windNormalized.z * facingFlat.z;
        double alignment = Math.abs(dot);

        return (float) Mth.lerp(alignment, 0.15, 1.0);
    }
}
