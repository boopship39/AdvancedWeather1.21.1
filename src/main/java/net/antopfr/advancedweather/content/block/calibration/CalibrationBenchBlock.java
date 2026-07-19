package net.antopfr.advancedweather.content.block.calibration;

import com.mojang.serialization.MapCodec;
import net.antopfr.advancedweather.content.item.CalibrationToolItem;
import net.antopfr.advancedweather.util.AWTooltips;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class CalibrationBenchBlock extends HorizontalDirectionalBlock {

    public static final MapCodec<CalibrationBenchBlock> CODEC = simpleCodec(CalibrationBenchBlock::new);
    public static final EnumProperty<ModelType> MODEL_TYPE = EnumProperty.create("model", ModelType.class);

    public CalibrationBenchBlock(Properties properties) {
        super(properties);
        registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(MODEL_TYPE, ModelType.MAIN));
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, MODEL_TYPE);
    }

    @Override
    public boolean useShapeForLightOcclusion(@NotNull BlockState state) {
        return true;
    }

    @Override
    public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 1.0F;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();

        BlockPos sidePos = pos.relative(facing.getClockWise());

        if (level.getBlockState(sidePos).canBeReplaced(context) && level.getWorldBorder().isWithinBounds(sidePos)) {
            return this.defaultBlockState()
                    .setValue(FACING, facing)
                    .setValue(MODEL_TYPE, ModelType.MAIN);
        }

        return null;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            BlockPos sidePos = pos.relative(state.getValue(FACING).getClockWise());
            level.setBlock(sidePos, state.setValue(MODEL_TYPE, ModelType.SIDE), Block.UPDATE_ALL);
            level.blockUpdated(pos, Blocks.AIR);
            state.updateNeighbourShapes(level, pos, Block.UPDATE_ALL);
        }
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (!level.isClientSide) {
            ModelType modelType = state.getValue(MODEL_TYPE);
            Direction facing = state.getValue(FACING);

            BlockPos otherPos = modelType == ModelType.MAIN
                    ? pos.relative(facing.getClockWise())
                    : pos.relative(facing.getCounterClockWise());

            BlockState otherState = level.getBlockState(otherPos);
            if (otherState.is(this)) {
                level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
                level.levelEvent(player, 2001, otherPos, Block.getId(otherState));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Deprecated
    @Override
    public boolean canSurvive(BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        if (state.getValue(MODEL_TYPE) == ModelType.MAIN) {
            BlockPos sidePos = pos.relative(state.getValue(FACING).getClockWise());
            BlockState sideState = level.getBlockState(sidePos);
            return sideState.canBeReplaced() || sideState.is(this);
        }
        return true;
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return state.getValue(MODEL_TYPE) == ModelType.MAIN ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack held, @NotNull BlockState state, @NotNull Level level,
                                                       @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand,
                                                       @NotNull BlockHitResult hit) {
        if (held.getItem() instanceof CalibrationToolItem) {
            return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
        }

        BlockPos mainPos = state.getValue(MODEL_TYPE) == ModelType.MAIN
                ? pos
                : pos.relative(state.getValue(FACING).getCounterClockWise());

        if (level.isClientSide && CalibrationScreenOpener.isCalibratable(held)) {
            CalibrationScreenOpener.open(mainPos, held, hand);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    public enum ModelType implements StringRepresentable {
        MAIN, SIDE;

        @Override
        public @NotNull String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String toString() {
            return getSerializedName();
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        AWTooltips.append(tooltip, "advancedweather.block_tooltip.calibration_bench", 2);
    }
}
