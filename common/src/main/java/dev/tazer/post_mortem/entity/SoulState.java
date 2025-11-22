package dev.tazer.post_mortem.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;

import java.util.function.IntFunction;

public enum SoulState {
    ALIVE(0, true, true, true, true),
    DOWNED(1, false, false, true, true),
    SPIRIT(2, true, false, false, false),
    MANIFESTATION(3, true, true, true, false),
    POSSESSION(4, false, false, false, false);

    private static final IntFunction<SoulState> BY_ID = ByIdMap.continuous(SoulState::id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, SoulState> STREAM_CODEC = ByteBufCodecs.idMapper(SoulState::byId, SoulState::id);
    private final int id;
    private final boolean canInteract;
    private final boolean canAttack;
    private final boolean canUse;
    private final boolean hasInventory;

    SoulState(int id, boolean canInteract, boolean canAttack, boolean canUse, boolean hasInventory) {
        this.id = id;
        this.canInteract = canInteract;
        this.canAttack = canAttack;
        this.canUse = canUse;
        this.hasInventory = hasInventory;
    }

    public static SoulState byId(int id) {
        return BY_ID.apply(id);
    }

    public int id() {
        return this.id;
    }

    /**
     * @return {@code true} if the player can interact with blocks and entities in this state.
     */
    public boolean canInteract() {
        return canInteract;
    }

    /**
     * @return {@code true} if the player can attack entities in this state.
     */
    public boolean canAttack() {
        return canAttack;
    }

    /**
     * @return {@code true} if the player can use items in this state.
     */
    public boolean canUse() {
        return canUse;
    }

    /**
     * @return {@code true} if the player retains their inventory in this state.
     */
    public boolean hasInventory() {
        return hasInventory;
    }
}
