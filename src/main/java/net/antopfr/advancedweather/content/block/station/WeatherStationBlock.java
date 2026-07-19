package net.antopfr.advancedweather.content.block.station;

import com.mojang.serialization.MapCodec;
import net.antopfr.advancedweather.config.AWCommonConfig;
import net.antopfr.advancedweather.content.advancement.AWAdvancements;
import net.antopfr.advancedweather.content.block.AWBlocks;
import net.antopfr.advancedweather.content.item.CalibrationToolItem;
import net.antopfr.advancedweather.util.AWTooltips;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WeatherStationBlock extends BaseEntityBlock {

    public static final MapCodec<WeatherStationBlock> CODEC = simpleCodec(WeatherStationBlock::new);

    public WeatherStationBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return AWBlocks.WEATHER_STATION_BE.create(pos, state);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING,
                context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack held, @NotNull BlockState state, @NotNull Level level,
                                                       @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hit) {
        if (held.getItem() instanceof CalibrationToolItem) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }

        if (!held.is(Items.PAPER) || !AWCommonConfig.get().enableWeatherReports) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (level instanceof ServerLevel serverLevel
                && level.getBlockEntity(pos) instanceof WeatherStationBlockEntity st) {
            if (AWCommonConfig.get().stationRequiresSensors && !st.hasAnySensor()) {
                level.playSound(null, pos, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 0.6f, 1f);
                if (player != null) player.displayClientMessage(
                        Component.translatable("advancedweather.station.no_sensors"), true);
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            if (st.startPrinting(serverLevel)) {
                held.consume(1, player);
                level.playSound(null, pos, SoundEvents.UI_BUTTON_CLICK.value(),
                        SoundSource.BLOCKS, 0.5f, 1.1f);
                if (player instanceof ServerPlayer sp) AWAdvancements.grant(sp, "for_the_record");
            }
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
                                                        @NotNull Player player, @NotNull BlockHitResult hit) {
        if (level.isClientSide
                && level.getBlockEntity(pos) instanceof WeatherStationBlockEntity station) {
            WeatherStationScreenOpener.open(station);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return createTickerHelper(type, AWBlocks.WEATHER_STATION_BE.get(),
                WeatherStationBlockEntity::serverTick);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        AWTooltips.append(tooltip, "advancedweather.block_tooltip.weather_station", 3, null);
    }
}
