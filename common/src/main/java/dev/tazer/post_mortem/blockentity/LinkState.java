package dev.tazer.post_mortem.blockentity;

import net.minecraft.util.StringRepresentable;

public enum LinkState implements StringRepresentable {
    ABSENT("absent"),
    WEAK("weak"),
    STRONG("strong");

    private final String name;

    LinkState(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
