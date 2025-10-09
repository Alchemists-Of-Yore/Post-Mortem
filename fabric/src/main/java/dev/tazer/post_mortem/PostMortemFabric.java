package dev.tazer.post_mortem;

import dev.tazer.post_mortem.registry.PMBlocks;
import dev.tazer.post_mortem.registry.PMDataSerializers;
import dev.tazer.post_mortem.registry.PMItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.network.syncher.EntityDataSerializers;

public class PostMortemFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        PostMortem.init();
        PMBlocks.register();
        PMItems.register();
//        PMTabs.register();
        EntityDataSerializers.registerSerializer(PMDataSerializers.SOUL_STATE);
    }
}
