package dev.tazer.post_mortem.client;

import dev.tazer.post_mortem.PostMortem;
import dev.tazer.post_mortem.common.entity.AnchorType;
import dev.tazer.post_mortem.networking.TeleportToAnchorPayload;
import dev.tazer.post_mortem.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DeathPointMenuItem implements SpectreMenuItem {
    public final Player player;

    public DeathPointMenuItem() {
        this.player = Minecraft.getInstance().player;
    }

    @Override
    public void selectItem(SpectreMenu menu) {
        Services.PLATFORM.sendToServer(new TeleportToAnchorPayload(AnchorType.DEATH_POINT));
    }

    @Override
    public Component getName() {
        return PostMortem.lang("anchor_type.death_point");
    }

    @Override
    public void renderIcon(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.renderItem(new ItemStack(Items.SKELETON_SKULL), x, y);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
