package dev.tazer.post_mortem.mixininterface;

import dev.tazer.post_mortem.entity.AnchorType;
import dev.tazer.post_mortem.entity.SpiritAnchor;
import org.jetbrains.annotations.Nullable;

public interface ServerPlayerExtension {
    default @Nullable SpiritAnchor findValidAnchor(AnchorType type) {
        throw new AssertionError("Implemented in Mixin");
    }

    default void validateAnchor() {
        throw new AssertionError("Implemented in Mixin");
    }
}
