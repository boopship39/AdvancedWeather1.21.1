package net.antopfr.advancedweather.server.command;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.antopfr.advancedweather.api.AdvancedWeatherAPI;
import net.antopfr.advancedweather.api.external.NominatimGeocoding;
import net.antopfr.advancedweather.api.external.OpenMeteo;
import net.antopfr.advancedweather.config.AWCommonConfig;
import net.antopfr.advancedweather.network.toclient.ToggleDebugPacket;
import net.antopfr.advancedweather.weather.WeatherEffects;
import net.antopfr.advancedweather.weather.WeatherManager;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.antopfr.advancedweather.weather.effect.EffectManager;
import net.antopfr.advancedweather.weather.effect.types.rainbows.RainbowEntity;
import net.antopfr.advancedweather.weather.effect.types.rainbows.RainbowSpawner;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

import static net.antopfr.advancedweather.weather.WeatherManager.isCompatibleWithLevel;

public class AWCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("aw")
                .requires(source -> source.hasPermission(2))
                .executes(AWCommand::info)
//                .then(forecast())
                .then(history())
                .then(debug())
                .then(force())
                .then(set())
                .then(setLocation())
                .then(rainbow())
                .then(effect())
                .then(auto())
                .then(refresh());
    }

    // /aw
    private static int info(CommandContext<CommandSourceStack> ctx) {
        ServerLevel level = ctx.getSource().getLevel();
        WeatherTypes current = WeatherManager.get(level).getCurrentWeather(level);
        ctx.getSource().sendSuccess(
                () -> Component.translatable("commands.aw.info", current.weatherName()), false);
        return 1;
    }

    // /aw forecast
