package net.antopfr.advancedweather.client.render;

import com.google.common.base.Suppliers;
import net.minecraft.client.Minecraft;

import java.util.function.Supplier;

public class AWClientRenderers {
    public static final Supplier<InstrumentItemRenderer> INSTRUMENT_RENDERER =
            Suppliers.memoize(() -> new InstrumentItemRenderer(
                    Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                    Minecraft.getInstance().getEntityModels()));
}
