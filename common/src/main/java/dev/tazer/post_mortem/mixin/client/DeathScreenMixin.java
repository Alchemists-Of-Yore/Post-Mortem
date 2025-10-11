package dev.tazer.post_mortem.mixin.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DeathScreen.class)
public abstract class DeathScreenMixin extends ScreenMixin {
    // TODO: background gets gray scale over time
    // TODO: title is enlarged(?) gets redder over time

    @Shadow
    @Nullable
    private Button exitToTitleButton;

    @Shadow
    @Final
    private Component causeOfDeath;

    @Shadow
    @Final
    private static ResourceLocation DRAFT_REPORT_SPRITE;

    @Shadow
    @Final
    private List<Button> exitButtons;

    @Shadow
    protected abstract void setButtonsActive(boolean active);

    @Shadow
    protected abstract void handleExitToTitleScreen();

    @Shadow
    private int delayTicker;

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void init(CallbackInfo ci) {
        delayTicker = 0;
        exitButtons.clear();
        exitButtons.add(addRenderableWidget(Button.builder(Component.translatable("deathScreen.respawn"),
                button -> minecraft.player.respawn())
                .bounds(width / 2 - 100, height / 2 + 72, 200, 20)
                .build()
        ));
        exitToTitleButton = addRenderableWidget(Button.builder(Component.translatable("deathScreen.titleScreen"),
                button -> minecraft.getReportingContext().draftReportHandled(minecraft,  (DeathScreen) (Object) this, this::handleExitToTitleScreen, true))
                .bounds(width / 2 - 100, height / 2 + 96, 200, 20)
                .build()
        );
        exitButtons.add(exitToTitleButton);
        setButtonsActive(false);
        ci.cancel();
    }
    
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", shift = At.Shift.AFTER), cancellable = true)
    private void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(3, 3, 3);
        guiGraphics.drawCenteredString(font, title, width / 2 / 3, 30, 16777215);
        guiGraphics.pose().popPose();
        if (causeOfDeath != null) {
            guiGraphics.drawCenteredString(font, causeOfDeath, width / 2, 150, 16777215);
        }
        
        if (exitToTitleButton != null && minecraft.getReportingContext().hasDraftReport()) {
            guiGraphics.blitSprite(DRAFT_REPORT_SPRITE, exitToTitleButton.getX() + exitToTitleButton.getWidth() - 17, exitToTitleButton.getY() + 3, 15, 15);
        }
        
        ci.cancel();
    }
}
