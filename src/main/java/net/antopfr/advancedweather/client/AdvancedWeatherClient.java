package net.antopfr.advancedweather.client;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.client.command.AWClientCommand;
import net.antopfr.advancedweather.config.AWConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = AdvancedWeather.MOD_ID, dist = Dist.CLIENT)
public class AdvancedWeatherClient {
    public AdvancedWeatherClient() {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class,
                () -> (container, parent) -> AWConfigScreen.create(parent)
        );

        NeoForge.EVENT_BUS.addListener(AWClientCommand::onRegisterClientCommands);
    }
}
