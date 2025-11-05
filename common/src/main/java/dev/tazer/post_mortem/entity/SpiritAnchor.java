package dev.tazer.post_mortem.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

/**
 * Contains either a {@link UUID} or a {@link GlobalPos}
 * The present parameter acts as the centre for the haunting area -
 *  the area in which a spirit can move in and interact with the world
 *
 * @param uuid The {@link UUID} of the entity the spirit is anchored to, null if not present
 * @param globalPos The {@link GlobalPos} the spirit is anchored to, null if not present
 */
public record SpiritAnchor(@Nullable UUID uuid, @Nullable GlobalPos globalPos, AnchorType type) {
    public static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);

    public static final Codec<SpiritAnchor> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    UUID_CODEC.optionalFieldOf("uuid", null).forGetter(SpiritAnchor::uuid),
                    GlobalPos.CODEC.optionalFieldOf("globalPos", null).forGetter(SpiritAnchor::globalPos),
                    AnchorType.CODEC.fieldOf("type").forGetter(SpiritAnchor::type)
            ).apply(instance, SpiritAnchor::new)
    );

    public static final StreamCodec<ByteBuf, SpiritAnchor> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(UUID_CODEC),
            SpiritAnchor::uuid,
            GlobalPos.STREAM_CODEC,
            SpiritAnchor::globalPos,
            AnchorType.STREAM_CODEC,
            SpiritAnchor::type,
            SpiritAnchor::new
    );

    /**
     * @param uuid Optional {@link UUID}, the {@code uuid} is set to null if it is not present
     * @param pos Optional {@link GlobalPos}, the {@code globalPos} is set to null if it is not present
     */
    public SpiritAnchor(Optional<UUID> uuid, Optional<GlobalPos> pos, AnchorType type) {
        this(uuid.orElse(null), pos.orElse(null), type);
    }

    /**
     * @param level The level the anchorType is in
     * @return The centre of the anchorType, or null if the anchorType is not in this level
     */
    public GlobalPos getPos(Level level) {
        if (uuid != null) {
            Player player = level.getPlayerByUUID(uuid);
            return player == null ? null : GlobalPos.of(player.level().dimension(), player.blockPosition());
        }

        return globalPos;
    }



    public void validate(ServerPlayer player) {
//        if (uuid == null) {
//            GlobalPos globalPos = anchor.globalPos();
//
//            if (globalPos != null) {
//                ServerLevel dimensionLevel = player.server.getLevel(globalPos.dimension());
//                if (dimensionLevel != null && dimensionLevel.getBlockEntity(globalPos.globalPos()) instanceof AbstractCenserBlockEntity censer && censer.spirit == player.getUUID()) {
//                    dimensionLevel.setBlockAndUpdate(globalPos.globalPos(), censer.getBlockState().setValue(AbstractCenserBlock.LINK_STATE, LinkState.STRONG));
//                }
//            }
//        } else {
//
//        }
    }
}
