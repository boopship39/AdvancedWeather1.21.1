package net.antopfr.advancedweather.content.item;

import net.antopfr.advancedweather.config.AWCommonConfig;
import net.antopfr.advancedweather.content.block.archive.WeatherArchiveBlockEntity;
import net.antopfr.advancedweather.content.block.autosampler.AutoSamplerBlockEntity;
import net.antopfr.advancedweather.content.block.sensor.IWeatherSensor;
import net.antopfr.advancedweather.content.block.station.WeatherStationBlockEntity;
import net.antopfr.advancedweather.network.toclient.HighlightSensorsPacket;
import net.antopfr.advancedweather.content.advancement.AWAdvancements;
import net.antopfr.advancedweather.util.AWTooltips;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CalibrationToolItem extends Item {

    private static final Map<UUID, BlockPos> pendingLink = new HashMap<>();

    public CalibrationToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) return InteractionResult.SUCCESS;
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        BlockPos pos = context.getClickedPos();
        BlockEntity be = level.getBlockEntity(pos);
        boolean isSensor = be instanceof IWeatherSensor;
        boolean isStation = be instanceof WeatherStationBlockEntity;
        boolean isArchive = be instanceof WeatherArchiveBlockEntity;
        boolean isSampler = be instanceof AutoSamplerBlockEntity;
        if (!isSensor && !isStation && !isArchive && !isSampler) return InteractionResult.PASS;

        if (player.isShiftKeyDown()) {
            if (isStation && player instanceof ServerPlayer sp) {
                sendHighlights(level, sp, (WeatherStationBlockEntity) be);
                return InteractionResult.SUCCESS;
            }
            if (isSampler && player instanceof ServerPlayer sp) {
                sendSamplerHighlights(level, sp, (AutoSamplerBlockEntity) be);
                return InteractionResult.SUCCESS;
            }
            if (isSensor) {
                boolean valid = ((IWeatherSensor) be).isValidlyPlaced(level, pos);
                player.displayClientMessage(Component.translatable(valid
                        ? "advancedweather.link.placement_valid"
                        : "advancedweather.link.placement_invalid"), true);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        BlockPos pending = pendingLink.get(player.getUUID());

        if (pending == null) {
            pendingLink.put(player.getUUID(), pos.immutable());
            String key = isSensor ? "advancedweather.link.sensor_selected"
                    : isStation ? "advancedweather.link.station_selected"
                    : isSampler ? "advancedweather.link.sampler_selected"
                    : "advancedweather.link.archive_selected";
            player.displayClientMessage(Component.translatable(key), true);
            level.playSound(null, pos, SoundEvents.COPPER_HIT, SoundSource.BLOCKS, 0.6f, 1.4f);
            return InteractionResult.SUCCESS;
        }

        BlockEntity pendingBe = level.getBlockEntity(pending);
        pendingLink.remove(player.getUUID());

        if (pendingBe != null && tryLink(level, player, pendingBe, pending, be, pos)) {
            return InteractionResult.SUCCESS;
        }

        pendingLink.put(player.getUUID(), pos.immutable());
        player.displayClientMessage(Component.translatable("advancedweather.link.restart"), true);
        return InteractionResult.SUCCESS;
    }

    private static boolean tryLink(ServerLevel level, Player player,
                                   BlockEntity a, BlockPos aPos,
                                   BlockEntity b, BlockPos bPos) {
        // capteur ↔ station
        if (a instanceof IWeatherSensor sensor && b instanceof WeatherStationBlockEntity station)
            return linkSensorStation(level, player, sensor, aPos, station, bPos);
        if (b instanceof IWeatherSensor sensor && a instanceof WeatherStationBlockEntity station)
            return linkSensorStation(level, player, sensor, bPos, station, aPos);

        // station ↔ sampler
        if (a instanceof WeatherStationBlockEntity && b instanceof AutoSamplerBlockEntity sampler)
            return linkSamplerTarget(level, player, sampler, bPos, aPos, true);
        if (b instanceof WeatherStationBlockEntity && a instanceof AutoSamplerBlockEntity sampler)
            return linkSamplerTarget(level, player, sampler, aPos, bPos, true);

        // archive ↔ sampler
        if (a instanceof WeatherArchiveBlockEntity && b instanceof AutoSamplerBlockEntity sampler)
            return linkSamplerTarget(level, player, sampler, bPos, aPos, false);
        if (b instanceof WeatherArchiveBlockEntity && a instanceof AutoSamplerBlockEntity sampler)
            return linkSamplerTarget(level, player, sampler, aPos, bPos, false);

        return false;
    }

    private static boolean linkSensorStation(ServerLevel level, Player player,
                                             IWeatherSensor sensor, BlockPos sensorPos,
                                             WeatherStationBlockEntity station, BlockPos stationPos) {
        if (!checkDistance(level, player, sensorPos, stationPos)) return true; // géré : message affiché
        station.linkSensor(level, sensorPos, sensor);
        player.displayClientMessage(Component.translatable("advancedweather.link.success",
                Component.translatable("advancedweather.sensor."
                        + sensor.getSensorType().name().toLowerCase())), true);
        level.playSound(null, stationPos, SoundEvents.COPPER_STEP, SoundSource.BLOCKS, 0.8f, 1.2f);
        if (player instanceof ServerPlayer sp) {
            AWAdvancements.grant(sp, "wired_in");
            if (hasAllSensorTypes(station)) AWAdvancements.grant(sp, "full_spectrum");
        }
        return true;
    }

    private static boolean hasAllSensorTypes(WeatherStationBlockEntity station) {
        for (IWeatherSensor.SensorType type : IWeatherSensor.SensorType.values()) {
            if (station.getSensor(type) == null) return false;
        }
        return true;
    }

    private static boolean linkSamplerTarget(ServerLevel level, Player player,
                                             AutoSamplerBlockEntity sampler, BlockPos samplerPos,
                                             BlockPos targetPos, boolean isStation) {
        if (!checkDistance(level, player, samplerPos, targetPos)) return true;
        if (isStation) {
            sampler.linkStation(targetPos);
            player.displayClientMessage(Component.translatable("advancedweather.link.sampler_station"), true);
        } else {
            sampler.linkArchive(targetPos);
            player.displayClientMessage(Component.translatable("advancedweather.link.sampler_archive"), true);
        }
        level.playSound(null, samplerPos, SoundEvents.COPPER_STEP, SoundSource.BLOCKS, 0.8f, 1.2f);
        if (player instanceof ServerPlayer sp) AWAdvancements.grant(sp, "set_and_forget");
        return true;
    }

    private static boolean checkDistance(ServerLevel level, Player player, BlockPos a, BlockPos b) {
        double maxDist = AWCommonConfig.get().sensorLinkMaxDistance;
        if (!a.closerThan(b, maxDist)) {
            player.displayClientMessage(Component.translatable("advancedweather.link.too_far",
                    (int) maxDist), true);
            level.playSound(null, b, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 0.6f, 1f);
            return false;
        }
        return true;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        AWTooltips.append(tooltip, "advancedweather.item_tooltip.calibration_tool", 4);
    }

    private static void sendHighlights(ServerLevel level, ServerPlayer player,
                                       WeatherStationBlockEntity station) {
        List<BlockPos> valid = new ArrayList<>();
        List<BlockPos> invalid = new ArrayList<>();
        for (IWeatherSensor.SensorType type : IWeatherSensor.SensorType.values()) {
            var status = station.getSensor(type);
            if (status == null) continue;
            (status.valid() ? valid : invalid).add(status.pos());
        }

        if (valid.isEmpty() && invalid.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable("advancedweather.link.no_sensors_linked"), true);
            level.playSound(null, station.getBlockPos(), SoundEvents.VILLAGER_NO,
                    SoundSource.BLOCKS, 0.5f, 1.2f);
            return;
        }

        PacketDistributor.sendToPlayer(player, new HighlightSensorsPacket(valid, invalid));
        level.playSound(null, station.getBlockPos(), SoundEvents.AMETHYST_BLOCK_RESONATE,
                SoundSource.BLOCKS, 0.7f, 1.3f);
    }

    private static void sendSamplerHighlights(ServerLevel level, ServerPlayer player,
                                              AutoSamplerBlockEntity sampler) {
        List<BlockPos> valid = new ArrayList<>();
        List<BlockPos> invalid = new ArrayList<>();

        classifyLink(level, sampler.getStationPos(), WeatherStationBlockEntity.class, valid, invalid);
        classifyLink(level, sampler.getArchivePos(), WeatherArchiveBlockEntity.class, valid, invalid);

        if (valid.isEmpty() && invalid.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable("advancedweather.link.sampler_unlinked"), true);
            level.playSound(null, sampler.getBlockPos(), SoundEvents.VILLAGER_NO,
                    SoundSource.BLOCKS, 0.5f, 1.2f);
            return;
        }

        PacketDistributor.sendToPlayer(player, new HighlightSensorsPacket(valid, invalid));
        level.playSound(null, sampler.getBlockPos(), SoundEvents.AMETHYST_BLOCK_RESONATE,
                SoundSource.BLOCKS, 0.7f, 1.3f);
    }

    private static void classifyLink(ServerLevel level, BlockPos target, Class<?> expected,
                                     List<BlockPos> valid, List<BlockPos> invalid) {
        if (target == null) return;
        if (level.isLoaded(target) && expected.isInstance(level.getBlockEntity(target))) {
            valid.add(target);
        } else {
            invalid.add(target);
        }
    }
}