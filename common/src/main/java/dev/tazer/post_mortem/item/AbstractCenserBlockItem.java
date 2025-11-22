package dev.tazer.post_mortem.item;

import dev.tazer.post_mortem.block.AbstractCenserBlock;
import dev.tazer.post_mortem.blockentity.AbstractCenserBlockEntity;
import dev.tazer.post_mortem.entity.AnchorType;
import dev.tazer.post_mortem.entity.SpiritAnchor;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public abstract class AbstractCenserBlockItem extends BlockItem {
    public <T extends AbstractCenserBlock> AbstractCenserBlockItem(T block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        if (super.placeBlock(context, state)) {
            Player player = context.getPlayer();
            if (player != null) {
                UUID spirit = player.getSpirit();
                Level level = context.getLevel();
                if (spirit != null && level.getBlockEntity(context.getClickedPos()) instanceof AbstractCenserBlockEntity censer) {
                    Player hauntingPlayer = level.getPlayerByUUID(spirit);
                    // TODO: some sort of request system is needed here, to allow changing the anchor of a disconnected player
                    if (hauntingPlayer != null) {
                        hauntingPlayer.setAnchor(new SpiritAnchor(null, GlobalPos.of(level.dimension(), context.getClickedPos()), AnchorType.CENSER));
                    }
                }
            }
            return true;
        }
        return false;
    }
}
