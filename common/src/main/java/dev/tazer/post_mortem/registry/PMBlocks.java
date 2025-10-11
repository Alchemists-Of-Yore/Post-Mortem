package dev.tazer.post_mortem.registry;

import dev.tazer.post_mortem.common.block.GravestoneBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

import static dev.tazer.post_mortem.registry.RegistryHandler.registerBlock;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;

public class PMBlocks {
    public static final List<Block> BLOCKS = new ArrayList<>();

    public static final GravestoneBlock GRAVESTONE = registerBlock("gravestone", new GravestoneBlock(ofFullCopy(Blocks.STONE).noOcclusion()), false);

    public static void register() {}
}
