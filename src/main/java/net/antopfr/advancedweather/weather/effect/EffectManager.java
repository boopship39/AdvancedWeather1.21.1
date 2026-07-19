package net.antopfr.advancedweather.weather.effect;

import net.antopfr.advancedweather.network.toclient.EffectSyncPacket;
import net.antopfr.advancedweather.weather.WeatherEffects;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Set;

public class EffectManager extends SavedData {

    private static final String NAME = "advancedweather_effects";
    private final Set<WeatherEffects> activeEffects = EnumSet.noneOf(WeatherEffects.class);

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
        ListTag list = new ListTag();
        for (WeatherEffects effect : activeEffects) {
            list.add(StringTag.valueOf(effect.name()));
        }
        tag.put("effects", list);
        return tag;
    }

    private static EffectManager load(CompoundTag tag, HolderLookup.Provider provider) {
        EffectManager manager = new EffectManager();
        ListTag list = tag.getList("effects", 8);
        for (int i = 0; i < list.size(); i++) {
            WeatherEffects effect = WeatherEffects.fromNameSafe(list.getString(i));
            if (effect != null) manager.activeEffects.add(effect);
        }
        return manager;
    }

    public static EffectManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(EffectManager::new, EffectManager::load),
                NAME
        );
    }

    public Set<WeatherEffects> getActiveEffects() {
        return activeEffects;
    }

    public boolean hasEffect(WeatherEffects effect) {
        return activeEffects.contains(effect);
    }

    public void setEffects(ServerLevel level, Set<WeatherEffects> effects) {
        if (activeEffects.equals(effects)) {
            return;
        }
        activeEffects.clear();
        activeEffects.addAll(effects);
        setDirty();
        syncToClients(level);
    }

    public void resync(ServerLevel level) {
        PacketDistributor.sendToPlayersInDimension(level, new EffectSyncPacket(
                activeEffects.isEmpty()
                        ? EnumSet.noneOf(WeatherEffects.class)
                        : EnumSet.copyOf(activeEffects)
        ));
    }

    private void syncToClients(ServerLevel level) {
        resync(level);
    }

    public void addEffect(ServerLevel level, WeatherEffects effect) {
        if (activeEffects.add(effect)) {
            setDirty();
            syncToClients(level);
        }
    }

    public void removeEffect(ServerLevel level, WeatherEffects effect) {
        if (activeEffects.remove(effect)) {
            setDirty();
            syncToClients(level);
        }
    }
}
