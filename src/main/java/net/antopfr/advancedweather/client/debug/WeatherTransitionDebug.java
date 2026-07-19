package net.antopfr.advancedweather.client.debug;

import net.antopfr.advancedweather.client.state.ClientTransitionState;
import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.network.toclient.TransitionProbabilitiesPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class WeatherTransitionDebug extends Screen {

    private static final int ROW_HEIGHT = 16;
    private static final int BAR_MAX_WIDTH = 200;
    private static final int PADDING = 14;

    public WeatherTransitionDebug() {
        super(Component.literal("Weather Transition Probabilities"));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, width, height, 0xDD000000);
        super.render(g, mouseX, mouseY, partialTick);

        int x = PADDING + 24;
        int y = PADDING + 20;

        g.drawCenteredString(this.font,
                "§bCurrent: §f" + ClientWeatherState.getCurrentWeather().weatherName(),
                this.width / 2, 10, 0xFFFFFF);

        List<TransitionProbabilitiesPacket.Entry> probabilities = ClientTransitionState.getProbabilities();

        if (probabilities.isEmpty()) {
            g.drawString(this.font, "§7No transition data available", x, y, 0xFFFFFF);
            return;
        }

        for (TransitionProbabilitiesPacket.Entry entry : probabilities) {
            String label = entry.type().weatherName();
            float percent = entry.probabilityPercent();

            g.drawString(this.font, label, x, y + 3, 0xFFFFFF);

            int barWidth = (int) (BAR_MAX_WIDTH * (percent / 100f));
            int barX = x + 130;
            g.fill(barX, y, barX + BAR_MAX_WIDTH, y + 12, 0x55FFFFFF);
            g.fill(barX, y, barX + barWidth, y + 12, getBarColor(percent));

            g.drawString(this.font, String.format("%.1f%%", percent), barX + BAR_MAX_WIDTH + 8, y + 3, 0xFFFFFF);

            y += ROW_HEIGHT;
        }

        g.drawCenteredString(this.font, "§8[ESC] Close", this.width / 2, this.height - PADDING, 0x666666);
    }

    private int getBarColor(float percent) {
        if (percent >= 30f) return 0xFF55FF55;
        if (percent >= 15f) return 0xFFFFFF55;
        return 0xFFFF5555;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
