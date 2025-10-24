package dev.tazer.post_mortem.networking;

import dev.tazer.post_mortem.PostMortem;
import dev.tazer.post_mortem.block.GravestoneBlock;
import dev.tazer.post_mortem.entity.AnchorType;
import dev.tazer.post_mortem.entity.SpiritAnchor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public record TeleportToAnchorPayload(AnchorType anchorType) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TeleportToAnchorPayload> TYPE = new CustomPacketPayload.Type<>(PostMortem.location("teleport_to_anchor"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TeleportToAnchorPayload> STREAM_CODEC = StreamCodec.composite(
            AnchorType.STREAM_CODEC,
            TeleportToAnchorPayload::anchorType,
            TeleportToAnchorPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPlayer player) {
        // TODO: change to only teleport if you're not already in range of it

        GlobalPos location = switch (anchorType()) {
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
                Direction direction = player.getDirection();

                BlockState state = level.getBlockState(pos);

                // TODO: display chat message and do not teleport if not valid

                if (anchorType() == AnchorType.GRAVESTONE) {
                    if (state.getBlock() instanceof GravestoneBlock) direction = state.getValue(GravestoneBlock.FACING);
                    else player.setGrave(null);
                }

                if (anchorType() == AnchorType.SPAWN_POINT) {
                    if (state.getBlock() instanceof BedBlock) direction = state.getValue(BedBlock.FACING);
                    else player.setRespawnPosition(Level.OVERWORLD, null, 0, false, false);
                }

                player.setAnchor(new SpiritAnchor(null, location));

                Vec3 vec3 = BedBlock.findStandUpPosition(player.getType(), level, pos, direction, player.getYRot()).orElse(Vec3.atBottomCenterOf(pos));
                player.teleportTo(level, vec3.x, vec3.y, vec3.z, player.getYRot(), player.getXRot());
            }
        }
    }
}
