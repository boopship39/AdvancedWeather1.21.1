package net.antopfr.advancedweather.content.block.sensor;

import net.createmod.catnip.outliner.Outliner;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SensorHighlighter {

    private static final int DURATION_TICKS = 100;

    public static void show(List<BlockPos> valid, List<BlockPos> invalid) {

        int i = 0;
        for (BlockPos pos : valid) {
            Outliner.getInstance().showAABB("aw_sensor_" + (i++), new AABB(pos), DURATION_TICKS)
                    .colored(0x4AE05A) // green
                    .lineWidth(1 / 16f);
        }
        for (BlockPos pos : invalid) {
            Outliner.getInstance().showAABB("aw_sensor_" + (i++), new AABB(pos), DURATION_TICKS)
                    .colored(0xE0A030) // amber
                    .lineWidth(1 / 16f);
        }
    }
}