package dev.tazer.post_mortem.registry;

import dev.tazer.post_mortem.common.entity.SoulState;
import net.minecraft.network.syncher.EntityDataSerializer;

public class PMDataSerializers {
    public static final EntityDataSerializer<SoulState> SOUL_STATE = EntityDataSerializer.forValueType(SoulState.STREAM_CODEC);
}
