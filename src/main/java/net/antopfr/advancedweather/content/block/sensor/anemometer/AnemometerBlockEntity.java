package net.antopfr.advancedweather.content.block.sensor.anemometer;

import net.antopfr.advancedweather.content.block.sensor.IWeatherSensor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class AnemometerBlockEntity extends BlockEntity implements IWeatherSensor {

    public AnemometerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public SensorType getSensorType() { return SensorType.ANEMOMETER; }

    @Override
    public boolean isValidlyPlaced(ServerLevel level, BlockPos pos) {
        if (level.dimension() != Level.OVERWORLD) return true;

        if (!level.canSeeSky(pos.above())) return false;
        for (var dir : Direction.Plane.HORIZONTAL) {
            BlockPos side = pos.relative(dir);
            if (level.getBlockState(side).isSolidRender(level, side)) {
                return false;
            }
        }
        return true;
    }
}