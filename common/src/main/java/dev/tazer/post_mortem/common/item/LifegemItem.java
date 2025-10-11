package dev.tazer.post_mortem.common.item;

import dev.tazer.post_mortem.common.entity.SoulState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class LifegemItem extends Item {
    public LifegemItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        SoulState newState = switch (player.getSoulState()) {
            case ALIVE -> SoulState.DOWNED;
            case DOWNED -> SoulState.SPECTRE;
            case SPECTRE -> SoulState.ETHEREAL;
            default -> SoulState.ALIVE;
        };

        System.out.println(newState);

        player.setSoulState(newState);
        return InteractionResultHolder.consume(player.getItemInHand(usedHand));
    }
}
