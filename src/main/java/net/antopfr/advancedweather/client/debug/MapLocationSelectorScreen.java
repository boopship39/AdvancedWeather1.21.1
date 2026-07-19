package net.antopfr.advancedweather.client.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import net.antopfr.advancedweather.api.external.NominatimGeocoding;
import net.antopfr.advancedweather.config.AWCommonConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public class MapLocationSelectorScreen extends Screen {

    private static final ResourceLocation MAP_TEXTURE =
            ResourceLocation.fromNamespaceAndPath("advancedweather", "textures/gui/map.png");

    private final int mapW = 360;
    private final int mapH = 180;
    private int mapX, mapY;

    private double selectedLat;
    private double selectedLon;

    public MapLocationSelectorScreen() {
        super(Component.literal("Select Weather Location"));

        AWCommonConfig config = AWCommonConfig.get();
        this.selectedLat = config.latitude;
        this.selectedLon = config.longitude;
    }

    @Override
    protected void init() {
        this.mapX = (this.width - this.mapW) / 2;
        this.mapY = (this.height - this.mapH) / 2;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        g.fill(0, 0, this.width, this.height, 0xCC000000);
        super.render(g, mouseX, mouseY, partialTick);

        g.drawCenteredString(font, "§bAdvancedWeather §7- Select Location", this.width / 2, this.mapY - 20, 0xFFFFFF);

        RenderSystem.setShaderTexture(0, MAP_TEXTURE);
        g.blit(MAP_TEXTURE, mapX, mapY, 0, 0, mapW, mapH, mapW, mapH);

        int markerX = mapX + (int) ((selectedLon + 180.0) * mapW / 360.0);
        int markerY = mapY + (int) ((90.0 - selectedLat) * mapH / 180.0);

        g.fill(markerX - 2, markerY - 2, markerX + 2, markerY + 2, 0xFFFF2222);

        String coordsText = String.format("Selected: %.4f, %.4f", selectedLat, selectedLon);
        g.drawCenteredString(font, coordsText, this.width / 2, mapY + mapH + 8, 0xAAAAAA);
        g.drawCenteredString(font, "§8Click on the map to change location - [ESC] to Save & Close", this.width / 2, mapY + mapH + 20, 0x666666);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX >= mapX && mouseX < mapX + mapW && mouseY >= mapY && mouseY < mapY + mapH) {
            double clickedPixelX = mouseX - mapX;
            double clickedPixelY = mouseY - mapY;

            this.selectedLon = (clickedPixelX * 360.0 / mapW) - 180.0;
            this.selectedLat = 90.0 - (clickedPixelY * 180.0 / mapH);

            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.25F, 1.0F);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        AWCommonConfig config = AWCommonConfig.get();
        config.latitude = this.selectedLat;
        config.longitude = this.selectedLon;
        config.save();

        NominatimGeocoding.invalidateCache();
        NominatimGeocoding.fetchLocationNameAsync(config.latitude, config.longitude);

        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
