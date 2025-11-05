package dev.tazer.post_mortem.blockentity;

import dev.tazer.post_mortem.registry.PMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HauntedCenserBlockEntity extends AbstractCenserBlockEntity {
    public HauntedCenserBlockEntity(BlockPos pos, BlockState blockState) {
        super(PMBlockEntities.HAUNTED_CENSER, pos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HauntedCenserBlockEntity censer) {
    }
}
