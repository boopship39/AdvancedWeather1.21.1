package net.antopfr.advancedweather.compat.jei;

import net.antopfr.advancedweather.content.item.AWItems;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record CalibrationRecipe(ItemStack input, ItemStack output) {

    public static List<CalibrationRecipe> all() {
        return List.of(
                new CalibrationRecipe(new ItemStack(AWItems.SPRING.get()),
                        new ItemStack(AWItems.CALIBRATED_SPRING.get())),
                new CalibrationRecipe(new ItemStack(AWItems.CUP_ROTOR.get()),
                        new ItemStack(AWItems.CALIBRATED_CUP_ROTOR.get())),
                new CalibrationRecipe(new ItemStack(AWItems.SENSITIVE_FIBER.get()),
                        new ItemStack(AWItems.CALIBRATED_SENSITIVE_FIBER.get()))
        );
    }
}
