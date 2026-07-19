package net.antopfr.advancedweather.content.advancement;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.api.event.WeatherChangeEvent;
import net.antopfr.advancedweather.config.AWCommonConfig;
import net.antopfr.advancedweather.content.item.almanac.WeatherAlmanacItem;
import net.antopfr.advancedweather.weather.WeatherManager;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.EnumSet;
import java.util.Set;

@EventBusSubscriber(modid = AdvancedWeather.MOD_ID)
public class AWAdvancementEvents {

    private static final Set<WeatherTypes> SKIP = EnumSet.of(
            WeatherTypes.CLEAR, WeatherTypes.SUNNY, WeatherTypes.CLOUDY, WeatherTypes.OVERCAST,
            WeatherTypes.NETHER_CLEAR, WeatherTypes.END_CLEAR);

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ItemStack crafted = event.getCrafting();
        if (!(crafted.getItem() instanceof WeatherAlmanacItem)) return;

        int max = AWCommonConfig.get().almanacMaxRecords;
        if (WeatherAlmanacItem.getRecords(crafted).size() >= max) {
            AWAdvancements.grant(player, "the_great_library");
        }
    }

    @SubscribeEvent
    public static void onWeatherChange(WeatherChangeEvent event) {
        ServerLevel level = event.getLevel();
        for (ServerPlayer player : level.players()) {
            weatherExperienced(player, event.getCurrent());
        }
    }

    @SubscribeEvent
    public static void onLightningStrike(EntityStruckByLightningEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ServerLevel level = player.serverLevel();
        if (WeatherManager.get(level).getCurrentWeather(level).isVanillaThundering()) {
            AWAdvancements.grant(player, "thunderstruck");
        }
    }

    public static void weatherExperienced(ServerPlayer player, WeatherTypes weather) {
        if (SKIP.contains(weather)) return;

        AWAdvancements.grant(player, hubOf(weather));
        AWAdvancements.grantCriterion(player, almanacOf(weather.dimension()), weather.name().toLowerCase());
        checkMaster(player);
    }

    private static String hubOf(WeatherTypes weather) {
        if (weather.isNether()) return "nether_skies";
        if (weather.isEnd()) return "end_skies";
        return "overworld_skies";
    }

    private static String almanacOf(WeatherTypes.Dimension dim) {
        return switch (dim) {
            case OVERWORLD -> "overworld_almanac";
            case NETHER -> "nether_almanac";
            case END -> "end_almanac";
        };
    }

    private static void checkMaster(ServerPlayer player) {
        if (AWAdvancements.isDone(player, "overworld_almanac")
                && AWAdvancements.isDone(player, "nether_almanac")
                && AWAdvancements.isDone(player, "end_almanac")) {
            AWAdvancements.grant(player, "master_meteorologist");
        }
    }
}
