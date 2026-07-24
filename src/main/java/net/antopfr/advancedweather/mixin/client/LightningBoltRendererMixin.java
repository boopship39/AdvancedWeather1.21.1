package net.antopfr.advancedweather.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.antopfr.advancedweather.client.state.ClientWeatherState;
import net.antopfr.advancedweather.weather.WeatherTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LightningBoltRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LightningBolt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LightningBoltRenderer.class)
public class LightningBoltRendererMixin {

    @Unique private static final float[] advancedweather$VANILLA = {0.45F, 0.45F, 0.50F};

    @Unique private boolean advancedweather$tinted;
    @Unique private float advancedweather$red;
    @Unique private float advancedweather$green;
    @Unique private float advancedweather$blue;

    @Inject(method = "render", at = @At("HEAD"))
    private void advancedweather$pickBoltColour(LightningBolt entity, float yaw, float partialTick,
                                                PoseStack poseStack, MultiBufferSource bufferSource,
                                                int packedLight, CallbackInfo ci) {
        this.advancedweather$tinted = false;
        if (!(entity.level() instanceof ClientLevel)) return;

        WeatherTypes weather = ClientWeatherState.getCurrentWeather();
        if (weather != WeatherTypes.THUNDERSTORM
                && weather != WeatherTypes.NETHERSTORM
                && weather != WeatherTypes.ENDERSTORM) {
            return;
        }

        RandomSource rng = RandomSource.create(entity.getId() * 0x9E3779B97F4A7C15L);
        float roll = rng.nextFloat();

        float[] colour;
        if (weather == WeatherTypes.NETHERSTORM) {
            if (roll < 0.55F)      colour = new float[]{1.00F, 0.22F, 0.10F}; // crimson
            else if (roll < 0.88F) colour = new float[]{1.00F, 0.55F, 0.12F}; // molten orange
            else                   colour = new float[]{0.75F, 0.10F, 0.85F}; // rare soul purple
        } else if (weather == WeatherTypes.ENDERSTORM) {
            if (roll < 0.55F)      colour = new float[]{0.90F, 0.15F, 1.00F}; // magenta
            else if (roll < 0.88F) colour = new float[]{0.15F, 0.95F, 0.65F}; // ender teal
            else                   colour = new float[]{0.45F, 0.10F, 0.85F}; // deep violet
        } else {
            if (roll < 0.60F)      colour = advancedweather$VANILLA;          // classic
            else if (roll < 0.85F) colour = new float[]{0.40F, 0.46F, 0.60F}; // colder blue
            else if (roll < 0.95F) colour = new float[]{0.52F, 0.44F, 0.58F}; // faint violet
            else                   colour = new float[]{0.58F, 0.48F, 0.40F}; // rare warm flash
        }

        this.advancedweather$red = advancedweather$jitter(colour[0], rng);
        this.advancedweather$green = advancedweather$jitter(colour[1], rng);
        this.advancedweather$blue = advancedweather$jitter(colour[2], rng);
        this.advancedweather$tinted = true;
    }

    @ModifyArgs(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/LightningBoltRenderer;quad("
                            + "Lorg/joml/Matrix4f;Lcom/mojang/blaze3d/vertex/VertexConsumer;"
                            + "FFIFFFFFFFZZZZ)V"
            )
    )
    private void advancedweather$tintQuad(Args args) {
        if (!this.advancedweather$tinted) return;
        args.set(7, this.advancedweather$red);
        args.set(8, this.advancedweather$green);
        args.set(9, this.advancedweather$blue);
    }

    @Unique
    private static float advancedweather$jitter(float channel, RandomSource rng) {
        return Mth.clamp(channel + (rng.nextFloat() - 0.5F) * 0.06F, 0.0F, 1.0F);
    }
}
