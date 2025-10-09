package dev.tazer.post_mortem.common.entity;

import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface Hauntable {
    boolean isHaunted();

    @Nullable Player getSpectre();

    void kickSpectre();
}
