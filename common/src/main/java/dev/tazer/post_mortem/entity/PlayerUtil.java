package dev.tazer.post_mortem.entity;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class PlayerUtil {
    public static void resetAttributes(Player player) {
        AttributeMap attributeMap = player.getAttributes();

        List<Holder<Attribute>> attributes = List.of(
                Attributes.MAX_HEALTH,
                Attributes.MOVEMENT_SPEED,
                Attributes.ENTITY_INTERACTION_RANGE,
                Attributes.BLOCK_INTERACTION_RANGE
        );

        for(Holder<Attribute> attribute : attributes) {
            AttributeInstance attributeinstance = attributeMap.getInstance(attribute);
            if (attributeinstance != null) {
                attributeinstance.removeModifiers();
            }
        }
    }
}
