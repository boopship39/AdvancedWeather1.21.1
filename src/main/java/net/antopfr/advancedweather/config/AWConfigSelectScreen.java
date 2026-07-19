package net.antopfr.advancedweather.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class AWConfigSelectScreen extends Screen {

    private final Screen parent;

    public AWConfigSelectScreen(Screen parent) {
        super(Component.literal("Advanced Weather"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int by = this.height / 2 - 24;

        addRenderableWidget(Button.builder(
                        Component.literal("Client Settings"),
                        b -> this.minecraft.setScreen(AWConfigScreen.createClient(this)))
                .tooltip(Tooltip.create(Component.literal(
                        "Visuals, HUD, units, particles and fog")))
                .bounds(cx - 100, by, 200, 20)
                .build());

        addRenderableWidget(Button.builder(
                        Component.literal("Common Settings"),
                        b -> this.minecraft.setScreen(AWConfigScreen.createCommon(this)))
                .tooltip(Tooltip.create(Component.literal(
                        "Weather types, gameplay, real weather and forecasting.\n"
                                + "Applies in singleplayer and when hosting. On a dedicated "
                                + "server the server's own config file takes priority.")))
                .bounds(cx - 100, by + 24, 200, 20)
                .build());

        addRenderableWidget(Button.builder(
                        Component.literal("Done"),
                        b -> this.onClose())
                .bounds(cx - 100, by + 60, 200, 20)
                .build());
    }

    @Override
    public void render(@NotNull GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);
        g.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 60, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}
