package net.antopfr.advancedweather.client.state;

import net.antopfr.advancedweather.network.toclient.TransitionProbabilitiesPacket;

import java.util.List;

public class ClientTransitionState {

    private static List<TransitionProbabilitiesPacket.Entry> probabilities = List.of();

    public static void updateProbabilities(List<TransitionProbabilitiesPacket.Entry> entries) {
        probabilities = entries;
    }

    public static List<TransitionProbabilitiesPacket.Entry> getProbabilities() {
        return probabilities;
    }

    public static void reset() {
        probabilities = List.of();
    }
}
