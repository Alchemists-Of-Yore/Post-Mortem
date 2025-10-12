package dev.tazer.post_mortem.entity;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface Hauntable {
    boolean isHaunted();

    @Nullable Player getSpirit();

    void kickSpirit();
}
