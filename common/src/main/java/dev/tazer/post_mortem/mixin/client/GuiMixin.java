package dev.tazer.post_mortem.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tazer.post_mortem.client.IGuiExtension;
import dev.tazer.post_mortem.client.SpectreGui;
import dev.tazer.post_mortem.common.entity.SoulState;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Gui.class)
public class GuiMixin implements IGuiExtension {

    // TODO: fix downed hearts
    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private SpectreGui spectreGui;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initSpectreGui(Minecraft minecraft, CallbackInfo ci) {
        spectreGui = new SpectreGui(minecraft);
    }

    @ModifyVariable(method = "renderHearts", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private int moveHeartsIfDowned(int value) {
        if (minecraft.player.getSoulState() == SoulState.DOWNED || minecraft.screen instanceof DeathScreen) return value + 78;
        return value;
    }

    @ModifyVariable(method = "renderHearts", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float reduceHeartsIfDowned(float value) {
        if (minecraft.player.getSoulState() == SoulState.DOWNED || minecraft.screen instanceof DeathScreen) return 6;
        return value;
    }

    @ModifyVariable(method = "renderHearts", at = @At(value = "STORE", ordinal = 0), ordinal = 1)
    private boolean hardcoreHeartsIfDowned(boolean original) {
        return minecraft.player.getSoulState() == SoulState.DOWNED || minecraft.screen instanceof DeathScreen;
    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void cancelRenderFoodIfDowned(GuiGraphics guiGraphics, Player player, int y, int x, CallbackInfo ci) {
        if (player.getSoulState() != SoulState.ALIVE) ci.cancel();
    }

    @Inject(method = "isExperienceBarVisible", at = @At("RETURN"), cancellable = true)
    private void cancelRenderExperienceBarIfDowned(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && minecraft.player.getSoulState() != SoulState.ALIVE) {
            cir.setReturnValue(false);
        }
    }

    @WrapOperation(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderItemHotbar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
    private void renderSpectreHotbar(Gui instance, GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
        if (minecraft.player.getSoulState() == SoulState.SPECTRE) {
            spectreGui.renderHotbar(guiGraphics, deltaTracker);
        } else original.call(instance, guiGraphics, deltaTracker);
    }

    @WrapOperation(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private void renderSpectreTooltip(Gui instance, GuiGraphics guiGraphics, Operation<Void> original) {
        if (minecraft.player.getSoulState() == SoulState.SPECTRE) {
            spectreGui.renderTooltip(guiGraphics);
        } else original.call(instance, guiGraphics);
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void tickSpectreMenu(CallbackInfo ci) {
        if (minecraft.player != null) {
            if (minecraft.player.getSoulState() == SoulState.SPECTRE) {
                spectreGui.tick();
            }
        }
    }

    @Override
    public SpectreGui getSpectreGui() {
        return spectreGui;
    }
}
