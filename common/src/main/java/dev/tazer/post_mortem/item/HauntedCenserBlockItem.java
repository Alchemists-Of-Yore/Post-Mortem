package dev.tazer.post_mortem.item;

import dev.tazer.post_mortem.block.AbstractCenserBlock;

public class HauntedCenserBlockItem extends AbstractCenserBlockItem {
    public <T extends AbstractCenserBlock> HauntedCenserBlockItem(T block, Properties properties) {
        super(block, properties);
    }
}
