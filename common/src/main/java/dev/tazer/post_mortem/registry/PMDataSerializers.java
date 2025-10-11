package dev.tazer.post_mortem.registry;

import dev.tazer.post_mortem.common.entity.SoulState;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.syncher.EntityDataSerializer;

public class PMDataSerializers {
    public static final EntityDataSerializer<SoulState> SOUL_STATE = EntityDataSerializer.forValueType(SoulState.STREAM_CODEC);
    public static final EntityDataSerializer<GlobalPos> GRAVE = EntityDataSerializer.forValueType(GlobalPos.STREAM_CODEC);
}
