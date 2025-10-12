package dev.tazer.post_mortem.mixininterface;

import dev.tazer.post_mortem.entity.SpiritAnchor;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for entities that can become spirits
 */
public interface Spirit {
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
}
