# Advanced Weather

*A physically-grounded weather overhaul for Minecraft - 28 weather types across all three dimensions, a real atmospheric simulation, and a full progression of craftable weather instruments.*

<!-- Badges: replace the URLs once the project is published -->
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen)]()
[![Loader](https://img.shields.io/badge/Loader-NeoForge-orange)]()
[![License](https://img.shields.io/badge/License-MIT-blue)]()

<!-- Hero image: 1920x1080 in-game shot of a dramatic weather (blizzard or sandstorm) with wind lines visible -->
<p align="center">
  <img src="docs/images/banner.png" alt="Advanced Weather" width="100%">
</p>

---

## What is Advanced Weather?

Vanilla Minecraft has three weather states: clear, rain, and thunder. Advanced Weather replaces that with a living atmosphere. Air pressure, temperature, dew point and wind are simulated continuously and drive **28 distinct weather types** - each with its own particles, fog, sky, sound and gameplay effects. Weather doesn't teleport between states: it *transitions* based on the current atmospheric conditions, and you can actually **read, predict and archive** it using an in-world progression of scientific instruments you build yourself.

You can let it run procedurally, sync it to **real-world weather** at any location on Earth, or take full manual control.

---

## Features

### 🌍 A real atmospheric simulation

Runs a continuous model:

- **Air pressure** driven by layered simplex noise, with realistic velocity, damping and gusting.
- **Temperature** derived from the weather type, the solar day/night cycle, adiabatic pressure effects and (optionally) the current season.
- **Humidity** computed from temperature and dew point using the Magnus relative-humidity formula.
- **Wind** with base intensity per weather type, pressure-gradient contribution and randomized gusts.
- **Probabilistic transitions** - the next weather is chosen from a transition graph weighted by live atmospheric conditions, so a falling barometer genuinely means a storm is coming.
- **Forecasting** - the engine projects the atmosphere forward in time to predict the *next* weather and the weather *in 30 minutes*, each with a confidence score.

Everything is per-dimension and saved with the world.

### 🌦️ 28 weather types across three dimensions

**Overworld (17):** Clear · Sunny · Cloudy · Overcast · Mist · Fog · Dense Fog · Drizzle · Light Rain · Heavy Rain · Freezing Rain · Thunderstorm · Snow · Blizzard · Hail · Windy · Sandstorm

**Nether (6):** Nether Clear · Ash Storm · Brimstone Storm · Lava Rain · Netherstorm · Hellfire

**The End (5):** End Clear · End Mist · Void Storm · Chorus Gale · Enderstorm

### ✨ Atmospheric effects

Each weather brings its own hand-tuned visuals and audio:

- Volumetric **wind lines** and directional wind sound that scale with intensity and gusts.
- Weather-specific particles: blizzard flakes, sand, ash, ground fog, freezing splashes, hail, ember/void particles.
- **Rainbows** that can spawn after rain clears, **heat shimmer** in extreme heat, **water and lava ripples**, wind-blown **tumbleweeds** and drifting **chorus plants**.
- Dynamic **fog, sky, cloud, sun, star and lightning** rendering that reacts to the current weather.

### ⚡ Weather that affects gameplay

- **Hail** can damage crops, hurt exposed entities and break glass (each toggleable).
- **Heavy rain** slows crop growth.
- **Blizzards** deepen snow accumulation.
- Mobs **seek shelter** from dangerous weather.
- Lightning behaviour is reworked for storms.

*(All gameplay impacts are individually configurable - see [Configuration](#configuration).)*

### 🔬 Instruments & progression

Advanced Weather is also a **tech-progression mod**. You can't just read the weather - you have to build the tools.

- **New ores & materials:** Cinnabar (→ washed dust → distilled into **Mercury**) and Bauxite (→ Alumina → carbothermic reduction → **Aluminum**, ingots, nuggets and sheets). A **Hammer** lets you crush and press by hand (or use Create's crushing wheels).
- **Four core instruments**, each built from a calibrated heart: **Thermometer** (mercury vial), **Barometer** (spring), **Hygrometer** (sensitive fiber), **Anemometer** (cup rotor).
- **Calibration Bench** and **Calibration Tool** - components must be calibrated before assembly, and sensors linked to stations.
- **Portable** versions of every instrument for readings on the move.

### 📟 The weather station network

Build a working meteorological station:

- **Weather Station** - displays live readings; link sensors to unlock each measurement. Right-click with paper to **print a Weather Report**; rename it to your liking.
- **Sensors** (Thermometer / Barometer / Hygrometer / Anemometer) with **realistic placement rules** - a thermometer must be sheltered from direct sky with airflow; an anemometer needs open sky and clear sides; etc. Bad placement gives bad readings.
- **Weather Archive** - store reports and almanacs; it studies your measurement history to **forecast the next weather**, rejecting duplicates and reports older than its memory.
- **Auto-Sampler** - automatically feeds station data into an archive at a configurable interval.
- **Weather Detector** - emits redstone based on weather (rain, snow, storm, thunderstorm, hail, or an analog 0–15 wind signal).
- **Weather Almanac** - a portable record of archived trends.

Two keybinds open a **weather history graph** and a **probable-transitions view** so you can study the atmosphere directly.

### 🌐 Real-world weather mode *(opt-in)*

Enable it in the config and Advanced Weather will pull **live weather for any latitude/longitude on Earth** (via Open-Meteo) and reverse-geocode the place name (via OpenStreetMap Nominatim), mapping real conditions onto Minecraft's weather system.

> **Privacy note:** this mode is **disabled by default**. When enabled, the coordinates you set in the config are sent to Open-Meteo and OpenStreetMap Nominatim to fetch weather and a location name. No data leaves your game unless you turn this on.

---

## Configuration

Config files are generated in your `config/` folder (`advancedweather-common.json` and the client config). Highlights:

- **Weather toggles** - enable/disable any weather type per dimension (Overworld, Nether, End).
- **Gameplay** - `hailDamageCrops`, `hailDamageEntities`, `hailBreakGlass`, `heavyRainSlowCrops`.
- **Instruments** - `stationRequiresSensors`, `sensorLinkMaxDistance`.
- **Archive/forecasting** - record capacity, minimum records for a forecast, confidence curve, forecast horizon, data-aging thresholds.
- **Real weather** - `realWeatherEnabled`, `latitude`, `longitude`, `syncIntervalMinutes`, `realWeatherOverridesCommands`.
- **Create compat** - `enableCreateWindmillCompat`, `windmillWindThreshold`, `windmillDirectionAffectsOutput`, `windmillSpeedScaleFactor`.

A **YACL config screen** is provided for in-game editing.

---

## Compatibility

| Mod | Type | What it adds |
|-----|------|--------------|
| **NeoForge** | Required | Loader |
| **YetAnotherConfigLib (YACL)** | Required (client) | In-game config screen |
| **Create** | Optional | Windmills react to real wind (speed & direction); weather data on Display Boards |
| **Serene Seasons** | Optional | Season-aware temperature & dew point |
| **Better Clouds** | Optional (client) | Enhanced cloud rendering integration |
| **JEI** | Optional (client) | Calibration Bench recipes in the recipe viewer |

---

## Requirements

- **Minecraft** 1.21.1
- **NeoForge** 21.1.230+
- **YetAnotherConfigLib** 3.6+

---

## Commands

All commands live under `/aw`. Server commands require permission level 2 (operator); the two client commands run locally and need no permissions.

**Read:**

| Command | Description                                                                    |
|---|--------------------------------------------------------------------------------|
| `/aw` | Show the current weather                                                       |
| `/aw history` | Recent weather history                                                         |

**Control:**

| Command | Description |
|---|---|
| `/aw set <type>` | Set and pin a weather type |
| `/aw auto` | Hand control back to the weather engine |
| `/aw refresh` | Pull real-world weather now (requires real weather enabled) |
| `/aw setlocation <lat> <lon>` | Set the real-weather coordinates |
| `/aw effect add\|remove <effect>` | Toggle an individual visual effect |
| `/aw force <pressure> <dewpoint> <temp> <seconds>` | Nudge the atmosphere for a duration |
| `/aw force clear` | Clear active atmospheric forcings |
| `/aw rainbow spawn\|clear` | Spawn or remove rainbows |
| `/aw debug` | Toggle the debug overlay |

**Client (run locally, no server needed):**

| Command | Description |
|---|---|
| `/aw map` | Open the location picker for real-weather mode |
| `/aw reset` | Clear the client's cached weather data |

---

## For developers

Advanced Weather exposes a small, stable API under `net.antopfr.advancedweather.api`. Everything **outside** that package is internal and may change without notice.

**Server-side reads & control** - `AdvancedWeatherAPI` (call on the server thread):

```java
import net.antopfr.advancedweather.api.AdvancedWeatherAPI;
import net.antopfr.advancedweather.weather.WeatherTypes;

WeatherTypes now = AdvancedWeatherAPI.getCurrentWeather(serverLevel);
AtmosphereSnapshot atmo = AdvancedWeatherAPI.getAtmosphere(serverLevel); // pressure, temp, humidity, wind...
WeatherForecast fc = AdvancedWeatherAPI.getForecast(serverLevel);        // next + in-30-min, with confidence

AdvancedWeatherAPI.setWeather(serverLevel, WeatherTypes.THUNDERSTORM);   // returns false if wrong dimension
AdvancedWeatherAPI.startAutoWeather(serverLevel);                        // hand control back to the engine
AdvancedWeatherAPI.applyForcing(serverLevel, -30f, 8f, 0f, 20 * 60);     // nudge pressure/dew point/temp
```

**Listen for changes** - `WeatherChangeEvent` on the NeoForge bus:

```java
NeoForge.EVENT_BUS.addListener((WeatherChangeEvent e) -> {
    if (e.getCurrent() == WeatherTypes.BLIZZARD) { /* ... */ }
});
```

**Client-side reads** (HUD add-ons, client thread only) - `AdvancedWeatherClientAPI`:
`getCurrentWeather()`, `getPressure()`, `getTemperature()`, `getHumidity()`, `getWindIntensity()`, `getPredictedNext()`, and more.

Registering **custom weather types** from another mod is not supported yet - planned for a later API revision.

---

## License

Released under the **MIT License**. See [LICENSE](LICENSE).

## Credits

Created by **Antopfr**.

Real-world weather data © [Open-Meteo](https://open-meteo.com/). Location names © [OpenStreetMap contributors](https://www.openstreetmap.org/copyright) via Nominatim.