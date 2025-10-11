package dev.tazer.post_mortem.networking;

import dev.tazer.post_mortem.PostMortem;
import dev.tazer.post_mortem.common.entity.AnchorType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

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
}
