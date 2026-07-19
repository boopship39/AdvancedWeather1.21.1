package net.antopfr.advancedweather.content.block.calibration;

import net.antopfr.advancedweather.content.item.AWItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class CalibrationScreenOpener {

    public static boolean isCalibratable(ItemStack stack) {
        return stack.is(AWItems.SPRING.get())
                || stack.is(AWItems.SENSITIVE_FIBER.get())
                || stack.is(AWItems.CUP_ROTOR.get());
    }

    public static void open(BlockPos benchPos, ItemStack piece, InteractionHand hand) {
        Screen screen = null;

        if (piece.is(AWItems.SPRING.get()))
            screen = new SpringCalibrationScreen(benchPos, piece, hand);
        else if (piece.is(AWItems.SENSITIVE_FIBER.get()))
            screen = new FiberCalibrationScreen(benchPos, piece, hand);
        else if (piece.is(AWItems.CUP_ROTOR.get()))
            screen = new RotorCalibrationScreen(benchPos, piece, hand);

        if (screen != null) {
            Minecraft.getInstance().setScreen(screen);
        }
    }
}
