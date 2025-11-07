package dev.tazer.post_mortem.registry.worldgen;

import dev.tazer.post_mortem.worldgen.feature.FleshGeodeConfiguration;
import dev.tazer.post_mortem.worldgen.feature.FleshGeodeFeature;

import static dev.tazer.post_mortem.registry.RegistryHandler.registerFeature;

public class PMFeatures {
    public static final FleshGeodeFeature FLESH_GEODE = registerFeature("flesh_geode", new FleshGeodeFeature(FleshGeodeConfiguration.CODEC));

    public static void register() {
    }
}
