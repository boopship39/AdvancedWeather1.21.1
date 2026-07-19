package net.antopfr.advancedweather.content.block.sensor.barometer;

import net.antopfr.advancedweather.content.block.sensor.IWeatherSensor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BarometerBlockEntity extends BlockEntity implements IWeatherSensor {

    public BarometerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public SensorType getSensorType() { return IWeatherSensor.SensorType.BAROMETER; }

    @Override
    public boolean isValidlyPlaced(ServerLevel level, BlockPos pos) {
        return true;
    }
}
