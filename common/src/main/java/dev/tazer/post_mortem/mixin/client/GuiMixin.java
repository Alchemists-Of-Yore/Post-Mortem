package dev.tazer.post_mortem.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tazer.post_mortem.client.SpiritGui;
import dev.tazer.post_mortem.entity.SoulState;
import dev.tazer.post_mortem.mixininterface.client.GuiExtension;
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
public class GuiMixin implements GuiExtension {

    // TODO: render completely different set of hearts for manifestation (and downed?)

    @Shadow
    @Final
    private Minecraft minecraft;

    @Unique
    private SpiritGui spiritGui;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initSpiritGui(Minecraft minecraft, CallbackInfo ci) {
        spiritGui = new SpiritGui(minecraft);
    }

    @ModifyVariable(method = "renderHearts", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private int moveHeartsWhenDowned(int value) {
        if (minecraft.player.getSoulState() == SoulState.DOWNED || minecraft.screen instanceof DeathScreen) return value + 78;
        return value;
    }

    @ModifyVariable(method = "renderHearts", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float reduceHeartsWhenDowned(float value) {
        if (minecraft.player.getSoulState() == SoulState.DOWNED || minecraft.screen instanceof DeathScreen) return 6;
        return value;
    }

    @ModifyVariable(method = "renderHearts", at = @At(value = "STORE", ordinal = 0), ordinal = 1)
    private boolean hardcoreHeartsWhenDowned(boolean original) {
        return minecraft.player.getSoulState() == SoulState.DOWNED || minecraft.screen instanceof DeathScreen;
    }

    @Inject(method = "renderFood", at = @At("HEAD"), cancellable = true)
    private void cancelRenderFoodWhenDowned(GuiGraphics guiGraphics, Player player, int y, int x, CallbackInfo ci) {
        if (player.getSoulState() != SoulState.ALIVE) ci.cancel();
    }

    @Inject(method = "isExperienceBarVisible", at = @At("RETURN"), cancellable = true)
    private void cancelRenderExperienceBarWhenDead(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && minecraft.player.getSoulState() != SoulState.ALIVE) {
            cir.setReturnValue(false);
        }
    }

    @WrapOperation(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderItemHotbar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
    private void renderSpiritHotbar(Gui instance, GuiGraphics guiGraphics, DeltaTracker deltaTracker, Operation<Void> original) {
        if (minecraft.player.getSoulState() == SoulState.SPIRIT) {
            spiritGui.renderHotbar(guiGraphics, deltaTracker);
        } else original.call(instance, guiGraphics, deltaTracker);
    }

    @WrapOperation(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private void renderSpiritTooltip(Gui instance, GuiGraphics guiGraphics, Operation<Void> original) {
        if (minecraft.player.getSoulState() == SoulState.SPIRIT) {
            spiritGui.renderTooltip(guiGraphics);
        } else original.call(instance, guiGraphics);
    }

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void tickSpiritGui(CallbackInfo ci) {
        if (minecraft.player != null) {
            if (minecraft.player.getSoulState() == SoulState.SPIRIT) {
                spiritGui.tick();
            }
        }
    }

    @Override
    public SpiritGui getSpiritGui() {
        return spiritGui;
    }
}
