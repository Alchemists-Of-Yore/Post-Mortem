package dev.tazer.post_mortem.networking;

import dev.tazer.post_mortem.PostMortem;
import dev.tazer.post_mortem.entity.AnchorType;
import dev.tazer.post_mortem.entity.SpiritAnchor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

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
        final SpiritAnchor anchor = player.findValidAnchor(anchorType);
        if (anchor == null) player.displayClientMessage(Component.literal("Spirit Anchor not found"), true);
        else player.setAnchor(anchor);
    }
}
