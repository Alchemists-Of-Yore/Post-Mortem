package dev.tazer.post_mortem.entity;

import dev.tazer.post_mortem.PostMortem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public enum DeadHeartType {
    DOWNED(
            ResourceLocation.withDefaultNamespace("hud/heart/hardcore_full"),
            ResourceLocation.withDefaultNamespace("hud/heart/hardcore_half")
    ),
    MANIFESTATION(
            PostMortem.location("hearts/manifestation_heart_full"),
            PostMortem.location("hearts/manifestation_heart_half")
    ),
    POSSESSION(
            PostMortem.location("hearts/possession_heart_full"),
            PostMortem.location("hearts/possession_heart_half")
    );

    private final ResourceLocation full;
    private final ResourceLocation half;

    DeadHeartType(ResourceLocation full, ResourceLocation half) {
        this.full = full;
        this.half = half;
    }

    public ResourceLocation getSprite(boolean halfHeart, boolean blinking) {
        if (halfHeart) return blinking ? this.half.withSuffix("_blinking") : this.half;
        else return blinking ? this.full.withSuffix("_blinking") : this.full;
    }

    /**
     * @return the {@link DeadHeartType} based on the player's {@link SoulState}.
     * @param player the player for which to determine the HeartType.
     */
    public static DeadHeartType forPlayer(Player player) {
        return switch (player.getSoulState()) {
            case MANIFESTATION -> MANIFESTATION;
            case POSSESSION -> POSSESSION;
            default -> DOWNED;
        };
    }
}
