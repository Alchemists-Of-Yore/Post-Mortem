package dev.tazer.post_mortem.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import dev.tazer.post_mortem.mixin.LevelInvoker;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

/**
 * Contains either a {@link UUID} or a {@link GlobalPos}
 * The present parameter acts as the centre for the haunting area -
 *  the area in which a spirit can move in and interact with the world
 *
 * @param uuid The {@link UUID} of the entity the spirit is anchored to, null if not present
 * @param pos The {@link GlobalPos} the spirit is anchored to, null if not present
 */
public record SpiritAnchor(UUID uuid, GlobalPos pos) {
    public static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);

    public static final Codec<SpiritAnchor> CODEC = Codec
            .either(UUID_CODEC, GlobalPos.CODEC)
            .xmap(either -> new SpiritAnchor(either.left(), either.right()), anchor -> {
                if (anchor.uuid == null) {
                    return Either.right(anchor.pos);
                } else return Either.left(anchor.uuid);
            });

    public static final StreamCodec<ByteBuf, SpiritAnchor> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(UUID_CODEC),
            SpiritAnchor::uuid,
            GlobalPos.STREAM_CODEC,
            SpiritAnchor::pos,
            SpiritAnchor::new
    );

    /**
     * @param uuid Optional {@link UUID}, the {@code uuid} is set to null if it is not present
     * @param pos Optional {@link GlobalPos}, the {@code pos} is set to null if it is not present
     */
    public SpiritAnchor(Optional<UUID> uuid, Optional<GlobalPos> pos) {
        this(uuid.orElse(null), pos.orElse(null));
    }

    /**
     * @param level The level the anchorType is in
     * @return The centre of the anchorType, or null if the anchorType is not in this level
     */
    public Vec3 getCentre(Level level) {
        Entity entity = ((LevelInvoker) level).getEntities().get(uuid);
        if (entity != null) return entity.position();
        else return level.dimension() == pos.dimension() ? Vec3.atCenterOf(pos.pos()) : null;
    }
}
