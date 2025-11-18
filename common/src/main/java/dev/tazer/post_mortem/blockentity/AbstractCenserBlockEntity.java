package dev.tazer.post_mortem.blockentity;

import dev.tazer.post_mortem.block.AbstractCenserBlock;
import dev.tazer.post_mortem.block.CenserLinkState;
import dev.tazer.post_mortem.entity.SoulState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractCenserBlockEntity extends BlockEntity {
    public Optional<UUID> spirit = Optional.empty();

    public AbstractCenserBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if (tag.contains("Spirit")) spirit = Optional.of(tag.getUUID("Spirit"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        spirit.ifPresent(uuid -> tag.putUUID("Spirit", uuid));
    }

    public void removeLink() {
        if (level != null) {
            spirit = Optional.empty();
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(AbstractCenserBlock.LINK_STATE, CenserLinkState.ABSENT));
        }
    }

    public void addLink(Player player) {
        if (level != null) {
            spirit = Optional.of(player.getUUID());
            level.setBlockAndUpdate(worldPosition, getBlockState().setValue(AbstractCenserBlock.LINK_STATE, CenserLinkState.STRONG));
            player.setSoulState(SoulState.MANIFESTATION);
        }
    }
}
