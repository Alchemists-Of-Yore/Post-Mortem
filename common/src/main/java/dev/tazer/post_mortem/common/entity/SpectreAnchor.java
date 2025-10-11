package dev.tazer.post_mortem.common.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

/**
 * Contains either a {@link UUID} or a {@link GlobalPos}
 * The present parameter acts as the centre for the haunting area -
 *  the area in which a spectre can move in and interact with the world
 *
 * @param uuid The {@link UUID} of the entity the spectre is anchored to, null if not present
 * @param pos The {@link GlobalPos} the spectre is anchored to, null if not present
 */
public record SpectreAnchor(UUID uuid, GlobalPos pos) {
    public static final Codec<SpectreAnchor> CODEC = Codec
            .either(Codec.STRING.xmap(UUID::fromString, UUID::toString), GlobalPos.CODEC)
            .xmap(either -> new SpectreAnchor(either.left(), either.right()), anchor -> {
                if (anchor.uuid == null) {
                    return Either.right(anchor.pos);
                } else return Either.left(anchor.uuid);
            });

    /**
     * @param uuid Optional {@link UUID}, the {@code uuid} is set to null if it is not present
     * @param pos Optional {@link GlobalPos}, the {@code pos} is set to null if it is not present
     */
    public SpectreAnchor(Optional<UUID> uuid, Optional<GlobalPos> pos) {
        this(uuid.orElse(null), pos.orElse(null));
    }

    /**
     * @param level The level the anchorType is in
     * @return The centre of the anchorType, or null if the anchorType is not in this level
     */
    public Vec3 getCentre(ServerLevel level) {
        Entity entity = level.getEntity(uuid);
        if (entity != null) return entity.position();
        else return level.dimension() == pos.dimension() ? Vec3.atCenterOf(pos.pos()) : null;
    }
}
