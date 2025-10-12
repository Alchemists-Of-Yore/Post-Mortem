package dev.tazer.post_mortem.registry;

import dev.tazer.post_mortem.PostMortem;
import net.minecraft.world.item.CreativeModeTab;

import static dev.tazer.post_mortem.registry.RegistryHandler.registerTab;

public class PMTabs {
    public static final CreativeModeTab TAB = registerTab("main", new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 7)
            .title(PostMortem.lang("itemGroup.tab"))
            .icon(PMItems.GRAVESTONE::getDefaultInstance)
            .displayItems((parameters, output) -> PMItems.ITEMS.forEach(output::accept))
            .build()
    );

    public static void register() {}
}
