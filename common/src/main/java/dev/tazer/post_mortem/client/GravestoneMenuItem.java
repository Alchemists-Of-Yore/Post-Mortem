package dev.tazer.post_mortem.client;

import dev.tazer.post_mortem.PostMortem;
import dev.tazer.post_mortem.common.entity.AnchorType;
import dev.tazer.post_mortem.networking.TeleportToAnchorPayload;
import dev.tazer.post_mortem.platform.Services;
import dev.tazer.post_mortem.registry.PMItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class GravestoneMenuItem implements SpectreMenuItem {
    public final Player player;

    public GravestoneMenuItem() {
        this.player = Minecraft.getInstance().player;
    }

    @Override
    public void selectItem(SpectreMenu menu) {
        Services.PLATFORM.sendToServer(new TeleportToAnchorPayload(AnchorType.GRAVESTONE));
    }

    @Override
    public Component getName() {
        return PostMortem.lang("anchor_type.gravestone");
    }

    @Override
    public void renderIcon(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.renderItem(new ItemStack(PMItems.GRAVESTONE), x, y);
    }

    @Override
    public boolean isEnabled() {
        return player.getGrave() != null;
    }
}
