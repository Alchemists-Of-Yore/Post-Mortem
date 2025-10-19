package dev.tazer.post_mortem.registry;

import dev.tazer.post_mortem.item.GravestoneBlockItem;
import dev.tazer.post_mortem.item.LifegemItem;
import dev.tazer.post_mortem.item.SurgicalAltarBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.ArrayList;
import java.util.List;

import static dev.tazer.post_mortem.registry.RegistryHandler.registerItem;
import static net.minecraft.world.item.Item.Properties;

public class PMItems {
    public static final List<Item> ITEMS = new ArrayList<>();

    public static final GravestoneBlockItem GRAVESTONE = registerItem("gravestone", new GravestoneBlockItem(PMBlocks.GRAVESTONE, new Properties()));
    public static final SurgicalAltarBlockItem SURGICAL_ALTAR = registerItem("surgical_altar", new SurgicalAltarBlockItem(PMBlocks.SURGICAL_ALTAR, new Properties()));

    public static final LifegemItem LIFEGEM = registerItem("lifegem", new LifegemItem(new Properties().rarity(Rarity.EPIC)));

    public static final Item LIFESALT = registerItem("lifesalt", new Item(new Properties().rarity(Rarity.UNCOMMON)));

    public static void register() {}
}
