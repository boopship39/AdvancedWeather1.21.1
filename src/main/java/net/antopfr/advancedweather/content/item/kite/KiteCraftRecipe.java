package net.antopfr.advancedweather.content.item.kite;

import net.antopfr.advancedweather.content.AWDataComponents;
import net.antopfr.advancedweather.content.AWRecipeSerializers;
import net.antopfr.advancedweather.content.item.AWItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class KiteCraftRecipe extends CustomRecipe {

    public KiteCraftRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, @NotNull Level level) {
        if (input.width() != 3 || input.height() != 3) return false;

        boolean corners = isDye(input.getItem(0, 0)) && isDye(input.getItem(2, 0))
                && isDye(input.getItem(0, 2)) && isDye(input.getItem(2, 2));

        boolean frame = input.getItem(1, 0).is(Items.PAPER)
                && input.getItem(1, 1).is(Items.LEAD)
                && input.getItem(1, 2).is(AWItems.ALUMINUM_NUGGET.get());

        boolean sidesEmpty = input.getItem(0, 1).isEmpty() && input.getItem(2, 1).isEmpty();

        return corners && frame && sidesEmpty;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput input, HolderLookup.@NotNull Provider registries) {
        ItemStack kite = new ItemStack(AWItems.KITE_ITEM.get());
        kite.set(AWDataComponents.KITE_COLORS.get(), new KiteColors(
                dyeColor(input.getItem(2, 0)),   // haut-droit
                dyeColor(input.getItem(0, 0)),   // haut-gauche
                dyeColor(input.getItem(2, 2)),   // bas-droit
                dyeColor(input.getItem(0, 2)))); // bas-gauche
        return kite;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) { return w == 3 && h == 3; }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() { return AWRecipeSerializers.KITE_CRAFT.get(); }

    static boolean isDye(ItemStack s) { return s.getItem() instanceof DyeItem; }

    public static int dyeColor(ItemStack s) {
        if (!(s.getItem() instanceof DyeItem dye)) return 0xFFFFFF;
        return switch (dye.getDyeColor()) {
            case WHITE -> 0xFFFFFF; case ORANGE -> 0xFF8C1A;
            case MAGENTA -> 0xE050C0; case LIGHT_BLUE -> 0x40B0E0;
            case YELLOW -> 0xFFD520;  case LIME -> 0x80D010;
            case PINK -> 0xFF9EC0;    case GRAY -> 0x606060;
            case LIGHT_GRAY -> 0xA0A0A0; case CYAN -> 0x18A0A0;
            case PURPLE -> 0x9020C0;  case BLUE -> 0x3040D0;
            case BROWN -> 0x805030;   case GREEN -> 0x50A020;
            case RED -> 0xE02020;     case BLACK -> 0x202020;
        };
    }
}
