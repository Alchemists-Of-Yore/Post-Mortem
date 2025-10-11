package dev.tazer.post_mortem.platform;

import dev.tazer.post_mortem.networking.GravePayload;
import dev.tazer.post_mortem.networking.GraveRequestPayload;
import dev.tazer.post_mortem.networking.TeleportToAnchorPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public class PacketHandler {
    public static void handleAnchorTeleport(final TeleportToAnchorPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
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
    }

    public static void handleGraveRequest(final GraveRequestPayload payload, IPayloadContext context) {
        if (context.player() instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new GravePayload(Optional.ofNullable(player.getGrave())));
        }
    }

    public static void handleGrave(final GravePayload payload, IPayloadContext context) {
        context.player().setGrave(payload.grave().orElse(null));
    }
}
