package dev.tazer.post_mortem.mixin.client;

import dev.tazer.post_mortem.common.entity.IPlayerExtension;
import dev.tazer.post_mortem.common.entity.PlayerUtil;
import dev.tazer.post_mortem.common.entity.SoulState;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Gui.class)
public class GuiMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @ModifyVariable(method = "renderHearts", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private int moveHeartsIfDowned(int value) {
        if (((IPlayerExtension) minecraft.player).getSoulState() == SoulState.DOWNED) return value + 78;
        return value;
    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void cancelRenderFoodIfDowned(GuiGraphics guiGraphics, Player player, int y, int x, CallbackInfo ci) {
        if (((IPlayerExtension) player).getSoulState() != SoulState.ALIVE) ci.cancel();
    }

    @Inject(method = "isExperienceBarVisible", at = @At("RETURN"), cancellable = true)
    private void cancelRenderExperienceBarIfDowned(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && ((IPlayerExtension) minecraft.player).getSoulState() != SoulState.ALIVE) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "renderHotbarAndDecorations", at = @At("HEAD"))
    private void addDownedBar(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Player player = minecraft.player;
        if (((IPlayerExtension) player).getSoulState() == SoulState.DOWNED) {
            PlayerUtil.renderDownedBar(guiGraphics, deltaTracker, player);
        }
    }
}
