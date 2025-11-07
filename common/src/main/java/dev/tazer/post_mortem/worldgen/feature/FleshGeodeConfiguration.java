package dev.tazer.post_mortem.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.GeodeBlockSettings;
import net.minecraft.world.level.levelgen.GeodeCrackSettings;
import net.minecraft.world.level.levelgen.GeodeLayerSettings;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

// Mostly copied from GeodeConfiguration
public record FleshGeodeConfiguration(GeodeBlockSettings geodeBlockSettings, GeodeLayerSettings geodeLayerSettings,
                                      GeodeCrackSettings geodeCrackSettings,
                                      IntProvider outerWallDistance, IntProvider distributionPoints,
                                      IntProvider pointOffset, int minGenOffset, int maxGenOffset,
                                      double noiseMultiplier, int invalidBlocksThreshold) implements FeatureConfiguration {
    public static final Codec<Double> CHANCE_RANGE = Codec.doubleRange(0.0, 1.0);
    public static final Codec<FleshGeodeConfiguration> CODEC = RecordCodecBuilder.create(
            p_160842_ -> p_160842_.group(
                            GeodeBlockSettings.CODEC.fieldOf("blocks").forGetter(FleshGeodeConfiguration::geodeBlockSettings),
                            GeodeLayerSettings.CODEC.fieldOf("layers").forGetter(FleshGeodeConfiguration::geodeLayerSettings),
                            GeodeCrackSettings.CODEC.fieldOf("crack").forGetter(FleshGeodeConfiguration::geodeCrackSettings),
                            IntProvider.codec(1, 20).fieldOf("outer_wall_distance").orElse(UniformInt.of(4, 5)).forGetter(FleshGeodeConfiguration::outerWallDistance),
                            IntProvider.codec(1, 20).fieldOf("distribution_points").orElse(UniformInt.of(3, 4)).forGetter(FleshGeodeConfiguration::distributionPoints),
                            IntProvider.codec(0, 10).fieldOf("point_offset").orElse(UniformInt.of(1, 2)).forGetter(FleshGeodeConfiguration::pointOffset),
                            Codec.INT.fieldOf("min_gen_offset").orElse(-16).forGetter(FleshGeodeConfiguration::minGenOffset),
                            Codec.INT.fieldOf("max_gen_offset").orElse(16).forGetter(FleshGeodeConfiguration::maxGenOffset),
                            CHANCE_RANGE.fieldOf("noise_multiplier").orElse(0.05).forGetter(FleshGeodeConfiguration::noiseMultiplier),
                            Codec.INT.fieldOf("invalid_blocks_threshold").forGetter(FleshGeodeConfiguration::invalidBlocksThreshold)
                    )
                    .apply(p_160842_, FleshGeodeConfiguration::new)
    );

}
