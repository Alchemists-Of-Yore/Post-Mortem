package dev.tazer.post_mortem;

import dev.tazer.post_mortem.networking.GravePayload;
import dev.tazer.post_mortem.networking.GraveRequestPayload;
import dev.tazer.post_mortem.networking.TeleportToAnchorPayload;
import dev.tazer.post_mortem.platform.PacketHandler;
import dev.tazer.post_mortem.registry.PMBlocks;
import dev.tazer.post_mortem.registry.PMDataSerializers;
import dev.tazer.post_mortem.registry.PMItems;
import dev.tazer.post_mortem.registry.PMTabs;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.syncher.EntityDataSerializers;

public class PostMortemFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        PostMortem.init();
        PMBlocks.register();
        PMItems.register();
        PMTabs.register();
        EntityDataSerializers.registerSerializer(PMDataSerializers.SOUL_STATE);

        PayloadTypeRegistry.playC2S().register(TeleportToAnchorPayload.TYPE, TeleportToAnchorPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(TeleportToAnchorPayload.TYPE, PacketHandler::handleAnchorTeleport);

        PayloadTypeRegistry.playC2S().register(GraveRequestPayload.TYPE, GraveRequestPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(GraveRequestPayload.TYPE, PacketHandler::handleGraveRequest);

        PayloadTypeRegistry.playS2C().register(GravePayload.TYPE, GravePayload.STREAM_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(GravePayload.TYPE, PacketHandler::handleGrave);
    }
}
