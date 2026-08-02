package net.antopfr.advancedweather.compat.jei;

import net.antopfr.advancedweather.content.block.AWBlocks;
import net.antopfr.advancedweather.content.item.AWItems;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record AmalgamationRecipe(ItemStack aluminum, ItemStack mercury, ItemStack output) {

    public static List<AmalgamationRecipe> all() {
        return List.of(
                new AmalgamationRecipe(
                        new ItemStack(AWBlocks.ALUMINUM_BLOCK.get()),
                        new ItemStack(AWItems.MERCURY_VIAL.get()),
                        new ItemStack(AWItems.DORMANT_CRYSTAL.get()))
        );
    }
}
