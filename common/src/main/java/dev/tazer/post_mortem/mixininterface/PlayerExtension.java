package dev.tazer.post_mortem.mixininterface;

import dev.tazer.post_mortem.entity.SoulState;
import dev.tazer.post_mortem.entity.SpiritAnchor;
import net.minecraft.core.GlobalPos;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

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
     * @return The spirit's current {@link SpiritAnchor}, or {@code null} if the spirit is not bound <br>
     * The {@link SpiritAnchor} defines the area in which a spirit can move in and interact with the world
     */
    default @Nullable SpiritAnchor getAnchor() {
        throw new AssertionError("Implemented in Mixin");
    }

    /**
     * @param spiritAnchor The {@link SpiritAnchor} to set for the spirit <br>
     * The {@link SpiritAnchor} defines the area in which a spirit can move in and interact with the world
     */
    default void setAnchor(@Nullable SpiritAnchor spiritAnchor) {
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

    /**
     * @return The spirit haunting the player
     */
    default @Nullable UUID getSpirit() {
        throw new AssertionError("Implemented in Mixin");
    }

    default void setSpirit(@Nullable UUID spirit) {
        throw new AssertionError("Implemented in Mixin");
    }
}
