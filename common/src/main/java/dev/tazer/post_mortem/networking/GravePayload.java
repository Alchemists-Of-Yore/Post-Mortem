package dev.tazer.post_mortem.networking;

import dev.tazer.post_mortem.PostMortem;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Optional;

public record GravePayload(Optional<GlobalPos> grave) implements CustomPacketPayload {
    public static final Type<GravePayload> TYPE = new Type<>(PostMortem.location("grave"));

    public static final StreamCodec<RegistryFriendlyByteBuf, GravePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(GlobalPos.STREAM_CODEC),
            GravePayload::grave,
            GravePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
