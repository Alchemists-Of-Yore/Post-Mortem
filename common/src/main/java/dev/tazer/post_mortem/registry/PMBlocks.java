package dev.tazer.post_mortem.registry;

import dev.tazer.post_mortem.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

import static dev.tazer.post_mortem.registry.RegistryHandler.registerBlock;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;

public class PMBlocks {
    public static final List<Block> BLOCKS = new ArrayList<>();

    public static final GravestoneBlock GRAVESTONE = registerBlock("gravestone", new GravestoneBlock(ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final SurgicalAltarBlock SURGICAL_ALTAR = registerBlock("surgical_altar", new SurgicalAltarBlock(ofFullCopy(Blocks.STONE).noOcclusion()));
    public static final SurgicalAltarPartBlock SURGICAL_ALTAR_PART = registerBlock("surgical_altar_part", new SurgicalAltarPartBlock(ofFullCopy(SURGICAL_ALTAR).noOcclusion()));
    public static final HauntedCenserBlock HAUNTED_CENSER = registerBlock("haunted_censer", new HauntedCenserBlock(
            ofFullCopy(Blocks.LANTERN)
            .lightLevel(state -> state.getValue(HauntedCenserBlock.LINK_STATE) == CenserLinkState.ABSENT ? 0 : state.getValue(HauntedCenserBlock.LINK_STATE) == CenserLinkState.WEAK ? 4 : 7)
    ));

    public static final Block LIFEBUD = registerBlock("lifebud", new Block(ofFullCopy(Blocks.HEAVY_CORE)));
    public static final Block FLESH = registerBlock("flesh", new Block(ofFullCopy(Blocks.SCULK)));
    public static final Block BUDDING_FLESH = registerBlock("budding_flesh", new Block(ofFullCopy(FLESH)));

    public static void register() {}
}
