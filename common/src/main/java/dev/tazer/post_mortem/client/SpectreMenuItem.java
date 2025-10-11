package dev.tazer.post_mortem.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public interface SpectreMenuItem {
    void selectItem(SpectreMenu menu);

    Component getName();

    void renderIcon(GuiGraphics guiGraphics, int x, int y);

    boolean isEnabled();
}
