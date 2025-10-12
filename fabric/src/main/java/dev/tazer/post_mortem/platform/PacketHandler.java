package dev.tazer.post_mortem.platform;

import dev.tazer.post_mortem.networking.TeleportToAnchorPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class PacketHandler {
    public static void handleAnchorTeleport(final TeleportToAnchorPayload payload, ServerPlayNetworking.Context context) {
        payload.handle(context.player());
    }
}
