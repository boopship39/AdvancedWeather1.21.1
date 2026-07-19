package net.antopfr.advancedweather.weather.effect.types.rainbows;

import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.weather.WeatherEffects;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.core.BlockPos;
import net.antopfr.advancedweather.content.advancement.AWAdvancements;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class RainbowSpawner {

    private static final Set<WeatherTypes> RAIN_TYPES = EnumSet.of(
            WeatherTypes.LIGHT_RAIN, WeatherTypes.HEAVY_RAIN, WeatherTypes.DRIZZLE);
    private static final Set<WeatherTypes> CLEAR_TYPES = EnumSet.of(
            WeatherTypes.CLEAR, WeatherTypes.SUNNY);

    private static final float DISTANCE      = 220f;
    private static final float ARC_ANGLE_DEG = 42.3f;

    public static void onWeatherTransition(ServerLevel level, WeatherTypes.Dimension dim,
                                           WeatherTypes from, WeatherTypes to) {
        if (dim != WeatherTypes.Dimension.OVERWORLD) return;
        if (!RAIN_TYPES.contains(from) || !CLEAR_TYPES.contains(to)) return;

        boolean alreadyExists = false;
        for (var e : level.getAllEntities()) {
            if (e instanceof RainbowEntity) { alreadyExists = true; break; }
        }
        if (alreadyExists) return;

        trySpawn(level);
    }

    public static void onWeatherTick(ServerLevel level, WeatherTypes current, long gameTick) {
        if (current != WeatherTypes.DRIZZLE) return;
        if (gameTick % 1200 != 0) return;
        if (level.getRandom().nextFloat() > 0.25f) return;

        // Évite les doublons
        boolean alreadyExists = false;
        for (var e : level.getAllEntities()) {
            if (e instanceof RainbowEntity) { alreadyExists = true; break; }
        }
        if (alreadyExists) return;

        trySpawn(level);
    }

    public static void trySpawn(ServerLevel level) {
        if (!ClientWeatherState.getActiveEffects().contains(WeatherEffects.RAINBOWS)) return;

        long dayTime = level.getDayTime() % 24000;
        boolean isNight = dayTime > 13000 && dayTime < 23000;
        if (isNight) return;

        float sunAngle = level.getSunAngle(0f);
        float angleRad = sunAngle * Mth.TWO_PI;
        Vec3 sunDir = new Vec3(Mth.sin(angleRad), Mth.cos(angleRad), 0).normalize();
        Vec3 antiSun = sunDir.scale(-1);

        float sunElevationDeg = (float) Math.toDegrees(Math.asin(Mth.clamp((float) sunDir.y, -1f, 1f)));
        if (sunElevationDeg > 35f) return;

        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) return;

        Vec3 center = Vec3.ZERO;
        for (ServerPlayer p : players) center = center.add(p.position());
        center = center.scale(1.0 / players.size());

        var random = level.getRandom();
        double jitterX = (random.nextDouble() - 0.5) * 40.0;
        double jitterZ = (random.nextDouble() - 0.5) * 40.0;
        center = center.add(jitterX, 0, jitterZ);

        Vec3 up = Math.abs(antiSun.y) > 0.99 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        Vec3 right = up.cross(antiSun).normalize();

        double rad = Math.toRadians(ARC_ANGLE_DEG);
        Vec3 footDir1 = antiSun.scale(Math.cos(rad)).add(right.scale(Math.sin(rad)));
        Vec3 footDir2 = antiSun.scale(Math.cos(rad)).add(right.scale(-Math.sin(rad)));

        // Calcul théorique initial (apex = center)
        Vec3 start = center.add(footDir1.normalize().scale(DISTANCE));
        Vec3 end   = center.add(footDir2.normalize().scale(DISTANCE));

        // Hauteur réelle du sol à ces deux positions
        int groundYStart = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE,
                new BlockPos((int) Math.floor(start.x), 0, (int) Math.floor(start.z))).getY();
        int groundYEnd = level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE,
                new BlockPos((int) Math.floor(end.x), 0, (int) Math.floor(end.z))).getY();

        double avgGroundY = (groundYStart + groundYEnd) / 2.0 + 1.0;
        double avgTheoreticalY = (start.y + end.y) / 2.0;
        double verticalCorrection = avgGroundY - avgTheoreticalY;

        // Translation pure de tout le système — ne change pas l'angle du cône
        Vec3 correctedCenter = new Vec3(center.x, center.y + verticalCorrection, center.z);
        Vec3 correctedStart   = new Vec3(start.x, start.y + verticalCorrection, start.z);
        Vec3 correctedEnd     = new Vec3(end.x, end.y + verticalCorrection, end.z);

        RainbowEntity rainbow = new RainbowEntity(level);
        rainbow.setPos(correctedCenter.x, correctedCenter.y, correctedCenter.z);
        rainbow.setEndpoints(correctedStart, correctedEnd);
        rainbow.setAntiSunDirection(antiSun);
        level.addFreshEntity(rainbow);

        // "Somewhere Over" — a rainbow just appeared; credit everyone in the dimension.
        for (ServerPlayer player : level.players()) {
            AWAdvancements.grant(player, "somewhere_over");
        }
    }
}