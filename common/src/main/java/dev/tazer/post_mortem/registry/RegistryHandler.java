package dev.tazer.post_mortem.registry;


import dev.tazer.post_mortem.PostMortem;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.UnaryOperator;

public class RegistryHandler {
    public static <R, T extends R> T register(String name, T object, Registry<R> rRegistry) {
        Registry.register(rRegistry, PostMortem.location(name), object);
        return object;
    }

    public static <T> DataComponentType<T> registerComponentType(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        return register(name, builder.apply(DataComponentType.builder()).build(), BuiltInRegistries.DATA_COMPONENT_TYPE);
    }

    public static <B extends Block> B registerBlock(String name, B block) {
        return registerBlock(name, block, true);
    }

    public static <B extends Block> B registerBlock(String name, B block, boolean registerItem) {
        B registered = register(name, block, BuiltInRegistries.BLOCK);
        PMBlocks.BLOCKS.add(registered);
        if (registerItem) registerItem(name, new BlockItem(block, new Item.Properties()));
        return registered;
    }

    public static <B extends Item> B registerItem(String name, B item) {
        B registered = register(name, item, BuiltInRegistries.ITEM);
        PMItems.ITEMS.add(registered);
        return registered;
    }

    public static <B extends CreativeModeTab> B registerTab(String name, B tab) {
        return register(name, tab, BuiltInRegistries.CREATIVE_MODE_TAB);
    }

    public static <T extends EntityType<?>> T registerEntityType(String name, T type) {
        return register(name, type, BuiltInRegistries.ENTITY_TYPE);
    }

    public static SoundEvent registerSoundEvent(String name, SoundEvent sound) {
        return register(name, sound, BuiltInRegistries.SOUND_EVENT);
    }

    public static SoundEvent registerSoundEvent(String name) {
        return registerSoundEvent(name, SoundEvent.createVariableRangeEvent(PostMortem.location(name)));
    }

    public static Holder<MobEffect> registerMobEffect(String name, MobEffect effect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, PostMortem.location(name), effect);
    }

    public static void register(ResourceKey<? extends Registry<?>> key) {
        if (key.equals(BuiltInRegistries.BLOCK.key())) {
            PMBlocks.register();
        }

        if (key.equals(BuiltInRegistries.ITEM.key())) {
            PMItems.register();
        }

        if (key.equals(BuiltInRegistries.CREATIVE_MODE_TAB.key())) {
            PMTabs.register();
        }
    }
}
