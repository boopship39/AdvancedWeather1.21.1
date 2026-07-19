package net.antopfr.advancedweather.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class PlayerChecks {

    private static final int CHECK_HEIGHT = 24;
    private static final int LATERAL_DISTANCE = 4;
    private static final int LATERAL_HEIGHT_CHECK = 6;

    public static boolean isShielded(Level level, BlockPos pos) {
        boolean hasNoSky = level.dimension() == Level.NETHER || level.dimension() == Level.END;

        if (hasNoSky) {
            return isShieldedNoSky(level, pos);
        }
        return isShieldedOverworld(level, pos);
    }

    private static boolean isShieldedOverworld(Level level, BlockPos pos) {
        if (!level.canSeeSky(pos.above())) return true;

        int blockedDirections = 0;
        int totalDirections = 0;

        BlockPos[] directions = {
                pos.north(LATERAL_DISTANCE),
                pos.south(LATERAL_DISTANCE),
                pos.east(LATERAL_DISTANCE),
                pos.west(LATERAL_DISTANCE)
        };

        for (BlockPos dirPos : directions) {
            totalDirections++;
            boolean blocked = false;

            int blockedHeights = 0;
            int checkedHeights = 0;
            for (int h = 1; h <= LATERAL_HEIGHT_CHECK; h += 2) {
                checkedHeights++;
                if (!level.canSeeSky(dirPos.above(h))) {
                    blockedHeights++;
                }
            }
            if (blockedHeights >= checkedHeights - 1) {
                blocked = true;
            }
            if (blocked) blockedDirections++;
        }

        return blockedDirections >= 3;
    }

    private static boolean isShieldedNoSky(Level level, BlockPos pos) {
        int ceilingDistance = -1;
        for (int i = 1; i <= CHECK_HEIGHT; i++) {
            if (!level.isEmptyBlock(pos.above(i))) {
                ceilingDistance = i;
                break;
            }
        }

        if (ceilingDistance == -1) return false;
        if (ceilingDistance <= 3) return true;

        int openSides = 0;
        BlockPos[] directions = {
                pos.north(LATERAL_DISTANCE),
                pos.south(LATERAL_DISTANCE),
                pos.east(LATERAL_DISTANCE),
                pos.west(LATERAL_DISTANCE)
        };

        for (BlockPos dirPos : directions) {
            boolean lateralOpen = true;
            for (int i = 1; i <= Math.min(ceilingDistance, CHECK_HEIGHT); i++) {
                if (!level.isEmptyBlock(dirPos.above(i))) {
                    lateralOpen = false;
                    break;
                }
            }
            if (lateralOpen) openSides++;
        }

        return openSides < 2;
    }
}
