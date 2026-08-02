package net.antopfr.advancedweather.content.block.autosampler;

import net.antopfr.advancedweather.config.AWCommonConfig;
import net.antopfr.advancedweather.content.block.archive.WeatherArchiveBlockEntity;
import net.antopfr.advancedweather.content.block.station.WeatherStationBlockEntity;
import net.antopfr.advancedweather.content.WeatherRecord;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class AutoSamplerBlockEntity extends BlockEntity {

    public static final int MIN_INTERVAL = 100;    // 5s
    public static final int MAX_INTERVAL = 72000;  // 1h

    private BlockPos stationPos = null;
    private BlockPos archivePos = null;
    private int intervalTicks = 1200; // 1 min
    private int cooldown = 0;

    public enum LastResult { IDLE, OK, NO_STATION, NO_ARCHIVE, STATION_NO_SENSORS, ARCHIVE_REJECTED }
    private LastResult lastResult = LastResult.IDLE;

    public AutoSamplerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void linkStation(BlockPos pos) {
        stationPos = pos.immutable();
        cooldown = Math.min(cooldown, 40);
        sync();
    }

    public void linkArchive(BlockPos pos) {
        archivePos = pos.immutable();
        cooldown = Math.min(cooldown, 40);
        sync();
    }

    public void setInterval(int ticks) {
        intervalTicks = Mth.clamp(ticks, MIN_INTERVAL, MAX_INTERVAL);
        cooldown = Math.min(cooldown, intervalTicks);
        sync();
    }

    public BlockPos getStationPos() { return stationPos; }
    public BlockPos getArchivePos() { return archivePos; }
    public int getIntervalTicks() { return intervalTicks; }
    public LastResult getLastResult() { return lastResult; }

    public String stationLabel() {
        if (stationPos == null) return null;
        if (level != null && level.isLoaded(stationPos)
                && level.getBlockEntity(stationPos) instanceof WeatherStationBlockEntity st
                && !st.getStationName().isBlank()) {
            return st.getStationName();
        }
        return stationPos.getX() + ", " + stationPos.getY() + ", " + stationPos.getZ();
    }

    public String archiveLabel() {
        if (archivePos == null) return null;
        return archivePos.getX() + ", " + archivePos.getY() + ", " + archivePos.getZ();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state,
                                  AutoSamplerBlockEntity be) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (--be.cooldown > 0) return;
        be.cooldown = be.intervalTicks;
        be.sample(serverLevel);
    }

    private void sample(ServerLevel level) {
        LastResult result;

        if (stationPos == null || !level.isLoaded(stationPos)
                || !(level.getBlockEntity(stationPos) instanceof WeatherStationBlockEntity station)) {
            result = LastResult.NO_STATION;
        } else if (archivePos == null || !level.isLoaded(archivePos)
                || !(level.getBlockEntity(archivePos) instanceof WeatherArchiveBlockEntity archive)) {
            result = LastResult.NO_ARCHIVE;
        } else if (AWCommonConfig.get().stationRequiresSensors && !station.hasAnySensor()) {
            result = LastResult.STATION_NO_SENSORS;
        } else {
            WeatherRecord record = WeatherRecord.capture(level, stationPos, station);
            result = archive.ingest(record) == WeatherArchiveBlockEntity.IngestResult.OK
                    ? LastResult.OK : LastResult.ARCHIVE_REJECTED;
        }

        AutoSamplerBlock.SamplerLight light = switch (result) {
            case OK -> AutoSamplerBlock.SamplerLight.OK;
            case STATION_NO_SENSORS, ARCHIVE_REJECTED -> AutoSamplerBlock.SamplerLight.WARNING;
            case NO_STATION, NO_ARCHIVE -> AutoSamplerBlock.SamplerLight.ERROR;
            case IDLE -> AutoSamplerBlock.SamplerLight.IDLE;
        };
        BlockState current = level.getBlockState(worldPosition);
        if (current.getValue(AutoSamplerBlock.LIGHT) != light) {
            level.setBlock(worldPosition, current.setValue(AutoSamplerBlock.LIGHT, light), 3);
        }

        if (result == LastResult.OK) {
            level.playSound(null, worldPosition, SoundEvents.AMETHYST_BLOCK_CHIME,
                    SoundSource.BLOCKS, 0.3f, 1.8f);
        }
        if (result != lastResult) {
            lastResult = result;
            sync();
        }
    }

    private void sync() {
        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        if (stationPos != null) tag.putLong("StationPos", stationPos.asLong());
        if (archivePos != null) tag.putLong("ArchivePos", archivePos.asLong());
        tag.putInt("Interval", intervalTicks);
        tag.putInt("LastResult", lastResult.ordinal());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        stationPos = tag.contains("StationPos") ? BlockPos.of(tag.getLong("StationPos")) : null;
        archivePos = tag.contains("ArchivePos") ? BlockPos.of(tag.getLong("ArchivePos")) : null;
        intervalTicks = tag.contains("Interval")
                ? Mth.clamp(tag.getInt("Interval"), MIN_INTERVAL, MAX_INTERVAL) : 1200;
        int lr = tag.getInt("LastResult");
        lastResult = lr >= 0 && lr < LastResult.values().length
                ? LastResult.values()[lr] : LastResult.IDLE;
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
