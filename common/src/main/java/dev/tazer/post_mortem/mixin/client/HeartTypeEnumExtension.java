package dev.tazer.post_mortem.mixin.client;

import dev.tazer.post_mortem.PostMortem;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(Gui.HeartType.class)
public abstract class HeartTypeEnumExtension {

    @Shadow
    @Final
    @Mutable
    private static Gui.HeartType[] $VALUES;

    @Unique
    private static final Gui.HeartType DOWNED = addHeartType("DOWNED", ResourceLocation.withDefaultNamespace("hud/heart/hardcore"));
    @Unique
    private static final Gui.HeartType MANIFESTATION = addHeartType("MANIFESTATION", PostMortem.location("hearts/manifestation_heart"));
    @Unique
    private static final Gui.HeartType POSSESSION = addHeartType("POSSESSION", PostMortem.location("hearts/possession_heart"));

    @Invoker("<init>")
    private static Gui.HeartType invokeInit(String name, int id, ResourceLocation full, ResourceLocation fullBlinking, ResourceLocation half, ResourceLocation halfBlinking, ResourceLocation hardcoreFull, ResourceLocation hardcoreBlinking, ResourceLocation hardcoreHalf, ResourceLocation hardcoreHalfBlinking) {
        throw new AssertionError();
    }

    @Unique
    private static Gui.HeartType addHeartType(String name, ResourceLocation heart) {
        ArrayList<Gui.HeartType> heartTypes = new ArrayList<>(Arrays.asList($VALUES));
        ResourceLocation fullHeart = heart.withSuffix("_full");
        ResourceLocation halfHeart = heart.withSuffix("_half");
        ResourceLocation fullBlinking = fullHeart.withSuffix("_blinking");
        ResourceLocation halfBlinking = halfHeart.withSuffix("_blinking");
        Gui.HeartType heartType = invokeInit(name, heartTypes.getLast().ordinal() + 1, fullHeart, fullBlinking, halfHeart, halfBlinking, fullHeart, fullBlinking, halfHeart, halfBlinking);
        heartTypes.add(heartType);
        $VALUES = heartTypes.toArray(new Gui.HeartType[0]);
        return heartType;
    }

    @Inject(method = "forPlayer", at = @At("HEAD"), cancellable = true)
    private static void forPlayer(Player player, CallbackInfoReturnable<Gui.HeartType> cir) {
        switch (player.getSoulState()) {
            case MANIFESTATION -> cir.setReturnValue(MANIFESTATION);
            case POSSESSION -> cir.setReturnValue(POSSESSION);
            case DOWNED -> cir.setReturnValue(DOWNED);
        }
    }
}
