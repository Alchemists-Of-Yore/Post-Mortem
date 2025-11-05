package dev.tazer.post_mortem.blockentity;

import dev.tazer.post_mortem.registry.PMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class SurgicalAltarBlockEntity extends AbstractCenserBlockEntity {
    public int timeActive = 0;

    public SurgicalAltarBlockEntity(BlockPos pos, BlockState blockState) {
        super(PMBlockEntities.SURGICAL_ALTAR, pos, blockState);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        timeActive = tag.getInt("TimeActive");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt("TimeActive", timeActive);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, SurgicalAltarBlockEntity altar) {
    }
}
