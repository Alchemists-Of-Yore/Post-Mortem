package dev.tazer.post_mortem.common.entity;

import dev.tazer.post_mortem.registry.PMDataSerializers;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface IPlayerExtension {
     EntityDataAccessor<SoulState> SOUL_STATE = SynchedEntityData.defineId(Player.class, PMDataSerializers.SOUL_STATE);

    /**
     * @return The player's current {@link SoulState}
     */
    default SoulState getSoulState() {
        throw new AssertionError("Implemented in Mixin");
    }

    /**
     * @param soulState The {@link SoulState} to set for the player
     */
    default void setSoulState(SoulState soulState) {
        throw new AssertionError("Implemented in Mixin");
    }

    /**
     * @return The player's current {@link GlobalPos} grave, or {@code null} if no grave is set
     */
    default @Nullable GlobalPos getGrave() {
        throw new AssertionError("Implemented in Mixin");
    }

    /**
     * @param grave The {@link GlobalPos} grave to set for the player
     */
    default void setGrave(@Nullable GlobalPos grave) {
        throw new AssertionError("Implemented in Mixin");
    }
}
