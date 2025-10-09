package dev.tazer.post_mortem.common.entity;

import net.minecraft.core.GlobalPos;
import org.jetbrains.annotations.Nullable;

public interface IPlayerExtension {

    /**
     * @return The player's current {@link SoulState}
     */
    SoulState getSoulState();

    /**
     * @param soulState The {@link SoulState} to set for the player
     */
    void setSoulState(SoulState soulState);

    /**
     * Sets the player's {@link SoulState} to the next one on death
     * @param soulState The {@link SoulState} to transition to
     */
    void transitionTo(SoulState soulState);

    /**
     * @return The player's current {@link GlobalPos} grave, or {@code null} if no grave is set
     */
    @Nullable GlobalPos getGrave();

    /**
     * @param grave The {@link GlobalPos} grave to set for the player
     */
    void setGrave(@Nullable GlobalPos grave);
}
