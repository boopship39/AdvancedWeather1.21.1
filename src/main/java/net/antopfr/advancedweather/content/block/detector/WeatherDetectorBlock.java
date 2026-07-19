package net.antopfr.advancedweather.content.block.detector;

import com.mojang.serialization.MapCodec;
import net.antopfr.advancedweather.content.advancement.AWAdvancements;
import net.antopfr.advancedweather.util.AWTooltips;
import net.antopfr.advancedweather.weather.WeatherManager;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WeatherDetectorBlock extends Block {

    public static final MapCodec<WeatherDetectorBlock> CODEC = simpleCodec(WeatherDetectorBlock::new);

    public static final EnumProperty<DetectionMode> MODE = EnumProperty.create("mode", DetectionMode.class);
    public static final IntegerProperty POWER = IntegerProperty.create("power", 0, 15);

    public enum DetectionMode implements StringRepresentable {
        ANY_PRECIPITATION("any_precipitation"),
        STORM("storm"),
        THUNDERSTORM("thunderstorm"),
        HAIL("hail"),
        SNOW("snow"),
        RAIN("rain"),
        WIND("wind");

        private final String name;
        DetectionMode(String name) { this.name = name; }
        @Override public @NotNull String getSerializedName() { return name; }

        public DetectionMode next(boolean backwards) {
            DetectionMode[] v = values();
            return v[Math.floorMod(ordinal() + (backwards ? -1 : 1), v.length)];
        }
    }

    public WeatherDetectorBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(MODE, DetectionMode.ANY_PRECIPITATION)
                .setValue(POWER, 0));
    }

    @Override
    protected @NotNull MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MODE, POWER);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
                                                        @NotNull Player player, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            WeatherDetectorScreenOpener.open(pos, state.getValue(MODE));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }


    @Override
    protected boolean isSignalSource(@NotNull BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Direction direction) {
        return state.getValue(POWER);
    }

    @Override
    protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        int newPower = computePower(state, level, pos);
        int oldPower = state.getValue(POWER);
        if (newPower != oldPower) {
            level.setBlock(pos, state.setValue(POWER, newPower), 3);
            if (oldPower == 0 && newPower > 0) {
                AWAdvancements.grantNearby(level, pos, 16.0, "automated_response");
            }
        }
        level.scheduleTick(pos, this, 20);
    }

    @Override
    protected void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean movedByPiston) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.scheduleTick(pos, this, 1);
        }
    }

    private int computePower(BlockState state, ServerLevel level, BlockPos pos) {
        if (!level.canSeeSky(pos.above())) return 0; // exposition requise

        WeatherTypes weather = WeatherManager.get(level).getCurrentWeather(level);
        DetectionMode mode = state.getValue(MODE);

        return switch (mode) {
            case WIND -> {
                float wi = WeatherManager.get(level).getWindIntensity(level);
                yield Mth.clamp(Math.round(wi * 15f), 0, 15);
            }
            case ANY_PRECIPITATION -> isPrecipitating(weather) ? 15 : 0;
            case STORM -> isStorm(weather) ? 15 : 0;
            case THUNDERSTORM -> weather == WeatherTypes.THUNDERSTORM
                    || weather == WeatherTypes.NETHERSTORM ? 15 : 0;
            case HAIL -> weather == WeatherTypes.HAIL ? 15 : 0;
            case SNOW -> weather == WeatherTypes.SNOW
                    || weather == WeatherTypes.BLIZZARD ? 15 : 0;
            case RAIN -> isRainType(weather) ? 15 : 0;
        };
    }

    private static boolean isRainType(WeatherTypes w) {
        return w == WeatherTypes.DRIZZLE || w == WeatherTypes.LIGHT_RAIN
                || w == WeatherTypes.HEAVY_RAIN || w == WeatherTypes.FREEZING_RAIN
                || w == WeatherTypes.THUNDERSTORM;
    }

    private static boolean isPrecipitating(WeatherTypes w) {
        return isRainType(w) || w == WeatherTypes.SNOW || w == WeatherTypes.BLIZZARD
                || w == WeatherTypes.HAIL || w == WeatherTypes.LAVA_RAIN;
    }

    private static boolean isStorm(WeatherTypes w) {
        return switch (w) {
            case THUNDERSTORM, BLIZZARD, HAIL, SANDSTORM,
                 NETHERSTORM, HELLFIRE, ASH_STORM, BRIMSTONE_STORM,
                 VOID_STORM, ENDERSTORM -> true;
            default -> false;
        };
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        AWTooltips.append(tooltip, "advancedweather.block_tooltip.weather_detector", 3, null);
    }
}
