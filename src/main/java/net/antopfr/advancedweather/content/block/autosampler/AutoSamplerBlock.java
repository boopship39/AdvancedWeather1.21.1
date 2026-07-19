package net.antopfr.advancedweather.content.block.autosampler;

import com.mojang.serialization.MapCodec;
import net.antopfr.advancedweather.content.block.AWBlocks;
import net.antopfr.advancedweather.content.item.CalibrationToolItem;
import net.antopfr.advancedweather.util.AWTooltips;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AutoSamplerBlock extends BaseEntityBlock {

    public static final MapCodec<AutoSamplerBlock> CODEC = simpleCodec(AutoSamplerBlock::new);

    public AutoSamplerBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(LIGHT, SamplerLight.IDLE));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return AWBlocks.AUTO_SAMPLER_BE.create(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        if (level.isClientSide) return null;
        return createTickerHelper(type, AWBlocks.AUTO_SAMPLER_BE.get(),
                AutoSamplerBlockEntity::serverTick);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                        Player player, BlockHitResult hit) {
        if (level.isClientSide
                && level.getBlockEntity(pos) instanceof AutoSamplerBlockEntity sampler) {
            AutoSamplerScreenOpener.open(sampler);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        AWTooltips.append(tooltip, "advancedweather.block_tooltip.auto_sampler", 3,
                "advancedweather.sensor_tooltip.link_hint");
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level,
                                                       BlockPos pos, Player player, InteractionHand hand,
                                                       BlockHitResult hit) {
        if (held.getItem() instanceof CalibrationToolItem) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public static final EnumProperty<SamplerLight> LIGHT = EnumProperty.create("light", SamplerLight.class);

    public enum SamplerLight implements StringRepresentable {
        IDLE, OK, WARNING, ERROR;
        @Override public String getSerializedName() { return name().toLowerCase(); }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING,
                context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, LIGHT);
    }
}
