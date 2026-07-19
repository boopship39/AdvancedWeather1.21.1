package net.antopfr.advancedweather.content.advancement;

import net.antopfr.advancedweather.AdvancedWeather;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public final class AWAdvancements {

    private AWAdvancements() {}

    public static void grant(ServerPlayer player, String path) {
        if (player == null) return;
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, path);
        AdvancementHolder advancement = player.server.getAdvancements().get(id);
        if (advancement == null) return;

        AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
        if (progress.isDone()) return;
        for (String criterion : progress.getRemainingCriteria()) {
            player.getAdvancements().award(advancement, criterion);
        }
    }

    public static void grantCriterion(ServerPlayer player, String path, String criterion) {
        if (player == null) return;
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, path);
        AdvancementHolder advancement = player.server.getAdvancements().get(id);
        if (advancement == null) return;
        player.getAdvancements().award(advancement, criterion);
    }

    public static boolean isDone(ServerPlayer player, String path) {
        if (player == null) return false;
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, path);
        AdvancementHolder advancement = player.server.getAdvancements().get(id);
        if (advancement == null) return false;
        return player.getAdvancements().getOrStartProgress(advancement).isDone();
    }

    public static void grantNearby(ServerLevel level, BlockPos pos, double radius, String path) {
        double r2 = radius * radius;
        for (ServerPlayer player : level.players()) {
            if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= r2) {
                grant(player, path);
            }
        }
    }
}
