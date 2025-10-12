package dev.tazer.post_mortem.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum AnchorType {
    GRAVESTONE(0),
    SPAWN_POINT(1),
    DEATH_POINT(2);

    private static final IntFunction<AnchorType> BY_ID = ByIdMap.continuous(AnchorType::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, AnchorType> STREAM_CODEC = ByteBufCodecs.idMapper(AnchorType::byId, AnchorType::id);
    private final int id;

    AnchorType(int id) {
        this.id = id;
    }

    public static AnchorType byId(int id) {
        return BY_ID.apply(id);
    }

    public int id() {
        return this.id;
    }
}
