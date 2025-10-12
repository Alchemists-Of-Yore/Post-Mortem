package dev.tazer.post_mortem.mixininterface;

import dev.tazer.post_mortem.entity.SoulState;
import net.minecraft.core.GlobalPos;
import org.jetbrains.annotations.Nullable;

public interface PlayerExtension {

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
