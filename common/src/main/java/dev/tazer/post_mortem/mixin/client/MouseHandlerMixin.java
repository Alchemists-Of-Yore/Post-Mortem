package dev.tazer.post_mortem.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.InputConstants;
import dev.tazer.post_mortem.common.entity.SoulState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapOperation(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;swapPaint(D)V"))
    private void scrollSpectreMenu(Inventory instance, double direction, Operation<Void> original, @Local(ordinal = 2) int k) {
        if (minecraft.player.getSoulState() == SoulState.SPECTRE) {
            minecraft.gui.getSpectreGui().onMouseScrolled(-k);
        } else original.call(instance, direction);
    }

    @WrapOperation(method = "onPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;click(Lcom/mojang/blaze3d/platform/InputConstants$Key;)V"))
    private void presssSpectreMenu(InputConstants.Key key, Operation<Void> original, @Local(ordinal = 3) int i) {
        if (minecraft.player.getSoulState() == SoulState.SPECTRE && i == 2) {
            minecraft.gui.getSpectreGui().onMouseMiddleClick();
        } else original.call(key);
    }
}
