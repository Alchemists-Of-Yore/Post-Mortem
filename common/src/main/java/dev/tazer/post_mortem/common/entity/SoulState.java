package dev.tazer.post_mortem.common.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum SoulState {
    ALIVE(0),
    DOWNED(1),
    SPECTRE(2),
    ETHEREAL(3),
    POSSESSING(4);

    private static final IntFunction<SoulState> BY_ID = ByIdMap.continuous(SoulState::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, SoulState> STREAM_CODEC = ByteBufCodecs.idMapper(SoulState::byId, SoulState::id);
    private final int id;

    SoulState(int id) {
        this.id = id;
    }

    public static SoulState byId(int id) {
        return BY_ID.apply(id);
    }

    public int id() {
        return this.id;
    }
}
