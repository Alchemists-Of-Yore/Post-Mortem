package dev.tazer.post_mortem.common.entity;

import org.jetbrains.annotations.Nullable;

/**
 * Interface for entities that can become spectres
 */
public interface Spectre {
    /**
     * @return The spectre's current {@link SpectreAnchor}, or {@code null} if the spectre is not bound <br>
     * The {@link SpectreAnchor} defines the area in which a spectre can move in and interact with the world
     */
    default @Nullable SpectreAnchor getAnchor() {
        throw new AssertionError("Implemented in Mixin");
    }

    /**
     * @param spectreAnchor The {@link SpectreAnchor} to set for the spectre <br>
     * The {@link SpectreAnchor} defines the area in which a spectre can move in and interact with the world
     */
    default void setAnchor(@Nullable SpectreAnchor spectreAnchor) {
        throw new AssertionError("Implemented in Mixin");
    }
}
