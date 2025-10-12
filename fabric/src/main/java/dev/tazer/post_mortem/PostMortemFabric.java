package dev.tazer.post_mortem;

import dev.tazer.post_mortem.networking.TeleportToAnchorPayload;
import dev.tazer.post_mortem.platform.PacketHandler;
import dev.tazer.post_mortem.registry.PMBlocks;
import dev.tazer.post_mortem.registry.PMItems;
import dev.tazer.post_mortem.registry.PMTabs;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class PostMortemFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        PostMortem.init();
        PMBlocks.register();
        PMItems.register();
        PMTabs.register();

        PayloadTypeRegistry.playC2S().register(TeleportToAnchorPayload.TYPE, TeleportToAnchorPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(TeleportToAnchorPayload.TYPE, PacketHandler::handleAnchorTeleport);
    }
}
