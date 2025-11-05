package dev.tazer.post_mortem.item;

import dev.tazer.post_mortem.block.AbstractCenserBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractCenserBlockItem extends BlockItem {
    public <T extends AbstractCenserBlock> AbstractCenserBlockItem(T block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {

        return super.placeBlock(context, state);
    }
}
