package dev.tazer.post_mortem.registry;

import dev.tazer.post_mortem.common.block.GravestoneBlockItem;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

import static dev.tazer.post_mortem.registry.RegistryHandler.registerItem;
import static net.minecraft.world.item.Item.Properties;

public class PMItems {
    public static final List<Item> ITEMS = new ArrayList<>();

    public static final GravestoneBlockItem GRAVESTONE = registerItem("gravestone", new GravestoneBlockItem(PMBlocks.GRAVESTONE, new Properties()));

    public static void register() {}
}
