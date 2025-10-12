package dev.tazer.post_mortem.platform;

import dev.tazer.post_mortem.networking.TeleportToAnchorPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PacketHandler {
    public static void handleAnchorTeleport(final TeleportToAnchorPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) payload.handle(player);
    }
}
