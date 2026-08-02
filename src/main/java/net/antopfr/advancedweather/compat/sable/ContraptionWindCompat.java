package net.antopfr.advancedweather.compat.sable;

import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.block.propeller.BlockEntitySubLevelPropellerActor;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.platform.SableEventPlatform;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.antopfr.advancedweather.config.AWCommonConfig;
import net.antopfr.advancedweather.weather.WeatherManager;
import net.antopfr.advancedweather.weather.effect.global.wind.WindDirectionCalc;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public final class ContraptionWindCompat {

    private static final double DRAG_COEFF = 0.6;

    private static final double AREA_PER_SURFACE = 1.0;
    private static final double HULL_AREA_FACTOR = 0.15;

    public static void register() {
        SableEventPlatform.INSTANCE.onPhysicsTick(ContraptionWindCompat::onPhysicsTick);
    }

    private static void onPhysicsTick(SubLevelPhysicsSystem system, double timeStep) {
        if (!AWCommonConfig.get().windAffectsContraptions) return;

        ServerLevel level = system.getLevel();
        SubLevelContainer c = SubLevelContainer.getContainer(level);
        if (!(c instanceof ServerSubLevelContainer container)) return;

        float intensity = WeatherManager.get(level).getWindIntensity(level);
        if (intensity <= 0.01f) return;

        double speed = (intensity * intensity * 120.0) / 3.6;

        Vec3 dir = WindDirectionCalc.getDirection(level.getDayTime(), 0f);
        Vector3d windWorld = new Vector3d(dir.x * speed, 0.0, dir.z * speed);

        for (ServerSubLevel sub : container.getAllSubLevels()) {
            if (!isAerodynamic(sub)) continue;

            RigidBodyHandle handle = RigidBodyHandle.of(sub);
            if (!handle.isValid()) continue;

            MassData mass = sub.getMassTracker();
            if (mass.isInvalid()) continue;

            Vector3d vel = handle.getLinearVelocity(new Vector3d());
            Vector3d rel = new Vector3d(windWorld).sub(vel.x, 0.0, vel.z);
            double relSpeed = rel.length();
            if (relSpeed < 0.01) continue;

            double pressure = DimensionPhysicsData.getAirPressure(level, sub.logicalPose().position());

            double force = DRAG_COEFF * exposedArea(sub) * relSpeed * relSpeed * pressure
                    * AWCommonConfig.get().contraptionWindStrength;

            Vector3d worldImpulse = rel.normalize(new Vector3d())
                    .mul(force * timeStep);

            Vector3d localImpulse = sub.logicalPose().orientation()
                    .transformInverse(worldImpulse, new Vector3d());

            sub.getOrCreateQueuedForceGroup(AWForceGroups.WIND.get())
                    .applyAndRecordPointForce(centerOfPressure(sub), localImpulse);
        }
    }

    private static boolean isAerodynamic(ServerSubLevel sub) {
        ServerLevelPlot plot = sub.getPlot();

        if (!plot.getLiftProviders().isEmpty()) return true;

        if (sub.getFloatingBlockController().needsTicking()) return true;

        for (BlockEntitySubLevelActor actor : plot.getBlockEntityActors()) {
            if (actor instanceof BlockEntitySubLevelPropellerActor) return true;
        }
        return false;
    }

    private static double exposedArea(ServerSubLevel sub) {
        int surfaces = sub.getPlot().getLiftProviders().size();
        double hull = Math.cbrt(Math.max(1.0, sub.getMassTracker().getMass()));
        return surfaces * AREA_PER_SURFACE + hull * hull * HULL_AREA_FACTOR;
    }

    private static Vector3d centerOfPressure(ServerSubLevel sub) {
        var providers = sub.getPlot().getLiftProviders();

        if (providers.isEmpty()) {
            Vector3dc com = sub.getMassTracker().getCenterOfMass();
            return com != null ? new Vector3d(com) : new Vector3d();
        }

        Vector3d sum = new Vector3d();
        for (var ctx : providers) {
            BlockPos p = ctx.pos();
            sum.add(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5);
        }
        return sum.div(providers.size());
    }
}