//    private static LiteralArgumentBuilder<CommandSourceStack> forecast() {
//        return Commands.literal("forecast")
//                .executes(ctx -> {
//                    ServerLevel level = ctx.getSource().getLevel();
//                    WeatherForecast f = AdvancedWeatherAPI.getForecast(level);
//                    ctx.getSource().sendSuccess(() -> Component.literal(
//                            String.format("§6[Forecast]\n"
//                                            + "§7Next: §f%s §8(%.0f%% confidence)\n"
//                                            + "§7In 30 min: §f%s §8(%.0f%% confidence)",
//                                    f.predictedNext().weatherName(), f.confidenceNext(),
//                                    f.predictedIn30Min().weatherName(), f.confidenceIn30Min())), false);
//                    return 1;
//                });
//    }

    // /aw history
    private static LiteralArgumentBuilder<CommandSourceStack> history() {
        return Commands.literal("history")
                .executes(ctx -> {
                    ServerLevel level = ctx.getSource().getLevel();
                    List<WeatherTypes> recent = AdvancedWeatherAPI.getRecentHistory(level);
                    if (recent.isEmpty()) {
                        ctx.getSource().sendSuccess(() -> Component.literal("§7[Advanced Weather] No recent history yet"), false);
                        return 1;
                    }
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < recent.size(); i++) {
                        if (i > 0) sb.append(" §8→ §f");
                        sb.append(recent.get(i).weatherName());
                    }
                    String joined = sb.toString();
                    ctx.getSource().sendSuccess(() -> Component.literal("§6[History] §f" + joined), false);
                    return 1;
                });
    }

    // /aw debug
    private static LiteralArgumentBuilder<CommandSourceStack> debug() {
        return Commands.literal("debug")
                .executes(ctx -> {
                    PacketDistributor.sendToPlayer(
                            ctx.getSource().getPlayerOrException(), ToggleDebugPacket.INSTANCE);
                    return 1;
                });
    }

    // /aw force <pressure> <dewpoint> <temp> <seconds>  |  /aw force clear
    private static LiteralArgumentBuilder<CommandSourceStack> force() {
        return Commands.literal("force")
                .then(Commands.argument("pressure", FloatArgumentType.floatArg(-100f, 100f))
                        .then(Commands.argument("dewpoint", FloatArgumentType.floatArg(-30f, 30f))
                                .then(Commands.argument("temp", FloatArgumentType.floatArg(-30f, 30f))
                                        .then(Commands.argument("seconds", IntegerArgumentType.integer(1, 600))
                                                .executes(ctx -> {
                                                    ServerLevel level = ctx.getSource().getLevel();
                                                    float p = FloatArgumentType.getFloat(ctx, "pressure");
                                                    float dp = FloatArgumentType.getFloat(ctx, "dewpoint");
                                                    float t = FloatArgumentType.getFloat(ctx, "temp");
                                                    int sec = IntegerArgumentType.getInteger(ctx, "seconds");

                                                    WeatherManager.get(level).getAtmosphere(level)
                                                            .applyForcing(p, dp, t, sec * 20);

                                                    ctx.getSource().sendSuccess(() -> Component.literal(
                                                            String.format("Forcing applied: ΔP=%.0f ΔDP=%.0f ΔT=%.0f over %ds",
                                                                    p, dp, t, sec)), true);
                                                    return 1;
                                                })))))
                .then(Commands.literal("clear")
                        .executes(ctx -> {
                            ServerLevel level = ctx.getSource().getLevel();
                            WeatherManager.get(level).getAtmosphere(level).clearForcings();
                            ctx.getSource().sendSuccess(() -> Component.literal("Forcings cleared"), true);
                            return 1;
                        }));
    }

    // /aw set <type>
    private static LiteralArgumentBuilder<CommandSourceStack> set() {
        return Commands.literal("set")
                .then(Commands.argument("type", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            ServerLevel level = ctx.getSource().getLevel();
                            for (WeatherTypes type : WeatherTypes.values()) {
                                if (isCompatibleWithLevel(type, level))
                                    builder.suggest(type.name().toLowerCase());
                            }
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            String input = StringArgumentType.getString(ctx, "type");
                            WeatherTypes type = WeatherTypes.fromNameSafe(input);
                            if (!input.equalsIgnoreCase(type.name())) {
                                ctx.getSource().sendFailure(
                                        Component.translatable("commands.aw.set.unknown", input));
                                return 0;
                            }
                            ServerLevel level = ctx.getSource().getLevel();
                            if (!isCompatibleWithLevel(type, level)) {
                                ctx.getSource().sendFailure(
                                        Component.translatable("commands.aw.set.unknown", input));
                                return 0;
                            }
                            WeatherManager.get(level).setCurrentWeather(level, type);
                            ctx.getSource().sendSuccess(
                                    () -> Component.translatable("commands.aw.set.success", type.weatherName()), true);
                            return 1;
                        }));
    }

    // /aw setlocation <latitude> <longitude>
    private static LiteralArgumentBuilder<CommandSourceStack> setLocation() {
        return Commands.literal("setlocation")
                .then(Commands.argument("latitude", DoubleArgumentType.doubleArg(-90, 90))
                        .then(Commands.argument("longitude", DoubleArgumentType.doubleArg(-180, 180))
                                .executes(ctx -> {
                                    double lat = DoubleArgumentType.getDouble(ctx, "latitude");
                                    double lon = DoubleArgumentType.getDouble(ctx, "longitude");
                                    AWCommonConfig config = AWCommonConfig.get();
                                    config.latitude = lat;
                                    config.longitude = lon;
                                    config.save();

                                    NominatimGeocoding.invalidateCache();
                                    NominatimGeocoding.fetchLocationNameAsync(lat, lon);

                                    ctx.getSource().sendSuccess(
                                            () -> Component.translatable("commands.aw.setlocation", lat + ", " + lon), true);
                                    return 1;
                                })));
    }

    // /aw rainbow spawn|clear
    private static LiteralArgumentBuilder<CommandSourceStack> rainbow() {
        return Commands.literal("rainbow")
                .then(Commands.literal("spawn")
                        .executes(ctx -> {
                            ServerLevel level = ctx.getSource().getLevel();
                            RainbowSpawner.trySpawn(level);
                            ctx.getSource().sendSuccess(
                                    () -> Component.literal("[AW] Rainbow spawn attempted"), false);
                            return 1;
                        }))
                .then(Commands.literal("clear")
                        .executes(ctx -> {
                            ServerLevel level = ctx.getSource().getLevel();
                            List<RainbowEntity> rainbows = new ArrayList<>();
                            for (Entity e : level.getAllEntities()) {
                                if (e instanceof RainbowEntity rainbow) rainbows.add(rainbow);
                            }
                            rainbows.forEach(Entity::discard);
                            ctx.getSource().sendSuccess(
                                    () -> Component.literal("[AW] Removed " + rainbows.size() + " rainbow(s)"), false);
                            return 1;
                        }));
    }

    // /aw effect add|remove <effect>
    private static LiteralArgumentBuilder<CommandSourceStack> effect() {
        return Commands.literal("effect")
                .then(Commands.literal("add")
                        .then(Commands.argument("effect", StringArgumentType.word())
                                .suggests(AWCommand::suggestEffects)
                                .executes(ctx -> applyEffect(ctx, true))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("effect", StringArgumentType.word())
                                .suggests(AWCommand::suggestEffects)
                                .executes(ctx -> applyEffect(ctx, false))));
    }

    private static java.util.concurrent.CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> suggestEffects(
            com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx,
            com.mojang.brigadier.suggestion.SuggestionsBuilder builder) {
        for (WeatherEffects e : WeatherEffects.values()) builder.suggest(e.name().toLowerCase());
        return builder.buildFuture();
    }

    private static int applyEffect(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx, boolean add) {
        String input = StringArgumentType.getString(ctx, "effect");
        WeatherEffects effect = WeatherEffects.fromNameSafe(input);
        if (effect == null) {
            ctx.getSource().sendFailure(Component.translatable("commands.aw.effect.unknown", input));
            return 0;
        }
        ServerLevel level = ctx.getSource().getLevel();
        if (add) EffectManager.get(level).addEffect(level, effect);
        else EffectManager.get(level).removeEffect(level, effect);
        ctx.getSource().sendSuccess(
                () -> Component.translatable(add ? "commands.aw.effect.added" : "commands.aw.effect.removed",
                        effect.effectName()), true);
        return 1;
    }

    // /aw auto
    private static LiteralArgumentBuilder<CommandSourceStack> auto() {
        return Commands.literal("auto")
                .executes(ctx -> {
                    ServerLevel level = ctx.getSource().getLevel();
                    WeatherManager.get(level).startAutoWeather(level);
                    ctx.getSource().sendSuccess(
                            () -> Component.translatable("commands.aw.auto.started"), true);
                    return 1;
                });
    }

    // /aw refresh  (pull real weather now)
    private static LiteralArgumentBuilder<CommandSourceStack> refresh() {
        return Commands.literal("refresh")
                .executes(ctx -> {
                    AWCommonConfig config = AWCommonConfig.get();
                    if (!config.realWeatherEnabled) {
                        ctx.getSource().sendFailure(Component.translatable("commands.aw.refresh.disabled"));
                        return 0;
                    }
                    ctx.getSource().sendSuccess(
                            () -> Component.translatable("commands.aw.refresh.fetching",
                                    config.latitude + ", " + config.longitude), false);

                    ServerLevel overworld = ctx.getSource().getServer().getLevel(Level.OVERWORLD);
                    if (overworld == null) return 0;

                    CommandSourceStack source = ctx.getSource();
                    OpenMeteo.fetchAsync(config.latitude, config.longitude)
                            .thenAccept(type -> overworld.getServer().execute(() -> {
                                if (type == null) {
                                    source.sendFailure(Component.translatable("commands.aw.refresh.failedfetch"));
                                    return;
                                }
                                WeatherManager.get(overworld).applyRealWeather(overworld, type);
                                source.sendSuccess(() -> Component.translatable("commands.aw.refresh.applied",
                                        type.name() + " (" + type.weatherName() + ")"), false);
                            }));
                    return 1;
                });
    }

    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(register());
    }
}
