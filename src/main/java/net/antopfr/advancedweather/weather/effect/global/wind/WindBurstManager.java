package net.antopfr.advancedweather.weather.effect.global.wind;

import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.config.AWClientConfig;
import net.antopfr.advancedweather.weather.WeatherEffects;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.Random;

public class WindBurstManager {

    private static final Random RANDOM = new Random();

    private static int burstCooldown = 20;
    private static int burstDuration = 0;
    private static boolean inBurst = false;

    private static float lightIntensity = 0.0f;
    private static float heavyIntensity = 0.0f;

    public static void tick() {
        AWClientConfig config = AWClientConfig.get();
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        float targetLight = 0.0f;
        float targetHeavy = 0.0f;

        if (ClientWeatherState.hasEffect(WeatherEffects.WIND_LINES)
                || ClientWeatherState.hasEffect(WeatherEffects.NETHER_WIND_LINES)
                || ClientWeatherState.hasEffect(WeatherEffects.END_WIND_LINES)) {

            WeatherTypes type = ClientWeatherState.getCurrentWeather();
            boolean forceContinuous = type == WeatherTypes.THUNDERSTORM || type == WeatherTypes.BLIZZARD || type == WeatherTypes.WINDY || type == WeatherTypes.NETHERSTORM || type == WeatherTypes.ENDERSTORM;

            if (forceContinuous) {
                inBurst = true;
                targetLight = 0.3f;
                targetHeavy = 1.0f;
            } else if (!config.windBurstEnabled) {
                inBurst = true;
                targetLight = 0.5f;
                targetHeavy = 0.0f;
            } else {
                if (inBurst) {
                    burstDuration--;
                    targetLight = 0.2f;
                    targetHeavy = 1.0f;
                    if (burstDuration <= 0) {
                        inBurst = false;
                        int min = config.windBurstCooldownMin;
                        int max = config.windBurstCooldownMax;
                        burstCooldown = min + (max > min ? RANDOM.nextInt(max - min) : 0);
                    }
                } else {
                    burstCooldown--;
                    targetLight = 0.6f;
                    targetHeavy = 0.0f;
                    if (burstCooldown <= 0) {
                        inBurst = true;
                        int min = config.windBurstDurationMin;
                        int max = config.windBurstDurationMax;
                        burstDuration = min + (max > min ? RANDOM.nextInt(max - min) : 0);
                    }
                }
            }

            float shelterModifier = getWindVolumeModifier(mc.level, mc.player.blockPosition());
            targetLight *= shelterModifier;
            targetHeavy *= shelterModifier;
        } else {
            inBurst = false;
        }

        if (lightIntensity < targetLight) lightIntensity = Math.min(targetLight, lightIntensity + 0.02f);
        else if (lightIntensity > targetLight) lightIntensity = Math.max(targetLight, lightIntensity - 0.01f);

        if (heavyIntensity < targetHeavy) heavyIntensity = Math.min(targetHeavy, heavyIntensity + 0.05f);
        else if (heavyIntensity > targetHeavy) heavyIntensity = Math.max(targetHeavy, heavyIntensity - 0.02f);
    }

    private static float getWindVolumeModifier(Level level, BlockPos playerPos) {
        int roofY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, playerPos.getX(), playerPos.getZ());
        boolean hasRoof = roofY > playerPos.getY() + 1;

        if (!hasRoof) return 1.0f;

        int distanceToRoof = roofY - playerPos.getY();
        float verticalFactor = 1.0f - (float) Math.exp(-distanceToRoof * 0.15f);

        BlockPos roofBlock = new BlockPos(playerPos.getX(), roofY - 1, playerPos.getZ());
        BlockState roofState = level.getBlockState(roofBlock);
        float materialFactor = getMaterialFactor(roofState);

        int checkRadius = 6;
        int openDirections = 0;

        for (int[] dir : new int[][]{{1,0},{-1,0},{0,1},{0,-1}}) {
            boolean blocked = false;
            for (int dist = 1; dist <= checkRadius; dist++) {
                BlockPos check = playerPos.offset(dir[0] * dist, 0, dir[1] * dist);
                if (!level.isEmptyBlock(check)) {
                    blocked = true;
                    break;
                }
            }
            if (!blocked) openDirections++;
        }

        float opennessFactor = 0.1f + (openDirections / 4.0f) * 0.7f;

        float shelter = verticalFactor * materialFactor * opennessFactor;
        return Mth.clamp(0.05f + shelter * 0.95f, 0.05f, 1.0f);
    }

    private static float getMaterialFactor(BlockState roofState) {
        SoundType roofSound = roofState.getSoundType();

        float materialFactor;
        if (roofSound == SoundType.GLASS) {
            materialFactor = 0.85f;
        } else if (roofSound == SoundType.METAL || roofSound == SoundType.COPPER) {
            materialFactor = 0.6f;
        } else if (roofSound == SoundType.WOOD || roofSound == SoundType.CHERRY_WOOD) {
            materialFactor = 0.4f;
        } else if (roofSound == SoundType.STONE || roofSound == SoundType.DEEPSLATE) {
            materialFactor = 0.3f;
        } else {
            materialFactor = 0.35f;
        }
        return materialFactor;
    }

    public static float getLightIntensity() { return lightIntensity; }
    public static float getHeavyIntensity() { return heavyIntensity; }
    public static boolean isInBurst() { return inBurst; }
}
