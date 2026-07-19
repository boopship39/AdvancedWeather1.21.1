package net.antopfr.advancedweather.client.gui.hud;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.config.AWClientConfig;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = "advancedweather", value = Dist.CLIENT)
public class WeatherHudOverlay {

    private static final int ICON_SIZE = 16;
    private static final int PADDING = 6;
    private static final int MARGIN = 8;

    public enum HudPosition { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        AWClientConfig config = AWClientConfig.get();
        if (!config.enableWeatherHud) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        if (mc.screen != null) return;


        GuiGraphics g = event.getGuiGraphics();
        WeatherTypes current = ClientWeatherState.getCurrentWeather();

        String label = current.weatherName();
        String timeLabel = config.weatherHudShowTime ? formatTime(mc.level.getDayTime() % 24000) : null;

        int textWidth = Math.max(
                mc.font.width(label),
                timeLabel != null ? mc.font.width(timeLabel) : 0
        );

        int boxWidth = ICON_SIZE + PADDING * 2 + textWidth + PADDING;
        int boxHeight = Math.max(ICON_SIZE + PADDING * 2, timeLabel != null ? 32 : 24);

        int[] pos = computePosition(g, config.weatherHudPosition, boxWidth, boxHeight);
        int x = pos[0];
        int y = pos[1];

        g.fill(x, y, x + boxWidth, y + boxHeight, 0x99000000);

        ResourceLocation icon = getIconFor(current);
        int iconY = y + (boxHeight - ICON_SIZE) / 2;
        g.blit(icon, x + PADDING, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);

        int textX = x + PADDING + ICON_SIZE + PADDING;
        int textY = timeLabel != null ? y + PADDING : y + (boxHeight - mc.font.lineHeight) / 2;

        g.drawString(mc.font, label, textX, textY, 0xFFFFFF, true);
        if (timeLabel != null) {
            g.drawString(mc.font, timeLabel, textX, textY + mc.font.lineHeight + 2, 0xAAAAAA, true);
        }
    }

    private static int[] computePosition(GuiGraphics g, HudPosition position, int boxWidth, int boxHeight) {
        int screenW = g.guiWidth();
        int screenH = g.guiHeight();

        return switch (position) {
            case TOP_LEFT     -> new int[]{MARGIN, MARGIN};
            case BOTTOM_LEFT  -> new int[]{MARGIN, screenH - boxHeight - MARGIN};
            case BOTTOM_RIGHT -> new int[]{screenW - boxWidth - MARGIN, screenH - boxHeight - MARGIN};
            default           -> new int[]{screenW - boxWidth - MARGIN, MARGIN};
        };
    }

    private static String formatTime(long dayTime) {
        int hours   = (int) ((dayTime + 6000) / 1000 % 24);
        int minutes = (int) ((dayTime % 1000) * 60 / 1000);
        return String.format("%02d:%02d", hours, minutes);
    }

    private static ResourceLocation getIconFor(WeatherTypes type) {
        String path = "textures/gui/weather_icons/" + type.name().toLowerCase() + ".png";
        return ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, path);
    }
}
