package net.antopfr.advancedweather.client.sound.wind;

import net.antopfr.advancedweather.client.state.ClientAtmosphereState;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WindBurstSound extends AbstractTickableSoundInstance {

    private final Player player;
    private final boolean isHeavyType;
    private float currentVolume = 0.0F;

    public WindBurstSound(Player player, SoundEvent soundEvent, boolean isHeavyType) {
        super(soundEvent, SoundSource.WEATHER, RandomSource.create());
        this.player = player;
        this.isHeavyType = isHeavyType;
        this.looping = true;
        this.delay = 0;
        this.relative = true;

        this.currentVolume = calculateTargetVolume();
        this.volume = Math.max(0.001F, this.currentVolume);
    }

    @Override
    public void tick() {
        if (this.player == null || this.player.isRemoved()) {
            this.stop();
            return;
        }

        float targetVolume = calculateTargetVolume();

        float lerpSpeed = (targetVolume > currentVolume) ? 0.05F : 0.02F;
        currentVolume += (targetVolume - currentVolume) * lerpSpeed;

        this.volume = Math.max(0.001F, currentVolume);

        if (isHeavyType) {
            this.pitch = 0.85F + (currentVolume * 0.20F);
        } else {
            this.pitch = 1.00F + (currentVolume * 0.15F);
        }
    }

    private float calculateTargetVolume() {
        float windIntensity = ClientAtmosphereState.getWindIntensity();

        if (isHeavyType) {

            if (windIntensity < 0.3F) return 0.0F;
            return Mth.clamp((windIntensity - 0.3F) / 0.7F, 0.0F, 1.0F);
        } else {
            return windIntensity * 0.7F;
        }
    }
}
