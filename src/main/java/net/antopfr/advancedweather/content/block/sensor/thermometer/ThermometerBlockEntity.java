package net.antopfr.advancedweather.content.block.sensor.thermometer;

import net.antopfr.advancedweather.content.block.sensor.IWeatherSensor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ThermometerBlockEntity extends BlockEntity implements IWeatherSensor {

    public ThermometerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public SensorType getSensorType() { return SensorType.THERMOMETER; }

    @Override
    public boolean isValidlyPlaced(ServerLevel level, BlockPos pos) {
        if (level.dimension() != Level.OVERWORLD) return true;
        if (level.canSeeSky(pos.above())) return false;

        int openSides = 0;
        for (var dir : Direction.Plane.HORIZONTAL) {
            if (!level.getBlockState(pos.relative(dir)).isSolidRender(level, pos.relative(dir))) {
                openSides++;
            }
        }
        return openSides >= 2;
    }
}
