package net.antopfr.advancedweather.content.item.kite;

import net.antopfr.advancedweather.content.AWDataComponents;
import net.antopfr.advancedweather.content.AWRecipeSerializers;
import net.antopfr.advancedweather.content.item.AWItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class KiteRecolorRecipe extends CustomRecipe {

    public KiteRecolorRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, @NotNull Level level) {
        if (input.width() != 3 || input.height() != 3) return false;
        if (!(input.getItem(1, 1).getItem() instanceof KiteItem)) return false;

        if (!input.getItem(1, 0).isEmpty() || !input.getItem(1, 2).isEmpty()) return false;
        if (!input.getItem(0, 1).isEmpty() || !input.getItem(2, 1).isEmpty()) return false;

        int dyes = 0;
        for (int[] p : new int[][]{{0,0},{2,0},{0,2},{2,2}}) {
            ItemStack s = input.getItem(p[0], p[1]);
            if (s.isEmpty()) continue;
            if (!KiteCraftRecipe.isDye(s)) return false;
            dyes++;
        }
        return dyes > 0;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput input, HolderLookup.@NotNull Provider registries) {
        KiteColors cur = input.getItem(1, 1).getOrDefault(
                AWDataComponents.KITE_COLORS.get(), KiteColors.WHITE);

        ItemStack kite = new ItemStack(AWItems.KITE_ITEM.get());
        kite.set(AWDataComponents.KITE_COLORS.get(), new KiteColors(
                pick(input, 2, 0, cur.topRight()),
                pick(input, 0, 0, cur.topLeft()),
                pick(input, 2, 2, cur.bottomRight()),
                pick(input, 0, 2, cur.bottomLeft())));
        return kite;
    }

    private static int pick(CraftingInput input, int x, int y, int fallback) {
        ItemStack s = input.getItem(x, y);
        return s.isEmpty() ? fallback : KiteCraftRecipe.dyeColor(s);
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) { return w == 3 && h == 3; }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() { return AWRecipeSerializers.KITE_RECOLOR.get(); }
}
