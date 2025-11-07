package dev.tazer.post_mortem;

import dev.tazer.post_mortem.registry.RegistryHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class PostMortemFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        register();
    }

    public static void register() {
        Set<ResourceLocation> ordered = new LinkedHashSet<>();
        ordered.addAll(Collections.unmodifiableSet(BuiltInRegistries.LOADERS.keySet()));
        ordered.addAll(BuiltInRegistries.REGISTRY.keySet().stream().sorted(ResourceLocation::compareTo).toList());

        for (ResourceLocation rootRegistryName : ordered) {
            ResourceKey<? extends Registry<?>> registryKey = ResourceKey.createRegistryKey(rootRegistryName);
            RegistryHandler.register(registryKey);
        }
    }
}
