package dev.tazer.post_mortem.networking;

import dev.tazer.post_mortem.PostMortem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record GraveRequestPayload() implements CustomPacketPayload {
    public static final Type<GraveRequestPayload> TYPE = new Type<>(PostMortem.location("grave_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, GraveRequestPayload> STREAM_CODEC = StreamCodec.unit(new GraveRequestPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
