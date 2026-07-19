package net.antopfr.advancedweather.weather;

import net.minecraft.util.Mth;

import java.util.List;

public class ForecastConfidence {

    public static float compute(List<WeatherTransitionGraph.Transition> transitions,
                                float[] finalWeights,
                                AtmosphericSystem atmosphere,
                                int forecastMinutes) {

        float timeFactor = computeTimeFactor(forecastMinutes);
        float marginFactor = computeMarginFactor(finalWeights);
        float stabilityFactor = computeStabilityFactor(atmosphere);

        float confidence = timeFactor * marginFactor * stabilityFactor;
        return Mth.clamp(confidence * 100f, 5f, 98f);
    }

    private static float computeTimeFactor(int forecastMinutes) {
        if (forecastMinutes <= 0) return 1.0f;
        return (float) Math.exp(-forecastMinutes / 65.0);
    }

    private static float computeMarginFactor(float[] weights) {
        if (weights.length <= 1) return 1.0f;

        float[] sorted = weights.clone();
        java.util.Arrays.sort(sorted);
        float best = sorted[sorted.length - 1];
        float secondBest = sorted[sorted.length - 2];

        float total = 0f;
        for (float w : weights) total += w;
        if (total <= 0f) return 0.5f;

        float bestShare = best / total;
        float margin = (best - secondBest) / total;

        return Mth.clamp(bestShare * 0.6f + margin * 0.4f + 0.15f, 0.1f, 1.0f);
    }


    private static float computeStabilityFactor(AtmosphericSystem atmosphere) {
        float pressureChange = Math.abs(atmosphere.getPressureVel());
        float instability = Mth.clamp(pressureChange / 1.5f, 0f, 1f);
        return Mth.lerp(instability, 1.0f, 0.55f);
    }

    public static String getConfidenceLabel(float confidencePercent) {
        if (confidencePercent >= 80f) return "High";
        if (confidencePercent >= 55f) return "Moderate";
        if (confidencePercent >= 30f) return "Low";
        return "Very Low";
    }
}
