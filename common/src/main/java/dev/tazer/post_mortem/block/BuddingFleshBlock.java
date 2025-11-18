package dev.tazer.post_mortem.block;

import dev.tazer.post_mortem.PMConfig;
import dev.tazer.post_mortem.registry.PMBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BuddingAmethystBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class BuddingFleshBlock extends Block {
    public BuddingFleshBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (PMConfig.softcore && random.nextFloat() < 0.01) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);

            if (BuddingAmethystBlock.canClusterGrowAtState(aboveState)) {
                BlockState lifebudState = PMBlocks.LIFEBUD.defaultBlockState()
                        .setValue(LifebudBlock.ROOTED, true)
                        .setValue(LifebudBlock.WATERLOGGED, Boolean.valueOf(aboveState.getFluidState().getType() == Fluids.WATER));
                level.setBlockAndUpdate(abovePos, lifebudState);
            }
        }
    }
}
