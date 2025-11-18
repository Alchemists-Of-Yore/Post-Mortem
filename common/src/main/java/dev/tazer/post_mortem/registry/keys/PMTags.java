package dev.tazer.post_mortem.registry.keys;

import dev.tazer.post_mortem.PostMortem;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class PMTags {
    // TODO: phasing
    public static final TagKey<Block> SPIRITS_PHASE_THROUGH = blockTag("spirits_phase_through");
    public static final TagKey<Biome> SPIRITS_ROAM_IN = biomeTag("spirits_roam_in");

    public static TagKey<Block> blockTag(String path) {
        return TagKey.create(Registries.BLOCK, PostMortem.location(path));
    }


    public static TagKey<Biome> biomeTag(String path) {
        return TagKey.create(Registries.BIOME, PostMortem.location(path));
    }
}
