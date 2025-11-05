package dev.tazer.post_mortem.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tazer.post_mortem.PostMortem;
import dev.tazer.post_mortem.client.SpiritGui;
import dev.tazer.post_mortem.entity.DeadHeartType;
import dev.tazer.post_mortem.entity.SoulState;
import dev.tazer.post_mortem.mixininterface.client.GuiExtension;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Gui.class)
public abstract class GuiMixin implements GuiExtension {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    private RandomSource random;

    @Shadow
    protected abstract void renderHeart(GuiGraphics guiGraphics, Gui.HeartType heartType, int x, int y, boolean hardcore, boolean halfHeart, boolean blinking);

    @Unique
    private SpiritGui spiritGui;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void initSpiritGui(Minecraft minecraft, CallbackInfo ci) {
        spiritGui = new SpiritGui(minecraft);
    }

    @Inject(method = "renderHearts", at = @At(value = "HEAD"), cancellable = true)
    private void maybeRenderHearts(GuiGraphics graphics, Player player, int x, int y, int height, int offsetHeartIndex, float maxHealth, int currentHealth, int displayHealth, int absorptionAmount, boolean renderHighlight, CallbackInfo ci) {
        if (player.getSoulState() != SoulState.ALIVE) {
            renderDeadHearts(graphics, player, x, y, height, offsetHeartIndex, maxHealth, currentHealth, displayHealth, absorptionAmount, renderHighlight);
            ci.cancel();
        }
    }

    @Unique
    private void renderDeadHearts(GuiGraphics graphics, Player player, int x, int y, int height, int offsetHeartIndex, float maxHealth, int currentHealth, int displayHealth, int absorptionAmount, boolean renderHighlight) {
        boolean hardcore = player.level().getLevelData().isHardcore();

        if (player.getSoulState() == SoulState.DOWNED || minecraft.screen instanceof DeathScreen) {
            hardcore = true;
            maxHealth = currentHealth;
            x += 78;
        }

//        x = graphics.guiWidth() / 2 - hearts * 4;

        DeadHeartType heartType = DeadHeartType.forPlayer(player);
        int hearts = Mth.ceil(maxHealth / 2);
        int absorptionHearts = Mth.ceil(absorptionAmount / 2F);
        int halfHearts = hearts * 2;
        if (minecraft.player.jumpableVehicle() == null) y += 5;

        for (int l = hearts + absorptionHearts - 1; l >= 0; l--) {
            int i1 = l / 10;
            int j1 = l % 10;
            int k1 = x + j1 * 8;
            int l1 = y - i1 * height;
            if (currentHealth + absorptionAmount <= 4) {
                l1 += random.nextInt(2);
            }

            if (l < hearts && l == offsetHeartIndex) {
                l1 -= 2;
            }

            renderHeart(graphics, Gui.HeartType.CONTAINER, k1, l1, hardcore, renderHighlight, false);
            int i2 = l * 2;
            boolean flag1 = l >= hearts;
            if (flag1) {
                int j2 = i2 - halfHearts;
                if (j2 < absorptionAmount) {
                    boolean flag2 = j2 + 1 == absorptionAmount;
                    renderHeart(graphics, Gui.HeartType.ABSORBING, k1, l1, hardcore, false, flag2);
                }
            }

            if (renderHighlight && i2 < displayHealth) {
                boolean flag3 = i2 + 1 == displayHealth;
                renderDeadHeart(graphics, heartType, k1, l1, true, flag3);
            }

            if (i2 < currentHealth) {
                boolean flag4 = i2 + 1 == currentHealth;
                renderDeadHeart(graphics, heartType, k1, l1, false, flag4);
            }
        }

    }

    @Unique
    private void renderDeadHeart(GuiGraphics guiGraphics, DeadHeartType heartType, int x, int y, boolean halfHeart, boolean blinking) {
        RenderSystem.enableBlend();
        guiGraphics.blitSprite(heartType.getSprite(blinking, halfHeart), x, y, 9, 9);
        RenderSystem.disableBlend();
    }

    @ModifyArg(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 1))
    private ResourceLocation changeAttackIndicator(ResourceLocation sprite) {
        SoulState soulState = minecraft.player.getSoulState();
        return soulState != SoulState.ALIVE && soulState != SoulState.DOWNED ? PostMortem.location("spirit_attack_indicator") : sprite;
    }

    @ModifyArg(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V", ordinal = 2))
    private ResourceLocation changeAttackIndicatorBackground(ResourceLocation sprite) {
        SoulState soulState = minecraft.player.getSoulState();
        return soulState != SoulState.ALIVE && soulState != SoulState.DOWNED ? PostMortem.location("spirit_attack_indicator_background") : sprite;
    }

    @ModifyArg(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIIIIIII)V"))
    private ResourceLocation changeAttackIndicatorProgress(ResourceLocation sprite) {
        SoulState soulState = minecraft.player.getSoulState();
        return soulState != SoulState.ALIVE && soulState != SoulState.DOWNED ? PostMortem.location("spirit_attack_indicator_progress") : sprite;
    }

    @Inject(method = "renderFood", at = @At(value = "HEAD"), cancellable = true)
    private void maybeRenderFood(GuiGraphics guiGraphics, Player player, int y, int x, CallbackInfo ci) {
        if (player.getSoulState() != SoulState.ALIVE) ci.cancel();
    }

    @Inject(method = "isExperienceBarVisible", at = @At("RETURN"), cancellable = true)
    private void maybeRenderExperienceBar(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && minecraft.player.getSoulState() != SoulState.ALIVE) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "renderItemHotbar", at = @At(value = "HEAD"), cancellable = true)
    private void renderSpiritHotbar(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (minecraft.player.getSoulState() == SoulState.SPIRIT) {
            spiritGui.renderHotbar(graphics, deltaTracker);
            ci.cancel();
        }
    }

    @Inject(method = "renderSelectedItemName", at = @At(value = "HEAD"), cancellable = true)
    private void renderSpiritTooltip(GuiGraphics graphics, CallbackInfo ci) {
        if (minecraft.player.getSoulState() == SoulState.SPIRIT) {
            spiritGui.renderTooltip(graphics);
            ci.cancel();
        }
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
