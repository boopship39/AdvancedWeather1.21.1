package net.antopfr.advancedweather.content.entity;

import com.tterrag.registrate.util.entry.EntityEntry;
import net.antopfr.advancedweather.util.AWRegistrate;
import net.antopfr.advancedweather.weather.effect.types.chorus_plants.ChorusPlantEntity;
import net.antopfr.advancedweather.weather.effect.types.rainbows.RainbowEntity;
import net.antopfr.advancedweather.weather.effect.types.tumbleweeds.TumbleweedEntity;
import net.minecraft.world.entity.MobCategory;

public class AWEntities {

    public static final EntityEntry<TumbleweedEntity> TUMBLEWEED =
            AWRegistrate.get()
                    .<TumbleweedEntity>entity("tumbleweed", TumbleweedEntity::new, MobCategory.MISC)
                    .properties(b -> b
                            .sized(0.7f, 0.7f)
                            .clientTrackingRange(8)
                            .updateInterval(3))
                    .register();

    public static final EntityEntry<ChorusPlantEntity> CHORUS_PLANT =
            AWRegistrate.get()
                    .<ChorusPlantEntity>entity("chorus_plant", ChorusPlantEntity::new, MobCategory.MISC)
                    .properties(b -> b
                            .sized(0.7f, 0.7f)
                            .clientTrackingRange(8)
                            .updateInterval(3))
                    .register();

    public static final EntityEntry<RainbowEntity> RAINBOW =
            AWRegistrate.get()
                    .<RainbowEntity>entity("rainbow", RainbowEntity::new, MobCategory.MISC)
                    .properties(b -> b
                            .sized(0.5f, 0.5f)
                            .clientTrackingRange(22)
                            .updateInterval(20))
                    .register();

    public static void register() {}
}
