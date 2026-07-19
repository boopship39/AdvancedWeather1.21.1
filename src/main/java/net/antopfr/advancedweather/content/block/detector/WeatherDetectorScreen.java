package net.antopfr.advancedweather.content.block.detector;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.network.toserver.SetDetectorModePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.PacketDistributor;

public class WeatherDetectorScreen extends Screen {

    private static final int PANEL_W = 170;
    private static final int ROW_H = 20;
    private static final int HEADER_H = 20;

    private final BlockPos pos;
    private final WeatherDetectorBlock.DetectionMode current;
    private float openProgress = 0f;
    private int hoveredIndex = -1;

    public WeatherDetectorScreen(BlockPos pos, WeatherDetectorBlock.DetectionMode current) {
        super(Component.literal("Weather Detector"));
        this.pos = pos;
        this.current = current;
    }

    private int panelHeight() {
        return HEADER_H + WeatherDetectorBlock.DetectionMode.values().length * ROW_H + 8;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        openProgress = Math.min(1f, openProgress + partialTick * 0.15f);
        float ease = 1f - (1f - openProgress) * (1f - openProgress);

        g.fill(0, 0, width, height, ((int) (ease * 0x90)) << 24);
        super.render(g, mouseX, mouseY, partialTick);

        int panelH = panelHeight();
        int x = (width - PANEL_W) / 2;
        int y = (int) Mth.lerp(ease, (height - panelH) / 2f + 25, (height - panelH) / 2f);
        int alpha = Math.max(4, (int) (ease * 0xFF));

        g.fill(x, y, x + PANEL_W, y + panelH, ((int) (ease * 0xE0) << 24) | 0x101418);
        g.fill(x, y, x + PANEL_W, y + HEADER_H, (alpha << 24) | 0x1E2630);
        g.drawCenteredString(font, "§lDetection Mode", x + PANEL_W / 2, y + 6, withAlpha(0xFFFFFF, alpha));

        WeatherDetectorBlock.DetectionMode[] modes = WeatherDetectorBlock.DetectionMode.values();
        hoveredIndex = -1;

        for (int i = 0; i < modes.length; i++) {
            int rowY = y + HEADER_H + 4 + i * ROW_H;
            boolean isCurrent = modes[i] == current;
            boolean hovered = mouseX >= x + 4 && mouseX < x + PANEL_W - 4
                    && mouseY >= rowY && mouseY < rowY + ROW_H;
            if (hovered) hoveredIndex = i;

            if (isCurrent) {
                g.fill(x + 4, rowY, x + PANEL_W - 4, rowY + ROW_H - 2, (alpha << 24) | 0x2A4A66);
            } else if (hovered) {
                g.fill(x + 4, rowY, x + PANEL_W - 4, rowY + ROW_H - 2, ((int) (ease * 0x50) << 24) | 0x30404C);
            }

            ResourceLocation icon = ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID,
                    "textures/gui/detector_modes/" + modes[i].getSerializedName() + ".png");
            g.blit(icon, x + 8, rowY + 2, 16, 16, 0, 0, 32, 32, 32, 32);

            String label = Component.translatable(
                    "advancedweather.detector.mode." + modes[i].getSerializedName()).getString();
            int textColor = isCurrent ? 0x7FD4FF : hovered ? 0xFFFFFF : 0xAAAAAA;
            g.drawString(font, label, x + 30, rowY + 6, withAlpha(textColor, alpha), true);

            if (isCurrent) {
                g.drawString(font, "●", x + PANEL_W - 16, rowY + 5, withAlpha(0x7FD4FF, alpha), true);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && hoveredIndex >= 0) {
            WeatherDetectorBlock.DetectionMode selected = WeatherDetectorBlock.DetectionMode.values()[hoveredIndex];
            if (selected != current) {
                PacketDistributor.sendToServer(
                        new SetDetectorModePacket(pos, selected));
            }
            onClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static int withAlpha(int rgb, int alpha) {
        return (Mth.clamp(alpha, 0, 255) << 24) | (rgb & 0x00FFFFFF);
    }
}