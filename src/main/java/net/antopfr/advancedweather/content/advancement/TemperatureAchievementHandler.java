package net.antopfr.advancedweather.content.advancement;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.network.toserver.TempAchievementPacket;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class TemperatureAchievementHandler {

    private static final ResourceLocation COLDEST_ID = ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, "coldest");
    private static final ResourceLocation HOTTEST_ID = ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, "hottest");

    public static void handle(final TempAchievementPacket payload, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ResourceLocation targetId = payload.isHot() ? HOTTEST_ID : COLDEST_ID;
                grantAdvancement(player, targetId);
            }
        });
    }

    private static void grantAdvancement(ServerPlayer player, ResourceLocation advancementId) {
        AdvancementHolder advancement = player.server.getAdvancements().get(advancementId);
        if (advancement != null) {
            AdvancementProgress progress = player.getAdvancements().getOrStartProgress(advancement);
            if (!progress.isDone()) {
                for (String crit : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(advancement, crit);
                }
            }
        }
    }
}
