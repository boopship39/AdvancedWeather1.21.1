package net.antopfr.advancedweather.content.block.station;

import net.antopfr.advancedweather.config.AWCommonConfig;
import net.antopfr.advancedweather.content.block.sensor.IWeatherSensor;
import net.antopfr.advancedweather.content.AWDataComponents;
import net.antopfr.advancedweather.content.item.AWItems;
import net.antopfr.advancedweather.content.WeatherRecord;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public class WeatherStationBlockEntity extends BlockEntity {

    public static final int SENSOR_SCAN_RADIUS = 8;
    private static final int SCAN_INTERVAL_TICKS = 100;

    private String stationName = "";

    public WeatherStationBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public record SensorStatus(BlockPos pos, boolean valid) {}

    private final Map<IWeatherSensor.SensorType, SensorStatus> sensors = new EnumMap<>(IWeatherSensor.SensorType.class);
    private int scanCooldown = 0;

    public boolean hasSensor(IWeatherSensor.SensorType type) {
        return sensors.containsKey(type);
    }

    public boolean hasAnySensor() {
        return !sensors.isEmpty();
    }

    public SensorStatus getSensor(IWeatherSensor.SensorType type) {
        return sensors.get(type);
    }

    public boolean hasFullSuite() {
        return sensors.size() == IWeatherSensor.SensorType.values().length;
    }

    public boolean sensorAvailable(IWeatherSensor.SensorType type) {
        if (!AWCommonConfig.get().stationRequiresSensors) return true;
        return hasSensor(type);
    }

    public boolean forecastAvailable() {
        if (!AWCommonConfig.get().stationRequiresSensors) return true;
        return hasFullSuite();
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String name) {
        this.stationName = name.length() > 32 ? name.substring(0, 32) : name;
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    public String displayName() {
        return stationName;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString("StationName", stationName);
        CompoundTag sensorsTag = new CompoundTag();
        sensors.forEach((type, status) -> {
            CompoundTag s = new CompoundTag();
            s.putLong("Pos", status.pos().asLong());
            s.putBoolean("Valid", status.valid());
            sensorsTag.put(type.name(), s);
        });
        tag.put("Sensors", sensorsTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        stationName = tag.getString("StationName");
        sensors.clear();
        CompoundTag sensorsTag = tag.getCompound("Sensors");
        for (IWeatherSensor.SensorType type : IWeatherSensor.SensorType.values()) {
            if (sensorsTag.contains(type.name())) {
                CompoundTag s = sensorsTag.getCompound(type.name());
                sensors.put(type, new SensorStatus(BlockPos.of(s.getLong("Pos")), s.getBoolean("Valid")));
            }
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    private int printingTicks = 0;
    private WeatherRecord pendingRecord = null;

    public boolean isPrinting() { return printingTicks > 0; }

    public boolean startPrinting(ServerLevel level) {
        if (printingTicks > 0) return false;
        pendingRecord = WeatherRecord.capture(level, worldPosition, this);
        printingTicks = AWCommonConfig.get().reportPrintingTicks;
        return true;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                  WeatherStationBlockEntity be) {
        if (be.printingTicks > 0) {
            be.printingTicks--;

            if (be.printingTicks % 6 == 0) {
                level.playSound(null, pos, SoundEvents.VILLAGER_WORK_CARTOGRAPHER,
                        SoundSource.BLOCKS, 0.4f, 1.6f + level.random.nextFloat() * 0.3f);
            }

            if (be.printingTicks == 0 && be.pendingRecord != null) {
                ItemStack report = new ItemStack(AWItems.WEATHER_REPORT.get());
                report.set(AWDataComponents.WEATHER_RECORD.get(), be.pendingRecord);
                be.pendingRecord = null;

                Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
                double ex = pos.getX() + 0.5 + facing.getStepX() * 0.6;
                double ey = pos.getY() + 0.4;
                double ez = pos.getZ() + 0.5 + facing.getStepZ() * 0.6;
                ItemEntity entity = new ItemEntity(level, ex, ey, ez, report,
                        facing.getStepX() * 0.08, 0.12, facing.getStepZ() * 0.08);
                level.addFreshEntity(entity);

                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.7f, 1.4f);
            }
        }

        if (!(level instanceof ServerLevel serverLevel)) return;
        if (--be.scanCooldown > 0) return;
        be.scanCooldown = SCAN_INTERVAL_TICKS;
        be.validateLinks(serverLevel);
    }

    private void validateLinks(ServerLevel level) {
        boolean changed = false;
        var it = sensors.entrySet().iterator();
        while (it.hasNext()) {
            var entry = it.next();
            BlockPos sensorPos = entry.getValue().pos();
            if (!level.isLoaded(sensorPos)
                    || !(level.getBlockEntity(sensorPos) instanceof IWeatherSensor sensor)
                    || sensor.getSensorType() != entry.getKey()) {
                it.remove();
                changed = true;
                continue;
            }
            boolean valid = sensor.isValidlyPlaced(level, sensorPos);
            if (valid != entry.getValue().valid()) {
                entry.setValue(new SensorStatus(sensorPos, valid));
                changed = true;
            }
        }
        if (changed) {
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    public void linkSensor(ServerLevel level, BlockPos sensorPos, IWeatherSensor sensor) {
        sensors.put(sensor.getSensorType(),
                new SensorStatus(sensorPos.immutable(), sensor.isValidlyPlaced(level, sensorPos)));
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }
}
