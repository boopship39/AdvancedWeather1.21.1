package net.antopfr.advancedweather.server.ai;

import net.antopfr.advancedweather.weather.WeatherManager;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.EnumSet;

public class SeekShelterGoal extends Goal {

    private final PathfinderMob mob;
    private BlockPos shelterPos = null;
    private int searchCooldown = 0;

    public SeekShelterGoal(PathfinderMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!(mob.level() instanceof ServerLevel level)) return false;
        if (!isShelterWeather(WeatherManager.get(level).getCurrentWeather(level))) return false;

        return !isUnderCover(mob.blockPosition());
    }

    @Override
    public boolean canContinueToUse() {
        if (!(mob.level() instanceof ServerLevel level)) return false;
        if (!isShelterWeather(WeatherManager.get(level).getCurrentWeather(level))) return false;
        if (isUnderCover(mob.blockPosition())) return false;
        return shelterPos != null;
    }

    @Override
    public void start() {
        shelterPos = findShelter();
        if (shelterPos != null) {
            mob.getNavigation().moveTo(
                    shelterPos.getX() + 0.5,
                    shelterPos.getY(),
                    shelterPos.getZ() + 0.5,
                    1
            );
        }
    }

    @Override
    public void tick() {
        searchCooldown--;

        if (searchCooldown <= 0) {
            searchCooldown = 60;
            shelterPos = findShelter();
            if (shelterPos != null) {
                mob.getNavigation().moveTo(
                        shelterPos.getX() + 0.5,
                        shelterPos.getY(),
                        shelterPos.getZ() + 0.5,
                        1
                );
            }
        }
    }

    @Override
    public void stop() {
        shelterPos = null;
        mob.getNavigation().stop();
    }

    private static boolean isShelterWeather(WeatherTypes w) {
        return w == WeatherTypes.HAIL
                || w == WeatherTypes.BLIZZARD
                || w == WeatherTypes.SANDSTORM
                || w == WeatherTypes.THUNDERSTORM;
    }

    private boolean isUnderCover(BlockPos pos) {
        ServerLevel level = (ServerLevel) mob.level();
        BlockPos sky = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos);
        return sky.getY() > pos.getY() + mob.getBbHeight() + 1;
    }

    private BlockPos findShelter() {
        ServerLevel level = (ServerLevel) mob.level();
        BlockPos origin = mob.blockPosition();
        int radius = 16;
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;

        for (int dx = -radius; dx <= radius; dx += 2) {
            for (int dz = -radius; dz <= radius; dz += 2) {
                BlockPos candidate = origin.offset(dx, 0, dz);
                BlockPos sky = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, candidate);

                if (sky.getY() <= candidate.getY()) continue;

                BlockPos floor = sky.below();
                if (!level.getBlockState(floor).isSolid()) continue;
                if (!level.isEmptyBlock(floor.above())) continue;
                if (!level.isEmptyBlock(floor.above(2))) continue;

                double dist = candidate.distSqr(origin);
                if (dist < bestDist) {
                    bestDist = dist;
                    best = floor.above();
                }
            }
        }
        return best;
    }
}