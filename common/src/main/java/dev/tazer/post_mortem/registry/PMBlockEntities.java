package dev.tazer.post_mortem.registry;

import dev.tazer.post_mortem.blockentity.HauntedCenserBlockEntity;
import dev.tazer.post_mortem.blockentity.SurgicalAltarBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static dev.tazer.post_mortem.registry.RegistryHandler.registerBlockEntity;

public class PMBlockEntities {
    public static final BlockEntityType<SurgicalAltarBlockEntity> SURGICAL_ALTAR = registerBlockEntity("surgical_altar", () -> BlockEntityType.Builder.of(SurgicalAltarBlockEntity::new, PMBlocks.SURGICAL_ALTAR));
    public static final BlockEntityType<HauntedCenserBlockEntity> HAUNTED_CENSER = registerBlockEntity("haunted_censer", () -> BlockEntityType.Builder.of(HauntedCenserBlockEntity::new, PMBlocks.HAUNTED_CENSER));

    public static void register() {}
}
