package dev.tazer.post_mortem.mixin.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DeathScreen.class)
public class DeathScreenMixin {
    // TODO: background gets gray scale over time
    // TODO: title is enlarged(?) gets redder over time
    // TODO: move both buttons down
    // TODO: enlarge(?) death message and move to centre
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V", ordinal = 2))
    private void cancelDeathScore(GuiGraphics instance, Font font, Component text, int x, int y, int color) {}
}
