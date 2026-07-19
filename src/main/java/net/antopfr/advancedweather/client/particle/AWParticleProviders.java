package net.antopfr.advancedweather.client.particle;

import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.client.particle.types.MercuryBubblePopParticle;
import net.antopfr.advancedweather.weather.effect.types.ash_particles.AshParticle;
import net.antopfr.advancedweather.weather.effect.types.blizzard_particles.BlizzardParticle;
import net.antopfr.advancedweather.weather.effect.types.brimstone_particles.BrimstoneParticle;
import net.antopfr.advancedweather.weather.effect.types.fog_wisps.FogWispParticle;
import net.antopfr.advancedweather.weather.effect.types.ground_fog.GroundFogParticle;
import net.antopfr.advancedweather.weather.effect.types.sand_particles.SandParticle;
import net.antopfr.advancedweather.client.particle.types.FreezingSplashParticle;
import net.antopfr.advancedweather.client.particle.types.HailDropParticle;
import net.antopfr.advancedweather.client.particle.types.LeafParticle;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = AdvancedWeather.MOD_ID, value = Dist.CLIENT)
public class AWParticleProviders {
    @SubscribeEvent
    public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(AWParticles.FREEZING_RAIN.get(), FreezingSplashParticle.Provider::new);
        event.registerSpriteSet(AWParticles.BLIZZARD_FLAKE.get(), BlizzardParticle.Provider::new);
        event.registerSpriteSet(AWParticles.FOG_WISP.get(), FogWispParticle.Provider::new);
        event.registerSpriteSet(AWParticles.GROUND_FOG.get(), GroundFogParticle.Provider::new);
        event.registerSpriteSet(AWParticles.SAND_PARTICLE.get(), SandParticle.Provider::new);
        event.registerSpriteSet(AWParticles.RED_SAND_PARTICLE.get(), SandParticle.Provider::new);
        event.registerSpriteSet(AWParticles.SOUL_SAND_PARTICLE.get(), SandParticle.Provider::new);
        event.registerSpriteSet(AWParticles.BRIMSTONE_DUST.get(), BrimstoneParticle.Provider::new);
        event.registerSpriteSet(AWParticles.LEAF.get(), LeafParticle.Provider::new);
        event.registerSpriteSet(AWParticles.HAIL_DROP.get(), HailDropParticle.Provider::new);
        event.registerSpriteSet(AWParticles.ASH_PARTICLE.get(), AshParticle.Provider::new);
        event.registerSpriteSet(AWParticles.MERCURY_BUBBLE_POP.get(), MercuryBubblePopParticle.Provider::new);
    }
}
