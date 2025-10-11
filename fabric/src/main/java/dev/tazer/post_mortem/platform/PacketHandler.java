package dev.tazer.post_mortem.platform;

import dev.tazer.post_mortem.networking.GravePayload;
import dev.tazer.post_mortem.networking.GraveRequestPayload;
import dev.tazer.post_mortem.networking.TeleportToAnchorPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class PacketHandler {
    public static void handleAnchorTeleport(final TeleportToAnchorPayload payload, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();
        GlobalPos location = switch (payload.anchorType()) {
            case DEATH_POINT -> player.getLastDeathLocation().orElse(null);
            case GRAVESTONE -> player.getGrave();
            case SPAWN_POINT -> GlobalPos.of(player.getRespawnDimension(),
                    player.getRespawnPosition() == null ? player.server.getWorldData().overworldData().getSpawnPos() : player.getRespawnPosition()
            );
        };

        if (location != null) {
            ServerLevel level = player.server.getLevel(location.dimension());
            if (level != null) {
                BlockPos pos = location.pos();
                player.teleportTo(level, pos.getX(), pos.getY(), pos.getZ(), player.getYRot(), player.getXRot());
            }
        }
    }

    public static void handleGraveRequest(final GraveRequestPayload payload, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();
        ServerPlayNetworking.send(player, new GravePayload(Optional.ofNullable(player.getGrave())));
    }

    public static void handleGrave(final GravePayload payload, ClientPlayNetworking.Context context) {
        context.player().setGrave(payload.grave().orElse(null));
    }
}
